package ui.pages;

import dao.UserDAO;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import models.User;
import tools.SessionManager;
import ui.elements.MainLayout;

public class LoginPage {
    private MainLayout mainLayout;

    public LoginPage(MainLayout mainLayout) {
        this.mainLayout = mainLayout;
    }

    public VBox getView() {
        Text title = new Text("Connexion");
        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Mot de passe");
        Button loginButton = new Button("Se connecter");
        Button registerButton = new Button("Créer un compte");

        // Action pour le bouton de connexion
        loginButton.setOnAction(e -> {
            try {
                UserDAO userDAO = new UserDAO();
                User user = userDAO.validateUser(emailField.getText(), passwordField.getText());
                if (user != null) {
                    System.out.println("Connexion réussie : " + user.getFirstName());
                    SessionManager.setCurrentUser(user);
                    mainLayout.enableMenu(); // Activer le menu après connexion
                    mainLayout.setContent(new CatalogPage().getView());
                } else {
                    System.out.println("Identifiants incorrects !");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        // Action pour le bouton d'inscription
        registerButton.setOnAction(e -> {
            mainLayout.setContent(new RegisterPage(mainLayout).getView());
        });

        // Disposition des éléments
        VBox layout = new VBox(10, title, emailField, passwordField, loginButton, registerButton);
        layout.setAlignment(Pos.CENTER);
        return layout;
    }
}
