package Main;

import javafx.application.Application;
import javafx.stage.Stage;
import tools.PasswordManager;
import tools.SessionManager;
import ui.elements.MainLayout;
import ui.pages.LoginPage;

public class MainLaunched extends Application {

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


    public static void main(String[] args) {
        launch(args);
    }
}
