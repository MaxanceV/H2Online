package Main;

import javafx.application.Application;
import javafx.stage.Stage;
import ui.AppUI;

//	private static Connection myConnection;
//    
//    public MainLaunch() {
//    	myConnection = DBconnection.getConnection();
//    	System.out.println(myConnection);
//    	
//    }
//    
//    public static void main(String[] args) {
//  	MainLaunch main = new MainLaunch();
//    	CtrlUser ctrlUser = new CtrlUser(myConnection);
//    	ctrlUser.recupUser();
//    }
	
public class MainLaunched extends Application {
	    @Override
	    public void start(Stage primaryStage) {
	        // Appel de la classe principale d'interface utilisateur
	        AppUI appUI = new AppUI(primaryStage);
	        appUI.showLogin(); // Affiche la page de connexion au lancement
	    }

	    public static void main(String[] args) {
	        launch(args); // Lance l'application JavaFX
	    } 
    

}
