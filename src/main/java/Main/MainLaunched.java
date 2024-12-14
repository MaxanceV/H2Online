package Main;

import javafx.application.Application;
import javafx.stage.Stage;
import tools.MainLayout;
import ui.LoginUI;

public class MainLaunched extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Initialiser le layout principal
        MainLayout mainLayout = new MainLayout();

        // Définir la page de connexion comme contenu initial
        mainLayout.setContent(new LoginUI(mainLayout).getView());

        // Désactiver le menu par défaut (pas connecté)
        mainLayout.disableMenu();

        // Configurer la scène et afficher la fenêtre
        primaryStage.setScene(mainLayout.getScene());
        primaryStage.setTitle("H2Online");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
