package ui.pages;

import dao.UserDAO;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import models.User;
import tools.PasswordManager;
import ui.elements.MainLayout;

public class RegisterUI {
    private MainLayout mainLayout;

    public RegisterUI(MainLayout mainLayout) {
        this.mainLayout = mainLayout;
    }

    public VBox getView() {
        // Composants de l'interface
        Label title = new Label("Inscription");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        TextField firstNameField = new TextField();
        firstNameField.setPromptText("Prénom");
        TextField lastNameField = new TextField();
        lastNameField.setPromptText("Nom");
        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Mot de passe");
        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirmer le mot de passe");

        Button registerButton = new Button("S'inscrire");
        registerButton.setDisable(true); // Désactivé par défaut
        Button cancelButton = new Button("Annuler");

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red;");

        // Validation dynamique des champs
        Runnable validateFields = () -> {
            boolean allFieldsFilled = !firstNameField.getText().isEmpty() &&
                                      !lastNameField.getText().isEmpty() &&
                                      !emailField.getText().isEmpty() &&
                                      !passwordField.getText().isEmpty() &&
                                      !confirmPasswordField.getText().isEmpty();

            boolean passwordsMatch = passwordField.getText().equals(confirmPasswordField.getText());

            if (allFieldsFilled && passwordsMatch) {
                registerButton.setDisable(false);
                errorLabel.setText(""); // Efface les erreurs
            } else {
                registerButton.setDisable(true);
                if (!passwordsMatch) {
                    errorLabel.setText("Les mots de passe ne correspondent pas.");
                }
            }
        };

        // Ajout des écouteurs pour validation dynamique
        firstNameField.textProperty().addListener((observable, oldValue, newValue) -> validateFields.run());
        lastNameField.textProperty().addListener((observable, oldValue, newValue) -> validateFields.run());
        emailField.textProperty().addListener((observable, oldValue, newValue) -> validateFields.run());
        passwordField.textProperty().addListener((observable, oldValue, newValue) -> validateFields.run());
        confirmPasswordField.textProperty().addListener((observable, oldValue, newValue) -> validateFields.run());

        // Actions des boutons
        registerButton.setOnAction(e -> {
            UserDAO userDAO = new UserDAO();
            try {
                if (!userDAO.emailExists(emailField.getText())) {
                    // Créer un nouvel utilisateur
                    User newUser = new User(
                        0, // ID auto-incrémenté
                        firstNameField.getText(),
                        lastNameField.getText(),
                        emailField.getText(),
                        PasswordManager.hashPassword(passwordField.getText()),
                        "customer" // Rôle par défaut
                    );

                    userDAO.addUser(newUser);
                    System.out.println("Utilisateur inscrit avec succès !");
                    mainLayout.setContent(new LoginUI(mainLayout).getView()); // Redirection vers la connexion
                } else {
                    errorLabel.setText("Cet email est déjà utilisé !");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                errorLabel.setText("Erreur lors de l'inscription. Veuillez réessayer.");
            }
        });

        cancelButton.setOnAction(e -> {
            mainLayout.setContent(new LoginUI(mainLayout).getView()); // Retour à la page de connexion
        });

        // Disposition des éléments
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
        form.add(new Label("Mot de passe :"), 0, 3);
        form.add(passwordField, 1, 3);
        form.add(new Label("Confirmer le mot de passe :"), 0, 4);
        form.add(confirmPasswordField, 1, 4);

        VBox layout = new VBox(15, title, form, errorLabel, registerButton, cancelButton);
        layout.setAlignment(Pos.CENTER);

        return layout;
    }
}
