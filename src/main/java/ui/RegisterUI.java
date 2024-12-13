package ui;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class RegisterUI {
    private AppUI appUI;

    public RegisterUI(AppUI appUI) {
        this.appUI = appUI;
    }

    public Scene getScene() {
        // Composants de l'interface
        Text title = new Text("Inscription");
        TextField firstNameField = new TextField();
        firstNameField.setPromptText("Prénom");
        TextField lastNameField = new TextField();
        lastNameField.setPromptText("Nom");
        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Mot de passe");
        Button registerButton = new Button("S'inscrire");

        // Actions
        registerButton.setOnAction(e -> {
            // Appeler une méthode pour ajouter un utilisateur
            System.out.println("Utilisateur inscrit !");
            appUI.showLogin(); // Retourner à la connexion après inscription
        });

        // Organisation de la vue
        VBox layout = new VBox(10, title, firstNameField, lastNameField, emailField, passwordField, registerButton);
        layout.setAlignment(Pos.CENTER);
        return new Scene(layout, 400, 400);
    }
}
