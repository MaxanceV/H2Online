package ui.pages;

import dao.OrderDAO;
import dao.OrderItemDAO;
import dao.ProductDAO;
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
        populateCartContent(); // Ajouter le contenu initial
    }

    public BorderPane getView() {
        return layout;
    }

    private void populateCartContent() {
        int userId = SessionManager.getCurrentUser().getId();
        OrderDAO orderDAO = new OrderDAO();
        OrderItemDAO orderItemDAO = new OrderItemDAO();

        try {
            // Obtenir la commande "in progress"
            Order inProgressOrder = orderDAO.getInProgressOrder(userId);
            if (inProgressOrder == null) {
                layout.setCenter(new Label("Your cart is empty."));
                return;
            }

            List<OrderItem> orderItems = orderItemDAO.getOrderItems(inProgressOrder.getOrderId());

            // Partie gauche : Liste des articles du panier
            VBox cartItemsBox = new VBox(10);
            cartItemsBox.setPadding(new Insets(10));
            cartItemsBox.setAlignment(Pos.TOP_LEFT);

            Label cartTitle = new Label("Current Cart Contents");
            cartTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
            cartItemsBox.getChildren().add(cartTitle);

            for (OrderItem item : orderItems) {
                HBox itemRow = createCartItemRow(item, orderItemDAO, orderDAO);
                cartItemsBox.getChildren().add(itemRow);
            }

            ScrollPane scrollPane = new ScrollPane(cartItemsBox);
            scrollPane.setFitToWidth(true);
            scrollPane.setFitToHeight(true); // Activer le défilement si nécessaire
            layout.setLeft(scrollPane);

            // Partie droite : Résumé total et bouton de validation
            VBox totalBox = createTotalSummaryBox(inProgressOrder, orderItemDAO);
            layout.setRight(totalBox);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private HBox createCartItemRow(OrderItem item, OrderItemDAO orderItemDAO, OrderDAO orderDAO) throws SQLException {
        HBox itemRow = new HBox(15);
        itemRow.setAlignment(Pos.CENTER_LEFT);
        itemRow.setPadding(new Insets(10));
        itemRow.setStyle("-fx-border-color: black; -fx-border-width: 1;");

        // Récupérer les détails du produit
        ProductDAO productDAO = new ProductDAO();
        Product product = productDAO.getProductById(item.getProductId());

        if (product == null) {
            throw new SQLException("Product not found for item with ID: " + item.getOrderItemId());
        }

        // Image du produit
        ImageView productImage = new ImageView(new Image(getClass().getResourceAsStream("/images/products/" + product.getImage())));
        productImage.setFitWidth(100);
        productImage.setPreserveRatio(true);

        // Informations sur le produit
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

        // Sélecteur de quantité
        VBox quantityBox = new VBox(3);
        quantityBox.setAlignment(Pos.CENTER);

        Label quantityLabel = new Label("Quantity:");
        Spinner<Integer> quantitySpinner = new Spinner<>();
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, product.getStockQuantity(), item.getQuantity());
        quantitySpinner.setValueFactory(valueFactory);
        quantitySpinner.setMaxWidth(70); // Réduire la largeur du spinner

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

        // Sous-total
        VBox subtotalBox = new VBox(3);
        subtotalBox.setAlignment(Pos.CENTER);

        Label subtotalText = new Label("Subtotal:");
        Label subtotalPrice = new Label(item.getSubtotalPrice() + " €");
        subtotalBox.getChildren().addAll(subtotalText, subtotalPrice);

        // Bouton de suppression
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

    private VBox createTotalSummaryBox(Order inProgressOrder, OrderItemDAO orderItemDAO) throws SQLException {
        VBox totalBox = new VBox(10);
        totalBox.setAlignment(Pos.TOP_RIGHT);
        totalBox.setPadding(new Insets(20));

        // Montant total
        BigDecimal totalAmount = orderItemDAO.getOrderItems(inProgressOrder.getOrderId())
                .stream()
                .map(OrderItem::getSubtotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Label totalLabel = new Label("Total: " + totalAmount + " €");
        totalLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Bouton de validation et paiement
        Button validateButton = new Button("Validate and Pay");
        validateButton.setStyle("-fx-font-size: 16px; -fx-background-color: green; -fx-text-fill: white;");
        validateButton.setOnAction(e -> {
            try {
                // Rediriger vers la nouvelle page OrderValidationPage
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
