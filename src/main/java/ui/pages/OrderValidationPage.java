package ui.pages;

import java.math.BigDecimal;
import java.sql.SQLException;

import dao.InvoiceDAO;
import dao.OrderDAO;
import dao.OrderItemDAO;
import dao.UserDAO;
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
import tools.CartUtils;
import tools.NotificationUtils;
import tools.SessionManager;

public class OrderValidationPage {
    private BorderPane layout;

    public OrderValidationPage(Order order) {
        layout = new BorderPane();

        VBox contentBox = new VBox(20);
        contentBox.setPadding(new Insets(20));
        contentBox.setAlignment(Pos.TOP_CENTER);

        // Récupérer l'utilisateur connecté
        User currentUser = SessionManager.getCurrentUser();

        // Adresse : Tout sur une ligne
        Label addressLabel = new Label("Delivery Address:");
        HBox addressFieldsBox = new HBox(10); // Alignement horizontal pour les champs
        addressFieldsBox.setAlignment(Pos.CENTER_LEFT);

        // Champs d'adresse
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

        // Ajouter les champs d'adresse à la boîte horizontale
        addressFieldsBox.getChildren().addAll(streetField, cityField, postalCodeField, countryField);

        // Méthode de paiement
        Label paymentLabel = new Label("Select Payment Method:");
        ToggleGroup paymentGroup = new ToggleGroup();
        RadioButton cardOption = new RadioButton("Credit Card");
        RadioButton paypalOption = new RadioButton("PayPal");
        cardOption.setToggleGroup(paymentGroup);
        paypalOption.setToggleGroup(paymentGroup);
        cardOption.setSelected(true); // Option par défaut

        VBox paymentBox = new VBox(10, paymentLabel, cardOption, paypalOption);

        // Boutons : Valider la commande et retour au panier
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);

        // Bouton "Retour au panier"
        Button backToCartButton = new Button("Back to Cart");
        backToCartButton.setStyle("-fx-background-color: gray; -fx-text-fill: white;");
        backToCartButton.setOnAction(e -> {
            SessionManager.getMainLayout().setContent(new CartPage().getView());
        });

        // Bouton "Valider la commande"
        Button validateButton = new Button("Validate Order");
        validateButton.setStyle("-fx-background-color: green; -fx-text-fill: white;");
        validateButton.setOnAction(e -> validateOrder(order, streetField.getText(), cityField.getText(), postalCodeField.getText(), countryField.getText(), paymentGroup));

        buttonBox.getChildren().addAll(backToCartButton, validateButton);

        // Ajouter tous les éléments au VBox principal
        contentBox.getChildren().addAll(
            addressLabel,
            addressFieldsBox,
            paymentBox,
            buttonBox
        );

        layout.setCenter(contentBox);
    }

    public BorderPane getView() {
        return layout;
    }

    private void validateOrder(Order order, String street, String city, String postalCode, String countryField, ToggleGroup paymentGroup) {
        if (street.isEmpty() || city.isEmpty() || postalCode.isEmpty() || countryField.isEmpty()) {
            NotificationUtils.showNotification(SessionManager.getMainLayout().getRootPane(), "Please complete all address fields.", false);
            return;
        }

        String selectedPaymentMethod = ((RadioButton) paymentGroup.getSelectedToggle()).getText();

        try {
            // Vérifier la disponibilité des produits
            boolean isCartValid = CartUtils.checkProductAvailability(order);

            if (!isCartValid) {
                NotificationUtils.showNotification(SessionManager.getMainLayout().getRootPane(),
                        "Some products are unavailable. Please review your cart.", false);
                SessionManager.getMainLayout().setContent(new CartPage().getView());
                return;
            }

            // Mettre à jour l'adresse de l'utilisateur
            User currentUser = SessionManager.getCurrentUser();
            currentUser.setAddress(street);
            currentUser.setCity(city);
            currentUser.setPostalCode(postalCode);
            currentUser.setCountry(countryField);

            UserDAO userDAO = new UserDAO();
            userDAO.updateUser(currentUser);

            // Passer la commande en statut "validated"
            order.setStatus("validated");
            new OrderDAO().updateOrderStatus(order.getOrderId(), "validated");

            // Générer la facture
            BigDecimal totalAmount = new OrderItemDAO().getOrderItems(order.getOrderId())
                    .stream()
                    .map(OrderItem::getSubtotalPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            new InvoiceDAO().generateInvoice(order.getOrderId(), totalAmount);

            // Réinitialiser le compteur du panier
            SessionManager.getMainLayout().updateCartBadge(0);

            // Afficher la notification de succès
            NotificationUtils.showNotification(SessionManager.getMainLayout().getRootPane(),
                    "Order validated and invoice generated successfully!", true);

            // Rediriger vers la page de récapitulatif des commandes
            SessionManager.getMainLayout().setContent(new OrderHistoryPage().getView());
        } catch (SQLException ex) {
            ex.printStackTrace();
            NotificationUtils.showNotification(SessionManager.getMainLayout().getRootPane(),
                    "An error occurred while validating the order.", false);
        }
    }
}
