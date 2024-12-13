package ui;

import java.sql.SQLException;

import dao.UserDAO;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import models.User;
import tools.SessionManager;

public class LoginUI {
    private AppUI appUI;

    public LoginUI(AppUI appUI) {
        this.appUI = appUI;
    }

    public Scene getScene() {
        // Composants de l'interface
        Text title = new Text("Connexion");
        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Mot de passe");
        Button loginButton = new Button("Se connecter");
        Button registerButton = new Button("S'inscrire");

        // Actions
        loginButton.setOnAction(e -> {
            UserDAO userDAO = new UserDAO();
            try {
                User user = userDAO.validateUser(emailField.getText(), passwordField.getText());
                if (user != null) {
                    System.out.println("Connexion réussie, bienvenue " + user.getFirstName() + " !");
                    SessionManager.setCurrentUser(user);
                    appUI.showUserSettingsUI(); // Passer à l'interface principale
                } else {
                    System.out.println("Identifiants incorrects !");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });


        registerButton.setOnAction(e -> appUI.showRegister()); // Naviguer vers la page d'inscription

        // Organisation de la vue
        VBox layout = new VBox(10, title, emailField, passwordField, loginButton, registerButton);
        layout.setAlignment(Pos.CENTER);
        return new Scene(layout, 400, 300);
    }
}
