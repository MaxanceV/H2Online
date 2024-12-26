package ui.pages;

import dao.ProductDAO;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.Product;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class ManageProductsPage {
    private BorderPane layout;
    private TableView<ProductRow> tableView;

    public ManageProductsPage() {
        layout = new BorderPane();

        Label titleLabel = new Label("Manage Products");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        tableView = new TableView<>();
        tableView.setEditable(true); // Enable table editing

        VBox topBox = new VBox(10);
        topBox.setPadding(new Insets(10));
        topBox.getChildren().addAll(createSearchBar(), createAddButton());

        layout.setTop(topBox);
        layout.setCenter(createTable());

        // Add a Save Changes button
        Button saveChangesButton = new Button("Save Changes");
        saveChangesButton.setOnAction(e -> saveChanges());
        saveChangesButton.setStyle("-fx-background-color: green; -fx-text-fill: white; -fx-font-size: 14px;");

        layout.setBottom(saveChangesButton);
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
            FilteredList<ProductRow> filteredList = new FilteredList<>(tableView.getItems(), product -> {
                if (filter.isEmpty()) return true;
                return product.matchesFilter(filter);
            });
            tableView.setItems(filteredList);
        });

        Button resetButton = new Button("Reset");
        resetButton.setOnAction(e -> {
            searchField.clear();
            loadProducts();
        });

        searchBox.getChildren().addAll(searchLabel, searchField, searchButton, resetButton);
        return searchBox;
    }

    private HBox createAddButton() {
        HBox addButtonBox = new HBox();
        addButtonBox.setPadding(new Insets(5));

        Button addButton = new Button("Add Product");
        addButton.setStyle("-fx-background-color: blue; -fx-text-fill: white;");
        addButton.setOnAction(e -> openAddProductDialog());

        addButtonBox.getChildren().add(addButton);
        return addButtonBox;
    }

    private TableView<ProductRow> createTable() {
        TableColumn<ProductRow, String> idColumn = createEditableColumn("ID", ProductRow::idProperty, false);
        TableColumn<ProductRow, String> nameColumn = createEditableColumn("Name", ProductRow::nameProperty, true);
        TableColumn<ProductRow, String> volumeColumn = createEditableColumn("Volume", ProductRow::volumeProperty, true);
        TableColumn<ProductRow, String> descriptionColumn = createEditableColumn("Description", ProductRow::descriptionProperty, true);
        TableColumn<ProductRow, String> imageColumn = createEditableColumn("Image", ProductRow::imageProperty, true);
        TableColumn<ProductRow, String> priceColumn = createEditableColumn("Price (€)", ProductRow::priceProperty, true);
        TableColumn<ProductRow, String> stockColumn = createEditableColumn("Stock", ProductRow::stockProperty, true);

        // Action column for Delete
        TableColumn<ProductRow, Void> actionColumn = new TableColumn<>("Actions");
        actionColumn.setCellFactory(param -> new TableCell<ProductRow, Void>() {
            private final Button deleteButton = new Button("Delete");

            {
                deleteButton.setStyle("-fx-background-color: red; -fx-text-fill: white;");
                deleteButton.setOnAction(e -> {
                    ProductRow productRow = getTableView().getItems().get(getIndex());
                    deleteProduct(productRow);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteButton);
                }
            }
        });

        tableView.getColumns().addAll(
                idColumn, nameColumn, volumeColumn, descriptionColumn, imageColumn,
                priceColumn, stockColumn, actionColumn
        );

        loadProducts();

        return tableView;
    }

    private TableColumn<ProductRow, String> createEditableColumn(String title, ProductPropertyGetter propertyGetter, boolean editable) {
        TableColumn<ProductRow, String> column = new TableColumn<>(title);
        column.setCellValueFactory(cellData -> propertyGetter.get(cellData.getValue()));

        if (editable) {
            column.setCellFactory(TextFieldTableCell.forTableColumn());
            column.setOnEditCommit(event -> {
                ProductRow productRow = event.getRowValue();
                String newValue = event.getNewValue();
                propertyGetter.get(productRow).set(newValue != null ? newValue : "");
            });
        }

        return column;
    }

    private void loadProducts() {
        ProductDAO productDAO = new ProductDAO();
        try {
            List<Product> products = productDAO.getAllProducts();
            List<ProductRow> productRows = products.stream().map(ProductRow::new).toList();
            tableView.setItems(FXCollections.observableArrayList(productRows));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void saveChanges() {
        ProductDAO productDAO = new ProductDAO();
        try {
            for (ProductRow productRow : tableView.getItems()) {
                Product product = productRow.toProduct();
                productDAO.updateProduct(product);
            }
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(null);
            alert.setContentText("Changes saved successfully!");
            alert.showAndWait();
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Failed to save changes.");
            alert.showAndWait();
        }
    }

    private void deleteProduct(ProductRow productRow) {
        ProductDAO productDAO = new ProductDAO();
        try {
            productDAO.deleteProduct(productRow.toProduct().getProductId());
            tableView.getItems().remove(productRow);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(null);
            alert.setContentText("Product deleted successfully!");
            alert.showAndWait();
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Failed to delete product.");
            alert.showAndWait();
        }
    }

    private void openAddProductDialog() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Add New Product");

        VBox dialogVBox = new VBox(10);
        dialogVBox.setPadding(new Insets(10));

        TextField nameField = new TextField();
        nameField.setPromptText("Name");

        TextField volumeField = new TextField();
        volumeField.setPromptText("Volume");

        TextField descriptionField = new TextField();
        descriptionField.setPromptText("Description");

        TextField imageField = new TextField();
        imageField.setPromptText("Image");

        TextField priceField = new TextField();
        priceField.setPromptText("Price");

        TextField stockField = new TextField();
        stockField.setPromptText("Stock Quantity");

        Button addButton = new Button("Add Product");
        addButton.setOnAction(e -> {
            try {
                Product product = new Product();
                product.setName(nameField.getText());
                product.setVolumePerBottle(new BigDecimal(volumeField.getText()));
                product.setDescription(descriptionField.getText());
                product.setImage(imageField.getText());
                product.setPrice(new BigDecimal(priceField.getText()));
                product.setStockQuantity(Integer.parseInt(stockField.getText()));

                ProductDAO productDAO = new ProductDAO();
                productDAO.addProduct(product);

                loadProducts();
                dialog.close();

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText("Product added successfully!");
                alert.showAndWait();
            } catch (Exception ex) {
                ex.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Failed to add product.");
                alert.showAndWait();
            }
        });

        dialogVBox.getChildren().addAll(nameField, volumeField, descriptionField, imageField, priceField, stockField, addButton);

        Scene dialogScene = new Scene(dialogVBox, 400, 400);
        dialog.setScene(dialogScene);
        dialog.showAndWait();
    }

    @FunctionalInterface
    private interface ProductPropertyGetter {
        StringProperty get(ProductRow productRow);
    }

    public static class ProductRow {
        private final StringProperty id;
        private final StringProperty name;
        private final StringProperty volume;
        private final StringProperty description;
        private final StringProperty image;
        private final StringProperty price;
        private final StringProperty stock;

        public ProductRow(Product product) {
            this.id = new SimpleStringProperty(String.valueOf(product.getProductId()));
            this.name = new SimpleStringProperty(product.getName());
            this.volume = new SimpleStringProperty(product.getVolumePerBottle().toString());
            this.description = new SimpleStringProperty(product.getDescription());
            this.image = new SimpleStringProperty(product.getImage());
            this.price = new SimpleStringProperty(product.getPrice().toString());
            this.stock = new SimpleStringProperty(String.valueOf(product.getStockQuantity()));
        }

        public StringProperty idProperty() {
            return id;
        }

        public StringProperty nameProperty() {
            return name;
        }

        public StringProperty volumeProperty() {
            return volume;
        }

        public StringProperty descriptionProperty() {
            return description;
        }

        public StringProperty imageProperty() {
            return image;
        }

        public StringProperty priceProperty() {
            return price;
        }

        public StringProperty stockProperty() {
            return stock;
        }

        public Product toProduct() {
            return new Product(
                    Integer.parseInt(id.get()),
                    name.get(),
                    new BigDecimal(volume.get()),
                    description.get(),
                    image.get(),
                    new BigDecimal(price.get()),
                    Integer.parseInt(stock.get()),
                    null,
                    null
            );
        }

        public boolean matchesFilter(String filter) {
            filter = filter.toLowerCase();
            return safeContains(id.get(), filter) ||
                    safeContains(name.get(), filter) ||
                    safeContains(volume.get(), filter) ||
                    safeContains(description.get(), filter) ||
                    safeContains(image.get(), filter) ||
                    safeContains(price.get(), filter) ||
                    safeContains(stock.get(), filter);
        }

        private boolean safeContains(String value, String filter) {
            return value != null && value.toLowerCase().contains(filter);
        }
    }
}
