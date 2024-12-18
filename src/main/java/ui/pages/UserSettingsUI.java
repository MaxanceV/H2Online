package ui.pages;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.User;
import tools.PasswordManager;
import tools.SessionManager;
import dao.UserDAO;

public class UserSettingsUI {
    private User user;

    public UserSettingsUI() {
        this.user = SessionManager.getCurrentUser(); // L'utilisateur actuellement connecté
    }

    public VBox getView() {
        // Champs de formulaire
        TextField firstNameField = new TextField(user.getFirstName());
        TextField lastNameField = new TextField(user.getLastName());
        TextField emailField = new TextField(user.getEmail());
        TextField phoneField = new TextField(user.getPhoneNumber());
        TextField addressField = new TextField(user.getAddress());
        TextField cityField = new TextField(user.getCity());
        TextField postalCodeField = new TextField(user.getPostalCode());
        TextField countryField = new TextField(user.getCountry());

        // Boutons
        Button saveButton = new Button("Enregistrer les modifications");
        saveButton.setDisable(true); // Désactivé par défaut
        Button changePasswordButton = new Button("Changer le mot de passe");

        // Formulaire
        GridPane form = new GridPane();
        form.setAlignment(Pos.CENTER);
        form.setHgap(10);
        form.setVgap(10);

        form.add(new Label("Prénom :"), 0, 0);
        form.add(firstNameField, 1, 0);
        form.add(new Label("Nom :"), 0, 1);
        form.add(lastNameField, 1, 1);
        form.add(new Label("Email :"), 0, 2);
        form.add(emailField, 1, 2);
        form.add(new Label("Téléphone :"), 0, 3);
        form.add(phoneField, 1, 3);
        form.add(new Label("Adresse :"), 0, 4);
        form.add(addressField, 1, 4);
        form.add(new Label("Ville :"), 0, 5);
        form.add(cityField, 1, 5);
        form.add(new Label("Code postal :"), 0, 6);
        form.add(postalCodeField, 1, 6);
        form.add(new Label("Pays :"), 0, 7);
        form.add(countryField, 1, 7);
        form.add(saveButton, 0, 8);
        form.add(changePasswordButton, 1, 8);

        // Conteneur principal
        VBox layout = new VBox(20, form);
        layout.setAlignment(Pos.CENTER);

        // Activer/Désactiver le bouton "Enregistrer"
        Runnable checkFields = () -> {
            boolean requiredFieldsFilled = 
                !firstNameField.getText().isEmpty() &&
                !lastNameField.getText().isEmpty() &&
                !emailField.getText().isEmpty();

            boolean hasChanges = 
                !firstNameField.getText().equals(user.getFirstName()) ||
                !lastNameField.getText().equals(user.getLastName()) ||
                !emailField.getText().equals(user.getEmail()) ||
                !phoneField.getText().equals(user.getPhoneNumber() != null ? user.getPhoneNumber() : "") ||
                !addressField.getText().equals(user.getAddress() != null ? user.getAddress() : "") ||
                !cityField.getText().equals(user.getCity() != null ? user.getCity() : "") ||
                !postalCodeField.getText().equals(user.getPostalCode() != null ? user.getPostalCode() : "") ||
                !countryField.getText().equals(user.getCountry() != null ? user.getCountry() : "");

            saveButton.setDisable(!(requiredFieldsFilled && hasChanges));
        };

        // Ajouter des écouteurs sur les champs
        firstNameField.textProperty().addListener((observable, oldValue, newValue) -> checkFields.run());
        lastNameField.textProperty().addListener((observable, oldValue, newValue) -> checkFields.run());
        emailField.textProperty().addListener((observable, oldValue, newValue) -> checkFields.run());
        phoneField.textProperty().addListener((observable, oldValue, newValue) -> checkFields.run());
        addressField.textProperty().addListener((observable, oldValue, newValue) -> checkFields.run());
        cityField.textProperty().addListener((observable, oldValue, newValue) -> checkFields.run());
        postalCodeField.textProperty().addListener((observable, oldValue, newValue) -> checkFields.run());
        countryField.textProperty().addListener((observable, oldValue, newValue) -> checkFields.run());

        // Gestion des boutons
        saveButton.setOnAction(e -> {
            try {
                UserDAO userDAO = new UserDAO();

                // Confirmation avant sauvegarde
                Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
                confirmation.setTitle("Confirmer les modifications");
                confirmation.setHeaderText("Enregistrer les modifications ?");
                confirmation.setContentText("Voulez-vous sauvegarder les changements ?");
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
                    System.out.println("Informations mises à jour avec succès !");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setTitle("Erreur");
                error.setHeaderText("Erreur lors de la mise à jour");
                error.setContentText("Impossible de sauvegarder les modifications.");
                error.showAndWait();
            }
        });

        changePasswordButton.setOnAction(e -> openPasswordChangePopup());

        return layout;
    }

    private void openPasswordChangePopup() {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Changer le mot de passe");

        PasswordField newPasswordField = new PasswordField();
        newPasswordField.setPromptText("Nouveau mot de passe");
        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirmer le mot de passe");

        Button submitButton = new Button("Confirmer");
        submitButton.setDisable(true);
        Button cancelButton = new Button("Annuler");

        GridPane popupGrid = new GridPane();
        popupGrid.setAlignment(Pos.CENTER);
        popupGrid.setHgap(10);
        popupGrid.setVgap(10);

        popupGrid.add(new Label("Nouveau mot de passe :"), 0, 0);
        popupGrid.add(newPasswordField, 1, 0);
        popupGrid.add(new Label("Confirmer :"), 0, 1);
        popupGrid.add(confirmPasswordField, 1, 1);
        popupGrid.add(submitButton, 0, 2);
        popupGrid.add(cancelButton, 1, 2);

        newPasswordField.textProperty().addListener((observable, oldValue, newValue) -> {
            boolean passwordsMatch = newValue.equals(confirmPasswordField.getText());
            submitButton.setDisable(!passwordsMatch);
        });

        confirmPasswordField.textProperty().addListener((observable, oldValue, newValue) -> {
            boolean passwordsMatch = newValue.equals(newPasswordField.getText());
            submitButton.setDisable(!passwordsMatch);
        });

        submitButton.setOnAction(e -> {
            try {
                UserDAO userDAO = new UserDAO();
                user.setPassword(PasswordManager.hashPassword(newPasswordField.getText()));
                userDAO.updateUser(user);
                System.out.println("Mot de passe mis à jour !");
                popupStage.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        cancelButton.setOnAction(e -> popupStage.close());

        Scene popupScene = new Scene(popupGrid, 400, 250);
        popupStage.setScene(popupScene);
        popupStage.showAndWait();
    }
}
