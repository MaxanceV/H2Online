package ui;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.User;
import tools.SessionManager;
import dao.UserDAO;

public class UserSettingsUI {
    private User user;
    private Scene scene;

    public UserSettingsUI() {
        this.user = SessionManager.getCurrentUser(); // L'utilisateur actuellement connecté
        createUI();
    }

    private void createUI() {
        // Création des champs de formulaire
        TextField firstNameField = new TextField(user.getFirstName());
        TextField lastNameField = new TextField(user.getLastName());
        TextField emailField = new TextField(user.getEmail());
        TextField phoneField = new TextField(user.getPhoneNumber());
        TextField addressField = new TextField(user.getAddress());

        // Boutons
        Button saveButton = new Button("Enregistrer les modifications");
        saveButton.setDisable(true); // Désactivé par défaut
        Button cancelButton = new Button("Annuler");
        Button changePasswordButton = new Button("Changer le mot de passe");

        // Disposition
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        gridPane.add(new Label("Prénom :"), 0, 0);
        gridPane.add(firstNameField, 1, 0);
        gridPane.add(new Label("Nom :"), 0, 1);
        gridPane.add(lastNameField, 1, 1);
        gridPane.add(new Label("Email :"), 0, 2);
        gridPane.add(emailField, 1, 2);
        gridPane.add(new Label("Téléphone :"), 0, 3);
        gridPane.add(phoneField, 1, 3);
        gridPane.add(new Label("Adresse :"), 0, 4);
        gridPane.add(addressField, 1, 4);
        gridPane.add(saveButton, 0, 5);
        gridPane.add(cancelButton, 1, 5);
        gridPane.add(changePasswordButton, 0, 6);

        // Écouteurs pour activer/désactiver le bouton
        Runnable checkFields = () -> {
            boolean allFieldsFilled = 
                !firstNameField.getText().isEmpty() &&
                !lastNameField.getText().isEmpty() &&
                !emailField.getText().isEmpty();
            saveButton.setDisable(!allFieldsFilled);
        };

        // Ajout des écouteurs sur les champs obligatoires
        firstNameField.textProperty().addListener((observable, oldValue, newValue) -> checkFields.run());
        lastNameField.textProperty().addListener((observable, oldValue, newValue) -> checkFields.run());
        emailField.textProperty().addListener((observable, oldValue, newValue) -> checkFields.run());

        // Gestion des actions des boutons
        saveButton.setOnAction(e -> {
            try {
                UserDAO userDAO = new UserDAO();

                // Mise à jour des informations utilisateur
                user.setFirstName(firstNameField.getText());
                user.setLastName(lastNameField.getText());
                user.setEmail(emailField.getText());
                user.setPhoneNumber(phoneField.getText());
                user.setAddress(addressField.getText());
                userDAO.updateUser(user);
                System.out.println("Informations mises à jour avec succès !");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        cancelButton.setOnAction(e -> {
            System.out.println("Modifications annulées.");
            // Ajouter une action pour revenir à l'écran précédent
        });

        changePasswordButton.setOnAction(e -> openPasswordChangePopup());

        scene = new Scene(gridPane, 400, 400);
    }

    public Scene getScene() {
        return scene;
    }

    private void openPasswordChangePopup() {
        // Fenêtre popup pour changer le mot de passe
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Changer le mot de passe");

        // Champs pour le nouveau mot de passe
        PasswordField newPasswordField = new PasswordField();
        newPasswordField.setPromptText("Nouveau mot de passe");
        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirmer le mot de passe");

        Button submitButton = new Button("Confirmer");
        submitButton.setDisable(true); // Désactivé par défaut
        Button cancelButton = new Button("Annuler");

        // Disposition
        GridPane popupGrid = new GridPane();
        popupGrid.setAlignment(Pos.CENTER);
        popupGrid.setHgap(10);
        popupGrid.setVgap(10);

        popupGrid.add(new Label("Nouveau mot de passe :"), 0, 0);
        popupGrid.add(newPasswordField, 1, 0);
        popupGrid.add(new Label("Confirmer le mot de passe :"), 0, 1);
        popupGrid.add(confirmPasswordField, 1, 1);
        popupGrid.add(submitButton, 0, 2);
        popupGrid.add(cancelButton, 1, 2);

        // Écouteurs pour activer/désactiver le bouton "Confirmer"
        Runnable checkPasswordFields = () -> {
            boolean passwordsMatch = !newPasswordField.getText().isEmpty() &&
                                     newPasswordField.getText().equals(confirmPasswordField.getText());
            submitButton.setDisable(!passwordsMatch);
        };

        newPasswordField.textProperty().addListener((observable, oldValue, newValue) -> checkPasswordFields.run());
        confirmPasswordField.textProperty().addListener((observable, oldValue, newValue) -> checkPasswordFields.run());

        // Gestion des actions
        submitButton.setOnAction(e -> {
            try {
                UserDAO userDAO = new UserDAO();
                user.setPassword(newPasswordField.getText());
                userDAO.updateUser(user);
                System.out.println("Mot de passe mis à jour avec succès !");
                popupStage.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        cancelButton.setOnAction(e -> popupStage.close());

        Scene popupScene = new Scene(popupGrid, 350, 200);
        popupStage.setScene(popupScene);
        popupStage.showAndWait();
    }
}
