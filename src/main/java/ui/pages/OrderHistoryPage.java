package ui.pages;

import java.sql.SQLException;
import java.util.List;

import dao.InvoiceDAO;
import dao.OrderDAO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import models.Order;
import tools.SessionManager;

public class OrderHistoryPage {
    private BorderPane layout;

    public OrderHistoryPage() {
        layout = new BorderPane();

        VBox contentBox = new VBox(20);
        contentBox.setPadding(new Insets(20));
        contentBox.setAlignment(Pos.TOP_CENTER);

        Label titleLabel = new Label("Order History");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        contentBox.getChildren().add(titleLabel);

        try {
            // Récupérer les commandes de l'utilisateur
            List<Order> orders = new OrderDAO().getOrdersByUser(SessionManager.getCurrentUser().getId());
            for (Order order : orders) {
                VBox orderBox = new VBox(10);
                orderBox.setPadding(new Insets(10));
                orderBox.setStyle("-fx-border-color: black; -fx-border-width: 1;");

                Label orderLabel = new Label("Order ID: " + order.getOrderId() +
                        " | Date: " + order.getOrderDate() +
                        " | Total: " + order.getTotalPrice() + " €" +
                        " | Status: ");
                Label statusLabel = new Label(order.getStatus());
                
                // Appliquer le style vert gras si le statut est "delivered"
                if ("delivered".equalsIgnoreCase(order.getStatus())) {
                    statusLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                } else {
                    statusLabel.setStyle("-fx-text-fill: black;"); // Style par défaut pour les autres statuts
                }

                Button viewInvoiceButton = new Button("View Invoice");
                viewInvoiceButton.setOnAction(e -> {
                    // Afficher ou télécharger la facture
                    new InvoiceDAO().downloadInvoice(order.getOrderId());
                });

                orderBox.getChildren().addAll(orderLabel, statusLabel, viewInvoiceButton);
                contentBox.getChildren().add(orderBox);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        layout.setCenter(contentBox);
    }

    public BorderPane getView() {
        return layout;
    }
}
