package ui;

import javafx.scene.Scene;
import javafx.stage.Stage;

public class AppUI {
    private Stage stage;

    public AppUI(Stage stage) {
        this.stage = stage;
    }

    // Afficher la vue de connexion
    public void showLogin() {
        LoginUI loginUI = new LoginUI(this); // Passe une référence pour la navigation
        Scene loginScene = loginUI.getScene();
        stage.setScene(loginScene);
        stage.setTitle("H2Online - Connexion");
        stage.show();
    }

    // Afficher la vue d'inscription
    public void showRegister() {
        RegisterUI registerUI = new RegisterUI(this);
        Scene registerScene = registerUI.getScene();
        stage.setScene(registerScene);
        stage.setTitle("H2Online - Inscription");
    }

    // Afficher l'interface principale après connexion
    public void showMainUI() {
//        MainUI mainUI = new MainUI(this);
//        Scene mainScene = mainUI.getScene();
//        stage.setScene(mainScene);
//        stage.setTitle("H2Online - Catalogue");
    }
}
