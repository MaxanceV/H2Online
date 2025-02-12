package Main;

import javafx.application.Application;
import javafx.stage.Stage;
import tools.PasswordManager;
import tools.SessionManager;
import ui.elements.MainLayout;
import ui.pages.LoginPage;

/**
 * The main entry point for the H2Online application.
 * This class initializes the primary stage and sets up the main layout.
 */
public class MainLaunched extends Application {

    /**
     * Starts the JavaFX application by initializing the main layout and displaying the login page.
     *
     * @param primaryStage The primary stage of the application.
     */
    @Override
    public void start(Stage primaryStage) {
        MainLayout mainLayout = new MainLayout(primaryStage);
        SessionManager.setMainLayout(mainLayout);
        mainLayout.setContent(new LoginPage(mainLayout).getView());
        mainLayout.disableMenu();
        primaryStage.setScene(mainLayout.getScene());
        primaryStage.setTitle("H2Online");
        primaryStage.show();
    }

    /**
     * The main method that launches the JavaFX application.
     *
     * @param args Command-line arguments passed to the application.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
