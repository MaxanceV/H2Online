package ui.pages;

import java.sql.SQLException;
import java.util.List;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import models.Order;
import sqlbdd.InvoiceSQL;
import sqlbdd.OrderSQL;
import tools.SessionManager;

/**
 * Displays the order history for the currently logged-in user,
 * allowing them to view the status of past orders and download invoices.
 */
public class OrderHistoryPage {
    private BorderPane layout;

    /**
     * Constructs the {@code OrderHistoryPage} by fetching the user's orders and
     * displaying them in a scrollable layout.
     */
    public OrderHistoryPage() {
        layout = new BorderPane();

        // VBox that contains the list of orders
        VBox contentBox = new VBox(20);
        contentBox.setPadding(new Insets(20));
        contentBox.setAlignment(Pos.TOP_CENTER);

        Label titleLabel = new Label("Order History");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        contentBox.getChildren().add(titleLabel);

        try {
            List<Order> orders = new OrderSQL().getOrdersByUser(SessionManager.getCurrentUser().getId());
            for (Order order : orders) {
                VBox orderBox = new VBox(10);
                orderBox.setPadding(new Insets(10));
                orderBox.setStyle("-fx-border-color: black; -fx-border-width: 1; -fx-background-color: #f9f9f9;");

                Label orderLabel = new Label("Order ID: " + order.getOrderId() +
                        " | Date: " + order.getOrderDate() +
                        " | Total: " + order.getTotalPrice() + " €" +
                        " | Status: ");
                orderLabel.setStyle("-fx-text-fill: black; -fx-font-size: 14px;");

                Label statusLabel = new Label(order.getStatus());
                if ("delivered".equalsIgnoreCase(order.getStatus())) {
                    statusLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                } else {
                    statusLabel.setStyle("-fx-text-fill: black;");
                }

                Button viewInvoiceButton = new Button("View Invoice");
                viewInvoiceButton.setOnAction(e -> {
                    new InvoiceSQL().downloadInvoice(order.getOrderId());
                });

                orderBox.getChildren().addAll(orderLabel, statusLabel, viewInvoiceButton);
                contentBox.getChildren().add(orderBox);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // A scroll pane to allow scrolling if there are many orders
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(contentBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background: transparent;");

        layout.setCenter(scrollPane);
    }

    /**
     * Retrieves the main layout of the order history page.
     *
     * @return A {@link BorderPane} containing the order history.
     */
    public BorderPane getView() {
        return layout;
    }
}
