package ui.pages;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.User;
import sqlbdd.UserSQL;
import tools.NotificationUtils;
import tools.PasswordManager;
import tools.SessionManager;

public class UserSettingsPage {
    private User user;

    public UserSettingsPage() {
        this.user = SessionManager.getCurrentUser();
    }

    public VBox getView() {
        TextField firstNameField = new TextField(safeString(user.getFirstName()));
        TextField lastNameField = new TextField(safeString(user.getLastName()));
        TextField emailField = new TextField(safeString(user.getEmail()));
        TextField phoneField = new TextField(safeString(user.getPhoneNumber()));
        TextField addressField = new TextField(safeString(user.getAddress()));
        TextField cityField = new TextField(safeString(user.getCity()));
        TextField postalCodeField = new TextField(safeString(user.getPostalCode()));
        TextField countryField = new TextField(safeString(user.getCountry()));

        Button saveButton = new Button("Save Changes");
        saveButton.setDisable(true); // Disabled by default
        Button changePasswordButton = new Button("Change Password");

        GridPane form = new GridPane();
        form.setAlignment(Pos.CENTER);
        form.setHgap(10);
        form.setVgap(10);

        form.add(new Label("First Name:"), 0, 0);
        form.add(firstNameField, 1, 0);
        form.add(new Label("Last Name:"), 0, 1);
        form.add(lastNameField, 1, 1);
        form.add(new Label("Email:"), 0, 2);
        form.add(emailField, 1, 2);
        form.add(new Label("Phone Number:"), 0, 3);
        form.add(phoneField, 1, 3);
        form.add(new Label("Address:"), 0, 4);
        form.add(addressField, 1, 4);
        form.add(new Label("City:"), 0, 5);
        form.add(cityField, 1, 5);
        form.add(new Label("Postal Code:"), 0, 6);
        form.add(postalCodeField, 1, 6);
        form.add(new Label("Country:"), 0, 7);
        form.add(countryField, 1, 7);
        form.add(saveButton, 0, 8);
        form.add(changePasswordButton, 1, 8);

        VBox layout = new VBox(20, form);
        layout.setAlignment(Pos.CENTER);

        Runnable checkFields = () -> {
            boolean requiredFieldsFilled =
                    !firstNameField.getText().isEmpty() &&
                            !lastNameField.getText().isEmpty() &&
                            !emailField.getText().isEmpty();

            boolean hasChanges =
                    !safeString(firstNameField.getText()).equals(safeString(user.getFirstName())) ||
                            !safeString(lastNameField.getText()).equals(safeString(user.getLastName())) ||
                            !safeString(emailField.getText()).equals(safeString(user.getEmail())) ||
                            !safeString(phoneField.getText()).equals(safeString(user.getPhoneNumber())) ||
                            !safeString(addressField.getText()).equals(safeString(user.getAddress())) ||
                            !safeString(cityField.getText()).equals(safeString(user.getCity())) ||
                            !safeString(postalCodeField.getText()).equals(safeString(user.getPostalCode())) ||
                            !safeString(countryField.getText()).equals(safeString(user.getCountry()));

            saveButton.setDisable(!(requiredFieldsFilled && hasChanges));
        };

        firstNameField.textProperty().addListener((observable, oldValue, newValue) -> checkFields.run());
        lastNameField.textProperty().addListener((observable, oldValue, newValue) -> checkFields.run());
        emailField.textProperty().addListener((observable, oldValue, newValue) -> checkFields.run());
        phoneField.textProperty().addListener((observable, oldValue, newValue) -> checkFields.run());
        addressField.textProperty().addListener((observable, oldValue, newValue) -> checkFields.run());
        cityField.textProperty().addListener((observable, oldValue, newValue) -> checkFields.run());
        postalCodeField.textProperty().addListener((observable, oldValue, newValue) -> checkFields.run());
        countryField.textProperty().addListener((observable, oldValue, newValue) -> checkFields.run());

        saveButton.setOnAction(e -> {
            try {
                UserSQL userDAO = new UserSQL();

                Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
                confirmation.setTitle("Confirm Changes");
                confirmation.setHeaderText("Save changes?");
                confirmation.setContentText("Do you want to save the changes?");
                if (confirmation.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                    user.setFirstName(firstNameField.getText());
                    user.setLastName(lastNameField.getText());
                    user.setEmail(emailField.getText());
                    user.setPhoneNumber(phoneField.getText());
                    user.setAddress(addressField.getText());
                    user.setCity(cityField.getText());
                    user.setPostalCode(postalCodeField.getText());
                    user.setCountry(countryField.getText());
                    userDAO.updateUser(user);
                    System.out.println("User information updated successfully!");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setTitle("Error");
                error.setHeaderText("Error during update");
                error.setContentText("Unable to save changes.");
                error.showAndWait();
            }
        });

        changePasswordButton.setOnAction(e -> openPasswordChangePopup());

        return layout;
    }

    private void openPasswordChangePopup() {
        Stage popupStage = new Stage();
        popupStage.setTitle("Change Password");
        popupStage.setResizable(false);
        popupStage.initModality(Modality.APPLICATION_MODAL); // Blocks other windows until closed

        PasswordField oldPasswordField = new PasswordField();
        oldPasswordField.setPromptText("Current password");
        PasswordField newPasswordField = new PasswordField();
        newPasswordField.setPromptText("New password");
        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm new password");

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red;");

        Button confirmButton = new Button("Confirm");
        confirmButton.setDisable(true);
        Button cancelButton = new Button("Cancel");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("Current Password:"), 0, 0);
        grid.add(oldPasswordField, 1, 0);
        grid.add(new Label("New Password:"), 0, 1);
        grid.add(newPasswordField, 1, 1);
        grid.add(new Label("Confirm Password:"), 0, 2);
        grid.add(confirmPasswordField, 1, 2);
        grid.add(errorLabel, 1, 3);

        HBox buttons = new HBox(10, confirmButton, cancelButton);
        buttons.setAlignment(Pos.CENTER_RIGHT);

        VBox content = new VBox(10, grid, buttons);
        content.setPadding(new Insets(10));

        Scene popupScene = new Scene(content);
        popupStage.setScene(popupScene);

        Runnable validateFields = () -> {
            String oldPassword = oldPasswordField.getText();
            String newPassword = newPasswordField.getText();
            String confirmPassword = confirmPasswordField.getText();

            boolean valid = !oldPassword.isEmpty() && !newPassword.isEmpty() && newPassword.equals(confirmPassword);
            confirmButton.setDisable(!valid);
            if (!newPassword.equals(confirmPassword)) {
                errorLabel.setText("New passwords do not match.");
            } else {
                errorLabel.setText("");
            }
        };

        oldPasswordField.textProperty().addListener((observable, oldValue, newValue) -> validateFields.run());
        newPasswordField.textProperty().addListener((observable, oldValue, newValue) -> validateFields.run());
        confirmPasswordField.textProperty().addListener((observable, oldValue, newValue) -> validateFields.run());

        confirmButton.setOnAction(e -> {
            try {
                UserSQL userDAO = new UserSQL();
                String oldPassword = oldPasswordField.getText();
                String newPassword = newPasswordField.getText();

                if (PasswordManager.hashPassword(oldPassword).equals(user.getPassword())) {
                    user.setPassword(PasswordManager.hashPassword(newPassword));
                    userDAO.updateUser(user);
                    NotificationUtils.showNotification(SessionManager.getMainLayout().getRootPane(),
                            "Password changed successfully!", true);

                    popupStage.close(); 
                } else {
                    errorLabel.setText("Incorrect current password.");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                NotificationUtils.showNotification(SessionManager.getMainLayout().getRootPane(),
                        "An error occurred while changing the password.", false);
            }
        });

        cancelButton.setOnAction(e -> {
            popupStage.close();
        });

        popupStage.showAndWait();
    }


    private String safeString(String value) {
        return value == null ? "" : value;
    }
}
