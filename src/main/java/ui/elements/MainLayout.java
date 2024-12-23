package ui.elements;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import tools.SessionManager;
import ui.pages.LoginUI;
import ui.pages.UserSettingsUI;

public class MainLayout {
    private BorderPane root; // Le layout principal
    private Scene scene;     // La scène de l'application
    private MenuButton menuButton; // Menu déroulant

    public MainLayout(Stage primaryStage) {
        // Initialiser le layout
        root = new BorderPane();
        createHeader(primaryStage);
        createFooter();
        scene = new Scene(root, 800, 600); // Taille par défaut
    }

    // Création du header
    private void createHeader(Stage primaryStage) {
        HBox header = new HBox(5); // Espacement très réduit entre les éléments
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("-fx-background-color: #333333; -fx-padding: 5;"); // Réduction du padding global

        // Logo
        Image logoImage = new Image(getClass().getResourceAsStream("/images/logo/logo_goutte.png"));
        ImageView logo = new ImageView(logoImage);
        logo.setFitWidth(50); // Taille adaptée
        logo.setFitHeight(50); // Taille adaptée pour un alignement parfait avec le titre
        logo.setPreserveRatio(true); // Conserver les proportions
        logo.setSmooth(true); // Améliorer le rendu visuel

        // Définir le logo comme icône de la fenêtre
        primaryStage.getIcons().add(logoImage);

        // Titre du site
        Label siteName = new Label("H2Online");
        siteName.setStyle("-fx-text-fill: white; -fx-font-size: 20px; -fx-padding: 0;"); // Alignement avec le logo

        // Menu déroulant
        menuButton = new MenuButton("Navigation");
        MenuItem homeItem = new MenuItem("Accueil");
        MenuItem cartItem = new MenuItem("Panier");
        MenuItem settingsItem = new MenuItem("Paramètres");
        MenuItem logoutItem = new MenuItem("Déconnexion");

        menuButton.getItems().addAll(homeItem, cartItem, settingsItem, logoutItem);

        // Ajouter les éléments au header (logo, titre, menu sur une seule ligne)
        header.getChildren().addAll(logo, siteName, menuButton);

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

        Label footerLabel = new Label("© 2024 H2Online - All rights reserved");
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
