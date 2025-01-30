package ui.pages;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import models.Order;
import models.User;
import sqlbdd.InvoiceSQL;
import sqlbdd.OrderSQL;
import sqlbdd.UserSQL;
import tools.SessionManager;

import java.sql.SQLException;
import java.util.List;

public class ManageOrdersPage {
    private BorderPane layout;
    private TableView<OrderRow> tableView;

    public ManageOrdersPage() {
        layout = new BorderPane();

        Label titleLabel = new Label("Manage Orders");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        tableView = new TableView<>();
        tableView.setEditable(true); 

        VBox topBox = new VBox(10);
        topBox.setPadding(new Insets(10));
        topBox.getChildren().addAll(titleLabel, createSearchBar());

        layout.setTop(topBox);
        layout.setCenter(createTable());
    }

    public BorderPane getView() {
        return layout;
    }

    private HBox createSearchBar() {
        HBox searchBox = new HBox(10);
        searchBox.setPadding(new Insets(5));

        Label searchLabel = new Label("Search:");
        TextField searchField = new TextField();
        searchField.setPromptText("Search by any field...");

        Button searchButton = new Button("Search");
        searchButton.setOnAction(e -> {
            String filter = searchField.getText().toLowerCase();
            FilteredList<OrderRow> filteredList = new FilteredList<>(tableView.getItems(), order -> {
                if (filter.isEmpty()) return true;
                return order.matchesFilter(filter);
            });
            tableView.setItems(filteredList);
        });

        Button resetButton = new Button("Reset");
        resetButton.setOnAction(e -> {
            searchField.clear();
            loadOrders();
        });

        searchBox.getChildren().addAll(searchLabel, searchField, searchButton, resetButton);
        return searchBox;
    }

    private TableView<OrderRow> createTable() {
        TableColumn<OrderRow, String> idColumn = createColumn("Order ID", OrderRow::idProperty);
        TableColumn<OrderRow, String> userColumn = createColumn("User", OrderRow::userInfoProperty);
        TableColumn<OrderRow, String> statusColumn = createEditableStatusColumn("Status", OrderRow::statusProperty);
        TableColumn<OrderRow, String> totalColumn = createColumn("Total (€)", OrderRow::totalProperty);

        TableColumn<OrderRow, Void> actionsColumn = new TableColumn<>("Actions");
        actionsColumn.setCellFactory(param -> new TableCell<OrderRow, Void>() {
            private final Button viewDetailsButton = new Button("Details");
            private final Button downloadInvoiceButton = new Button("Invoice");

            {
                viewDetailsButton.setStyle("-fx-background-color: blue; -fx-text-fill: white;");
                downloadInvoiceButton.setStyle("-fx-background-color: green; -fx-text-fill: white;");

                viewDetailsButton.setOnAction(e -> {
                    OrderRow orderRow = getTableView().getItems().get(getIndex());
                    viewOrderDetails(orderRow);
                });

                downloadInvoiceButton.setOnAction(e -> {
                    OrderRow orderRow = getTableView().getItems().get(getIndex());
                    if (orderRow.getStatus().equalsIgnoreCase("validated") ||
                            orderRow.getStatus().equalsIgnoreCase("delivered")) {
                        downloadInvoice(orderRow.getId());
                    } else {
                        showError("Invoice can only be downloaded for validated or delivered orders.");
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttonsBox = new HBox(10, viewDetailsButton, downloadInvoiceButton);
                    setGraphic(buttonsBox);
                }
            }
        });

        tableView.getColumns().addAll(idColumn, userColumn, statusColumn, totalColumn, actionsColumn);

        loadOrders();

        return tableView;
    }

    private TableColumn<OrderRow, String> createColumn(String title, OrderPropertyGetter propertyGetter) {
        TableColumn<OrderRow, String> column = new TableColumn<>(title);
        column.setCellValueFactory(cellData -> propertyGetter.get(cellData.getValue()));
        return column;
    }

    private TableColumn<OrderRow, String> createEditableStatusColumn(String title, OrderPropertyGetter propertyGetter) {
        TableColumn<OrderRow, String> column = new TableColumn<>(title);
        column.setCellValueFactory(cellData -> propertyGetter.get(cellData.getValue()));
        column.setCellFactory(ComboBoxTableCell.forTableColumn("in progress", "validated", "delivered"));

        column.setOnEditCommit(event -> {
            OrderRow orderRow = event.getRowValue();
            String newStatus = event.getNewValue();
            orderRow.setStatus(newStatus);
            updateOrderStatus(orderRow);
        });

        return column;
    }

    private void loadOrders() {
        OrderSQL orderDAO = new OrderSQL();
        UserSQL userDAO = new UserSQL();

        try {
            List<Order> orders = orderDAO.getAllOrders();
            List<OrderRow> orderRows = orders.stream().map(order -> {
                try {
                    User user = userDAO.getUserById(order.getUserId());
                    return new OrderRow(order, user);
                } catch (SQLException e) {
                    e.printStackTrace();
                    return null;
                }
            }).toList();

            tableView.setItems(FXCollections.observableArrayList(orderRows));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateOrderStatus(OrderRow orderRow) {
        OrderSQL orderDAO = new OrderSQL();
        try {
            orderDAO.updateOrderStatus(orderRow.getId(), orderRow.getStatus());
            showSuccess("Order status updated successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Failed to update order status.");
        }
    }

    private void viewOrderDetails(OrderRow orderRow) {
        try {
            OrderSQL orderDAO = new OrderSQL();
            Order order = orderDAO.getOrderById(orderRow.getId());

            if (order != null) {
                SessionManager.getMainLayout().setContent(new OrderDetailsPage(order).getView());
            } else {
                showError("Order not found. Please try again.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Failed to retrieve order details. Please try again.");
        }
    }

    private void downloadInvoice(int orderId) {
        InvoiceSQL invoiceDAO = new InvoiceSQL();
        invoiceDAO.downloadInvoice(orderId);
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FunctionalInterface
    private interface OrderPropertyGetter {
        StringProperty get(OrderRow orderRow);
    }

    public static class OrderRow {
        private final StringProperty id;
        private final StringProperty userInfo;
        private final StringProperty status;
        private final StringProperty total;

        public OrderRow(Order order, User user) {
            this.id = new SimpleStringProperty(String.valueOf(order.getOrderId()));
            this.userInfo = new SimpleStringProperty(user.getFirstName() + " " + user.getLastName() + " (" + user.getEmail() + ")");
            this.status = new SimpleStringProperty(order.getStatus());
            this.total = new SimpleStringProperty(order.getTotalPrice().toString());
        }

        public StringProperty idProperty() {
            return id;
        }

        public StringProperty userInfoProperty() {
            return userInfo;
        }

        public StringProperty statusProperty() {
            return status;
        }

        public StringProperty totalProperty() {
            return total;
        }

        public int getId() {
            return Integer.parseInt(id.get());
        }

        public String getStatus() {
            return status.get();
        }

        public void setStatus(String status) {
            this.status.set(status);
        }

        public String getUserInfo() {
            return userInfo.get();
        }

        public boolean matchesFilter(String filter) {
            filter = filter.toLowerCase();
            return safeContains(id.get(), filter) ||
                   safeContains(userInfo.get(), filter) ||
                   safeContains(status.get(), filter) ||
                   safeContains(total.get(), filter);
        }

        private boolean safeContains(String value, String filter) {
            return value != null && value.toLowerCase().contains(filter);
        }
    }
}
