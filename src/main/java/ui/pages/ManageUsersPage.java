package ui.pages;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import models.User;
import sqlbdd.UserSQL;

import java.sql.SQLException;
import java.util.List;

public class ManageUsersPage {
    private BorderPane layout;
    private TableView<UserRow> tableView;
    private TextField searchField;

    public ManageUsersPage() {
        layout = new BorderPane();

        Label titleLabel = new Label("Manage Users");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        tableView = new TableView<>();
        tableView.setEditable(true); 

        VBox topBox = new VBox(10);
        topBox.setPadding(new Insets(10));
        topBox.getChildren().addAll(titleLabel, createSearchBar());

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

    private VBox createSearchBar() {
        VBox searchBox = new VBox(5);
        searchBox.setPadding(new Insets(5));

        Label searchLabel = new Label("Search by any field:");
        searchField = new TextField();
        searchField.setPromptText("Type to search...");

        Button searchButton = new Button("Search");
        searchButton.setOnAction(e -> applySearchFilter()); 

        HBox searchBar = new HBox(10, searchField, searchButton);

        searchBox.getChildren().addAll(searchLabel, searchBar);
        return searchBox;
    }

    private TableView<UserRow> createTable() {
        TableColumn<UserRow, String> idColumn = createEditableColumn("ID", UserRow::idProperty, false);
        TableColumn<UserRow, String> firstNameColumn = createEditableColumn("First Name", UserRow::firstNameProperty, true);
        TableColumn<UserRow, String> lastNameColumn = createEditableColumn("Last Name", UserRow::lastNameProperty, true);
        TableColumn<UserRow, String> emailColumn = createEditableColumn("Email", UserRow::emailProperty, true);
        TableColumn<UserRow, String> phoneColumn = createEditableColumn("Phone", UserRow::phoneProperty, true);
        TableColumn<UserRow, String> addressColumn = createEditableColumn("Address", UserRow::addressProperty, true);
        TableColumn<UserRow, String> cityColumn = createEditableColumn("City", UserRow::cityProperty, true);
        TableColumn<UserRow, String> postalCodeColumn = createEditableColumn("Postal Code", UserRow::postalCodeProperty, true);
        TableColumn<UserRow, String> countryColumn = createEditableColumn("Country", UserRow::countryProperty, true);
        TableColumn<UserRow, String> roleColumn = createEditableColumn("Role", UserRow::roleProperty, true);

        // Colonne pour l'action de suppression
        TableColumn<UserRow, Void> actionColumn = new TableColumn<>("Actions");
        actionColumn.setCellFactory(param -> new TableCell<UserRow, Void>() {
            private final Button deleteButton = new Button("Delete");

            {
                deleteButton.setStyle("-fx-background-color: red; -fx-text-fill: white;");
                deleteButton.setOnAction(e -> {
                    UserRow userRow = getTableView().getItems().get(getIndex());
                    deleteUser(userRow);
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
                idColumn, firstNameColumn, lastNameColumn, emailColumn, phoneColumn,
                addressColumn, cityColumn, postalCodeColumn, countryColumn, roleColumn, actionColumn
        );

        loadUsers();

        return tableView;
    }

    private void deleteUser(UserRow userRow) {
        UserSQL userDAO = new UserSQL();
        try {
            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Delete User");
            confirmationAlert.setHeaderText("Are you sure you want to delete this user?");
            confirmationAlert.setContentText("This action cannot be undone.");

            if (confirmationAlert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                userDAO.deleteUser(Integer.parseInt(userRow.idProperty().get())); // Supprime l'utilisateur de la BDD
                tableView.getItems().remove(userRow); // Supprime l'utilisateur du tableau
                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Success");
                successAlert.setHeaderText(null);
                successAlert.setContentText("User deleted successfully.");
                successAlert.showAndWait();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Error");
            errorAlert.setHeaderText("Failed to delete user.");
            errorAlert.setContentText("An error occurred while deleting the user.");
            errorAlert.showAndWait();
        }
    }


    private TableColumn<UserRow, String> createEditableColumn(String title, UserPropertyGetter propertyGetter, boolean editable) {
        TableColumn<UserRow, String> column = new TableColumn<>(title);
        column.setCellValueFactory(cellData -> propertyGetter.get(cellData.getValue()));

        if (editable) {
            column.setCellFactory(TextFieldTableCell.forTableColumn());
            column.setOnEditCommit(event -> {
                UserRow userRow = event.getRowValue();
                String newValue = event.getNewValue();
                propertyGetter.get(userRow).set(newValue != null ? newValue : ""); // Assurez-vous de ne pas définir null
            });
        }

        return column;
    }

    private void loadUsers() {
        UserSQL userDAO = new UserSQL();
        try {
            List<User> users = userDAO.getAllUsers();
            List<UserRow> userRows = users.stream().map(UserRow::new).toList();
            tableView.setItems(FXCollections.observableArrayList(userRows));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void applySearchFilter() {
        String filterText = searchField.getText().trim().toLowerCase();

        if (filterText.isEmpty()) {
            loadUsers();
        } else {
            FilteredList<UserRow> filteredList = new FilteredList<>(tableView.getItems(), user -> user.matchesFilter(filterText));
            tableView.setItems(filteredList);
        }
    }

    private void saveChanges() {
        UserSQL userDAO = new UserSQL();
        try {
            for (UserRow userRow : tableView.getItems()) {
                User user = userRow.toUser();
                userDAO.updateUser(user);
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

    @FunctionalInterface
    private interface UserPropertyGetter {
        StringProperty get(UserRow userRow);
    }

    public static class UserRow {
        private final StringProperty id;
        private final StringProperty firstName;
        private final StringProperty lastName;
        private final StringProperty email;
        private final StringProperty phone;
        private final StringProperty address;
        private final StringProperty city;
        private final StringProperty postalCode;
        private final StringProperty country;
        private final StringProperty role;

        public UserRow(User user) {
            this.id = new SimpleStringProperty(String.valueOf(user.getId()));
            this.firstName = new SimpleStringProperty(user.getFirstName());
            this.lastName = new SimpleStringProperty(user.getLastName());
            this.email = new SimpleStringProperty(user.getEmail());
            this.phone = new SimpleStringProperty(user.getPhoneNumber());
            this.address = new SimpleStringProperty(user.getAddress());
            this.city = new SimpleStringProperty(user.getCity());
            this.postalCode = new SimpleStringProperty(user.getPostalCode());
            this.country = new SimpleStringProperty(user.getCountry());
            this.role = new SimpleStringProperty(user.getRole());
        }

        public StringProperty idProperty() {
            return id;
        }

        public StringProperty firstNameProperty() {
            return firstName;
        }

        public StringProperty lastNameProperty() {
            return lastName;
        }

        public StringProperty emailProperty() {
            return email;
        }

        public StringProperty phoneProperty() {
            return phone;
        }

        public StringProperty addressProperty() {
            return address;
        }

        public StringProperty cityProperty() {
            return city;
        }

        public StringProperty postalCodeProperty() {
            return postalCode;
        }

        public StringProperty countryProperty() {
            return country;
        }

        public StringProperty roleProperty() {
            return role;
        }

        public User toUser() {
            return new User(
                    Integer.parseInt(id.get()),
                    firstName.get(),
                    lastName.get(),
                    email.get(),
                    phone.get(),
                    address.get(),
                    city.get(),
                    postalCode.get(),
                    country.get(),
                    null,
                    role.get()
            );
        }

        public boolean matchesFilter(String filter) {
            filter = filter.toLowerCase();
            return safeContains(id.get(), filter) ||
                    safeContains(firstName.get(), filter) ||
                    safeContains(lastName.get(), filter) ||
                    safeContains(email.get(), filter) ||
                    safeContains(phone.get(), filter) ||
                    safeContains(address.get(), filter) ||
                    safeContains(city.get(), filter) ||
                    safeContains(postalCode.get(), filter) ||
                    safeContains(country.get(), filter) ||
                    safeContains(role.get(), filter);
        }

        private boolean safeContains(String value, String filter) {
            return value != null && value.toLowerCase().contains(filter);
        }
    }
}
