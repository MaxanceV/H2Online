package ui.pages;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import models.Order;
import models.OrderItem;
import models.Product;
import models.User;
import sqlbdd.OrderItemSQL;
import sqlbdd.ProductSQL;
import sqlbdd.UserSQL;
import tools.SessionManager;

public class OrderDetailsPage {
    private BorderPane layout;
    private Order order;

    public OrderDetailsPage(Order order) {
        this.order = order;
        layout = new BorderPane();

        Label titleLabel = new Label("Order Details - Order ID: " + order.getOrderId());
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        VBox topBox = new VBox(10);
        topBox.setPadding(new Insets(10));
        topBox.getChildren().addAll(titleLabel, createCustomerInfoSection());

        layout.setTop(topBox);
        layout.setCenter(createOrderItemsTable());
        layout.setBottom(createFooterButtons());
    }

    public BorderPane getView() {
        return layout;
    }

    private VBox createCustomerInfoSection() {
        VBox customerInfoBox = new VBox(10);
        customerInfoBox.setPadding(new Insets(10));
        customerInfoBox.setStyle("-fx-border-color: lightgray; -fx-border-width: 1; -fx-padding: 10;");

        try {
            UserSQL userDAO = new UserSQL();
            User user = userDAO.getUserById(order.getUserId());

            Label customerNameLabel = new Label("Customer: " + user.getFirstName() + " " + user.getLastName());
            Label emailLabel = new Label("Email: " + user.getEmail());
            Label phoneLabel = new Label("Phone: " + (user.getPhoneNumber() != null ? user.getPhoneNumber() : "N/A"));
            Label addressLabel = new Label("Address: " + user.getAddress() + ", " + user.getCity() + ", " +
                    user.getPostalCode() + ", " + user.getCountry());
            Label statusLabel = new Label("Status: " + order.getStatus());
            Label totalLabel = new Label("Total Amount: €" + order.getTotalPrice());

            customerInfoBox.getChildren().addAll(
                    customerNameLabel, emailLabel, phoneLabel, addressLabel, statusLabel, totalLabel
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return customerInfoBox;
    }

    private TableView<OrderItemRow> createOrderItemsTable() {
        TableView<OrderItemRow> tableView = new TableView<>();

        TableColumn<OrderItemRow, String> productColumn = new TableColumn<>("Product Name");
        productColumn.setCellValueFactory(cellData -> cellData.getValue().productNameProperty());

        TableColumn<OrderItemRow, String> quantityColumn = new TableColumn<>("Quantity");
        quantityColumn.setCellValueFactory(cellData -> cellData.getValue().quantityProperty());

        TableColumn<OrderItemRow, String> unitPriceColumn = new TableColumn<>("Unit Price (€)");
        unitPriceColumn.setCellValueFactory(cellData -> cellData.getValue().unitPriceProperty());

        TableColumn<OrderItemRow, String> subtotalColumn = new TableColumn<>("Subtotal (€)");
        subtotalColumn.setCellValueFactory(cellData -> cellData.getValue().subtotalPriceProperty());

        tableView.getColumns().addAll(productColumn, quantityColumn, unitPriceColumn, subtotalColumn);

        loadOrderItems(tableView);

        return tableView;
    }

    private void loadOrderItems(TableView<OrderItemRow> tableView) {
        try {
            OrderItemSQL orderItemDAO = new OrderItemSQL();
            List<OrderItem> orderItems = orderItemDAO.getOrderItems(order.getOrderId());
            ProductSQL productDAO = new ProductSQL();

            List<OrderItemRow> rows = orderItems.stream().map(orderItem -> {
                try {
                    Product product = productDAO.getProductById(orderItem.getProductId());
                    return new OrderItemRow(product.getName(), orderItem.getQuantity(),
                            orderItem.getUnitPrice(), orderItem.getSubtotalPrice());
                } catch (SQLException e) {
                    e.printStackTrace();
                    return null;
                }
            }).toList();

            tableView.getItems().setAll(rows);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private HBox createFooterButtons() {
        HBox footerBox = new HBox(10);
        footerBox.setAlignment(Pos.CENTER);
        footerBox.setPadding(new Insets(10));

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> {
            SessionManager.getMainLayout().setContent((new ManageOrdersPage().getView()));
        });

        footerBox.getChildren().add(backButton);
        return footerBox;
    }

    public static class OrderItemRow {
        private final StringProperty productName;
        private final StringProperty quantity;
        private final StringProperty unitPrice;
        private final StringProperty subtotalPrice;

        public OrderItemRow(String productName, int quantity, BigDecimal unitPrice, BigDecimal subtotalPrice) {
            this.productName = new SimpleStringProperty(productName);
            this.quantity = new SimpleStringProperty(String.valueOf(quantity));
            this.unitPrice = new SimpleStringProperty(unitPrice.toString());
            this.subtotalPrice = new SimpleStringProperty(subtotalPrice.toString());
        }

        public StringProperty productNameProperty() {
            return productName;
        }

        public StringProperty quantityProperty() {
            return quantity;
        }

        public StringProperty unitPriceProperty() {
            return unitPrice;
        }

        public StringProperty subtotalPriceProperty() {
            return subtotalPrice;
        }
    }
}
