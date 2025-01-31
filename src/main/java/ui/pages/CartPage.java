package ui.pages;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import models.Order;
import models.OrderItem;
import models.Product;
import sqlbdd.OrderSQL;
import sqlbdd.OrderItemSQL;
import sqlbdd.ProductSQL;
import tools.CartUtils;
import tools.NotificationUtils;
import tools.SessionManager;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class CartPage {
    private BorderPane layout;

    public CartPage() {
        layout = new BorderPane();
        populateCartContent();
    }

    public BorderPane getView() {
        return layout;
    }

    private void populateCartContent() {
        int userId = SessionManager.getCurrentUser().getId();
        OrderSQL orderDAO = new OrderSQL();
        OrderItemSQL orderItemDAO = new OrderItemSQL();

        try {
            Order inProgressOrder = orderDAO.getInProgressOrder(userId);
            if (inProgressOrder == null) {
                layout.setCenter(new Label("Your cart is empty."));
                return;
            }

            List<OrderItem> orderItems = orderItemDAO.getOrderItems(inProgressOrder.getOrderId());

            VBox cartItemsBox = new VBox(10);
            cartItemsBox.setPadding(new Insets(10));
            cartItemsBox.setAlignment(Pos.TOP_LEFT);

            Label cartTitle = new Label("Current Cart Content");
            cartTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
            cartItemsBox.getChildren().add(cartTitle);

            for (OrderItem item : orderItems) {
                HBox itemRow = createCartItemRow(item, orderItemDAO, orderDAO);
                cartItemsBox.getChildren().add(itemRow);
            }

            ScrollPane scrollPane = new ScrollPane(cartItemsBox);
            scrollPane.setFitToWidth(true);
            scrollPane.setFitToHeight(true); 
            layout.setLeft(scrollPane);

            VBox totalBox = createTotalSummaryBox(inProgressOrder, orderItemDAO);
            layout.setRight(totalBox);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private HBox createCartItemRow(OrderItem item, OrderItemSQL orderItemDAO, OrderSQL orderDAO) throws SQLException {
        HBox itemRow = new HBox(15);
        itemRow.setAlignment(Pos.CENTER_LEFT);
        itemRow.setPadding(new Insets(10));
        itemRow.setStyle("-fx-border-color: black; -fx-border-width: 1;");

        ProductSQL productDAO = new ProductSQL();
        Product product = productDAO.getProductById(item.getProductId());

        if (product == null) {
            throw new SQLException("Product not found for item with ID: " + item.getOrderItemId());
        }

        ImageView productImage = new ImageView(new Image(getClass().getResourceAsStream("/images/products/" + product.getImage())));
        productImage.setFitWidth(100);
        productImage.setPreserveRatio(true);

        VBox productInfoBox = new VBox(5);
        productInfoBox.setAlignment(Pos.TOP_LEFT);

        Label productName = new Label(product.getName());
        productName.setStyle("-fx-font-weight: bold;");

        Label description = new Label(product.getDescription());
        description.setWrapText(true);
        description.setMaxWidth(200);

        Label volume = new Label(product.getVolumePerBottle() + "L");
        volume.setStyle("-fx-text-fill: gray; -fx-font-size: 12px;");

        productInfoBox.getChildren().addAll(productName, description, volume);

        VBox quantityBox = new VBox(3);
        quantityBox.setAlignment(Pos.CENTER);

        Label quantityLabel = new Label("Quantity:");
        Spinner<Integer> quantitySpinner = new Spinner<>();
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, product.getStockQuantity(), item.getQuantity());
        quantitySpinner.setValueFactory(valueFactory);
        quantitySpinner.setMaxWidth(70);

        Button updateQuantityButton = new Button();
        ImageView editIcon = new ImageView(new Image(getClass().getResourceAsStream("/images/logo/edit.png")));
        editIcon.setFitWidth(20);
        editIcon.setFitHeight(20);
        updateQuantityButton.setGraphic(editIcon);

        updateQuantityButton.setOnAction(e -> {
            int newQuantity = quantitySpinner.getValue();
            try {
                CartUtils.addToCart(SessionManager.getCurrentUser().getId(), product, newQuantity);
                populateCartContent();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        quantityBox.getChildren().addAll(quantityLabel, quantitySpinner, updateQuantityButton);

        VBox subtotalBox = new VBox(3);
        subtotalBox.setAlignment(Pos.CENTER);

        Label subtotalText = new Label("Subtotal:");
        Label subtotalPrice = new Label(item.getSubtotalPrice() + " €");
        subtotalBox.getChildren().addAll(subtotalText, subtotalPrice);

        Button deleteButton = new Button();
        ImageView binIcon = new ImageView(new Image(getClass().getResourceAsStream("/images/logo/bin.png")));
        binIcon.setFitWidth(20);
        binIcon.setFitHeight(20);
        deleteButton.setGraphic(binIcon);

        deleteButton.setOnAction(e -> {
            try {
                orderItemDAO.deleteOrderItem(item.getOrderItemId());
                NotificationUtils.showNotification(SessionManager.getMainLayout().getRootPane(), "Item removed from cart!", true);
                populateCartContent();
            } catch (SQLException ex) {
                ex.printStackTrace();
                NotificationUtils.showNotification(SessionManager.getMainLayout().getRootPane(), "Failed to remove item from cart!", false);
            }
        });

        itemRow.getChildren().addAll(productImage, productInfoBox, quantityBox, subtotalBox, deleteButton);
        return itemRow;
    }

    private VBox createTotalSummaryBox(Order inProgressOrder, OrderItemSQL orderItemDAO) throws SQLException {
        VBox totalBox = new VBox(10);
        totalBox.setAlignment(Pos.TOP_RIGHT);
        totalBox.setPadding(new Insets(20));

        BigDecimal totalAmount = orderItemDAO.getOrderItems(inProgressOrder.getOrderId())
                .stream()
                .map(OrderItem::getSubtotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Label totalLabel = new Label("Total: " + totalAmount + " €");
        totalLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Button validateButton = new Button("Validate and Pay");
        validateButton.setStyle("-fx-font-size: 16px; -fx-background-color: green; -fx-text-fill: white;");
        validateButton.setOnAction(e -> {
            try {
                SessionManager.getMainLayout().setContent(new OrderValidationPage(inProgressOrder).getView());
            } catch (Exception ex) {
                ex.printStackTrace();
                NotificationUtils.showNotification(SessionManager.getMainLayout().getRootPane(), "An error occurred while redirecting to validation page!", false);
            }
        });

        totalBox.getChildren().addAll(totalLabel, validateButton);
        return totalBox;
    }
}
