package ui.pages;

import dao.UserDAO;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import models.User;
import tools.SessionManager;

public class UserSettingsPage {
    private User user;

    public UserSettingsPage() {
        this.user = SessionManager.getCurrentUser(); // L'utilisateur actuellement connecté
    }

    public VBox getView() {
        // Champs de formulaire
        TextField firstNameField = new TextField(safeString(user.getFirstName()));
        TextField lastNameField = new TextField(safeString(user.getLastName()));
        TextField emailField = new TextField(safeString(user.getEmail()));
        TextField phoneField = new TextField(safeString(user.getPhoneNumber()));
        TextField addressField = new TextField(safeString(user.getAddress()));
        TextField cityField = new TextField(safeString(user.getCity()));
        TextField postalCodeField = new TextField(safeString(user.getPostalCode()));
        TextField countryField = new TextField(safeString(user.getCountry()));

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
        // Code de la fenêtre popup pour changer le mot de passe (inchangé)
    }

    private String safeString(String value) {
        return value == null ? "" : value;
    }
}
