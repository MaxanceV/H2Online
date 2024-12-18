package ui.elements;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import tools.SessionManager;
import ui.pages.LoginUI;
import ui.pages.UserSettingsUI;

public class MainLayout {
    private BorderPane root; // Le layout principal
    private Scene scene;     // La scène de l'application
    private MenuButton menuButton; // Menu déroulant

    public MainLayout() {
        // Initialiser le layout
        root = new BorderPane();
        createHeader();
        createFooter();
        scene = new Scene(root, 800, 600); // Taille par défaut
    }

    // Création du header
    private void createHeader() {
        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("-fx-background-color: #333333; -fx-padding: 10;");

        // Logo
//        ImageView logo = new ImageView(new Image(getClass().getResourceAsStream("/icons/logo.png")));
//        logo.setFitWidth(50);
//        logo.setFitHeight(50);

        // Titre du site
        Label siteName = new Label("H2Online");
        siteName.setStyle("-fx-text-fill: white; -fx-font-size: 24px;");

        // Menu déroulant
        menuButton = new MenuButton("Navigation");
        MenuItem homeItem = new MenuItem("Accueil");
        MenuItem cartItem = new MenuItem("Panier");
        MenuItem settingsItem = new MenuItem("Paramètres");
        MenuItem logoutItem = new MenuItem("Déconnexion");

        menuButton.getItems().addAll(homeItem, cartItem, settingsItem, logoutItem);

        // Ajouter les éléments au header
//        header.getChildren().addAll(logo, siteName, menuButton);
        header.getChildren().addAll(siteName, menuButton);
        
        root.setTop(header);

        // Actions des éléments du menu
        homeItem.setOnAction(e -> setContent(new Label("Page d'accueil")));
        cartItem.setOnAction(e -> setContent(new Label("Page du panier")));
        settingsItem.setOnAction(e -> setContent(new UserSettingsUI().getView()));
        logoutItem.setOnAction(e -> {
            System.out.println("Déconnexion...");
            SessionManager.clearSession();
            disableMenu(); // Désactiver le menu après déconnexion
            setContent(new LoginUI(this).getView());
        });
    }

    // Création du footer
    private void createFooter() {
        HBox footer = new HBox();
        footer.setAlignment(Pos.CENTER);
        footer.setStyle("-fx-background-color: #333333; -fx-padding: 10;");

        Label footerLabel = new Label("© 2024 H2Online - Tous droits réservés");
        footerLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");

        footer.getChildren().add(footerLabel);
        root.setBottom(footer);
    }

    // Mettre à jour le contenu central
    public void setContent(Node content) {
        root.setCenter(content);
    }

    // Obtenir la scène
    public Scene getScene() {
        return scene;
    }

    // Activer le menu déroulant
    public void enableMenu() {
        menuButton.setDisable(false);
        menuButton.setVisible(true);
    }

    // Désactiver le menu déroulant
    public void disableMenu() {
        menuButton.setDisable(true);
        menuButton.setVisible(false);
    }
}
