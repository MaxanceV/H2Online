package ui;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

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
            // Appeler la méthode de validation
            // Simuler une connexion réussie pour tester
            boolean success = true; // Remplace par une validation réelle
            if (success) {
                appUI.showMainUI(); // Naviguer vers l'interface principale
            } else {
                System.out.println("Identifiants incorrects !");
            }
        });

        registerButton.setOnAction(e -> appUI.showRegister()); // Naviguer vers la page d'inscription

        // Organisation de la vue
        VBox layout = new VBox(10, title, emailField, passwordField, loginButton, registerButton);
        layout.setAlignment(Pos.CENTER);
        return new Scene(layout, 400, 300);
    }
}
