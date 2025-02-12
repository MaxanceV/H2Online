package ui.pages;

import java.math.BigDecimal;
import java.sql.SQLException;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import models.Order;
import models.OrderItem;
import models.User;
import sqlbdd.InvoiceSQL;
import sqlbdd.OrderSQL;
import sqlbdd.OrderItemSQL;
import sqlbdd.UserSQL;
import tools.CartUtils;
import tools.NotificationUtils;
import tools.SessionManager;

/**
 * Represents the order validation page, where users can confirm their address
 * details, choose a payment method, and validate an order. If products are not
 * fully available, the order is adjusted before confirmation.
 */
public class OrderValidationPage {
    private BorderPane layout;

    /**
     * Constructs an {@code OrderValidationPage} for the specified {@link Order}.
     * Allows the user to update address fields, choose a payment method, and
     * validate the order.
     *
     * @param order The order to be validated.
     */
    public OrderValidationPage(Order order) {
        layout = new BorderPane();

        VBox contentBox = new VBox(20);
        contentBox.setPadding(new Insets(20));
        contentBox.setAlignment(Pos.TOP_CENTER);

        User currentUser = SessionManager.getCurrentUser();

        Label addressLabel = new Label("Delivery Address:");
        HBox addressFieldsBox = new HBox(10);
        addressFieldsBox.setAlignment(Pos.CENTER_LEFT);

        TextField streetField = new TextField();
        streetField.setPromptText("Street and Number");
        if (currentUser != null && currentUser.getAddress() != null && !currentUser.getAddress().isEmpty()) {
            streetField.setText(currentUser.getAddress());
        }

        TextField cityField = new TextField();
        cityField.setPromptText("City");
        if (currentUser != null && currentUser.getCity() != null && !currentUser.getCity().isEmpty()) {
            cityField.setText(currentUser.getCity());
        }

        TextField postalCodeField = new TextField();
        postalCodeField.setPromptText("Postal Code");
        if (currentUser != null && currentUser.getPostalCode() != null && !currentUser.getPostalCode().isEmpty()) {
            postalCodeField.setText(currentUser.getPostalCode());
        }

        TextField countryField = new TextField();
        countryField.setPromptText("Country");
        if (currentUser != null && currentUser.getCountry() != null && !currentUser.getCountry().isEmpty()) {
            countryField.setText(currentUser.getCountry());
        }

        addressFieldsBox.getChildren().addAll(streetField, cityField, postalCodeField, countryField);

        Label paymentLabel = new Label("Select Payment Method:");
        ToggleGroup paymentGroup = new ToggleGroup();
        RadioButton cardOption = new RadioButton("Credit Card");
        RadioButton paypalOption = new RadioButton("PayPal");
        cardOption.setToggleGroup(paymentGroup);
        paypalOption.setToggleGroup(paymentGroup);
        cardOption.setSelected(true);

        VBox paymentBox = new VBox(10, paymentLabel, cardOption, paypalOption);

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);

        Button backToCartButton = new Button("Back to Cart");
        backToCartButton.setStyle("-fx-background-color: gray; -fx-text-fill: white;");
        backToCartButton.setOnAction(e -> SessionManager.getMainLayout().setContent(new CartPage().getView()));

        Button validateButton = new Button("Validate Order");
        validateButton.setStyle("-fx-background-color: green; -fx-text-fill: white;");
        validateButton.setOnAction(e -> validateOrder(
                order,
                streetField.getText(),
                cityField.getText(),
                postalCodeField.getText(),
                countryField.getText(),
                paymentGroup
        ));

        buttonBox.getChildren().addAll(backToCartButton, validateButton);

        contentBox.getChildren().addAll(
            addressLabel,
            addressFieldsBox,
            paymentBox,
            buttonBox
        );

        layout.setCenter(contentBox);
    }

    /**
     * Retrieves the {@link BorderPane} containing the order validation layout.
     *
     * @return The root layout for this page.
     */
    public BorderPane getView() {
        return layout;
    }

    /**
     * Validates the order by checking product availability, updating user address
     * details in the database, marking the order status as validated, generating
     * an invoice, and clearing the cart badge. If there is insufficient stock for
     * any product, the user is notified and the cart page is displayed again.
     *
     * @param order The order to validate.
     * @param street The street address for delivery.
     * @param city The city for delivery.
     * @param postalCode The postal code for delivery.
     * @param countryField The country for delivery.
     * @param paymentGroup The {@link ToggleGroup} containing the selected payment method.
     */
    private void validateOrder(
            Order order,
            String street,
            String city,
            String postalCode,
            String countryField,
            ToggleGroup paymentGroup
    ) {
        if (street.isEmpty() || city.isEmpty() || postalCode.isEmpty() || countryField.isEmpty()) {
            NotificationUtils.showNotification(
                    SessionManager.getMainLayout().getRootPane(),
                    "Please complete all address fields.",
                    false
            );
            return;
        }

        String selectedPaymentMethod = ((RadioButton) paymentGroup.getSelectedToggle()).getText();

        try {
            boolean isCartValid = CartUtils.checkProductAvailability(order);
            if (!isCartValid) {
                NotificationUtils.showNotification(
                        SessionManager.getMainLayout().getRootPane(),
                        "Some products are unavailable. Please review your cart.",
                        false
                );
                SessionManager.getMainLayout().setContent(new CartPage().getView());
                return;
            }

            User currentUser = SessionManager.getCurrentUser();
            currentUser.setAddress(street);
            currentUser.setCity(city);
            currentUser.setPostalCode(postalCode);
            currentUser.setCountry(countryField);

            UserSQL userDAO = new UserSQL();
            userDAO.updateUser(currentUser);

            order.setStatus("validated");
            new OrderSQL().updateOrderStatus(order.getOrderId(), "validated");

            BigDecimal totalAmount = new OrderItemSQL().getOrderItems(order.getOrderId())
                    .stream()
                    .map(OrderItem::getSubtotalPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            new InvoiceSQL().generateInvoice(order.getOrderId(), totalAmount);

            SessionManager.getMainLayout().updateCartBadge(0);

            NotificationUtils.showNotification(
                    SessionManager.getMainLayout().getRootPane(),
                    "Order validated and invoice generated successfully!",
                    true
            );

            SessionManager.getMainLayout().setContent(new OrderHistoryPage().getView());
        } catch (SQLException ex) {
            ex.printStackTrace();
            NotificationUtils.showNotification(
                    SessionManager.getMainLayout().getRootPane(),
                    "An error occurred while validating the order.",
                    false
            );
        }
    }
}
