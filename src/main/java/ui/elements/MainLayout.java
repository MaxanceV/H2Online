package ui.elements;

import java.sql.SQLException;

import dao.OrderDAO;
import dao.OrderItemDAO;
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
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import models.Order;
import models.OrderItem;
import tools.SessionManager;
import ui.pages.CartPage;
import ui.pages.CatalogPage;
import ui.pages.LoginUI;
import ui.pages.RegisterUI;
import ui.pages.UserSettingsUI;

public class MainLayout {
    private StackPane rootPane; // Conteneur principal pour les superpositions
    private BorderPane root; // Layout principal
    private Scene scene;     // Scène de l'application
    private MenuButton profileMenuButton; // Menu déroulant pour le profil
    private StackPane cartButton; // Bouton Panier
    private Label catalogButton; // Bouton Catalog
    private Text cartBadge; // Pastille rouge pour le nombre d’articles
    

    public MainLayout(Stage primaryStage) {
        // Initialiser le conteneur principal et le layout principal
        rootPane = new StackPane();
        root = new BorderPane();
        rootPane.getChildren().add(root); // Ajouter le BorderPane comme enfant du StackPane

        createHeader(primaryStage);
        createFooter();
        
        scene = new Scene(rootPane, 800, 600); // Taille par défaut
    }
    


    // Création du header
    private void createHeader(Stage primaryStage) {
        BorderPane header = new BorderPane(); // Conteneur pour le header
        header.setStyle("-fx-background-color: #333333; -fx-padding: 5;"); // Style global du header

        // Logo et titre (à gauche)
        HBox leftSection = new HBox(10); // Espacement entre le logo et le titre
        leftSection.setAlignment(Pos.CENTER_LEFT);

        Image logoImage = new Image(getClass().getResourceAsStream("/images/logo/logo_goutte.png"));
        ImageView logo = new ImageView(logoImage);
        logo.setFitWidth(50); // Taille adaptée
        logo.setFitHeight(50); // Taille adaptée pour un alignement parfait avec le titre
        logo.setPreserveRatio(true); // Conserver les proportions

        Label siteName = new Label("H2Online");
        siteName.setStyle("-fx-text-fill: white; -fx-font-size: 20px; -fx-padding: 0;");

        leftSection.getChildren().addAll(logo, siteName);

        // Bouton Catalog (au centre)
        HBox centerSection = new HBox();
        centerSection.setAlignment(Pos.CENTER);

        catalogButton = new Label("Catalog");
        catalogButton.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-cursor: hand;");
        catalogButton.setOnMouseClicked(e -> setContent(new CatalogPage().getView()));
        centerSection.getChildren().add(catalogButton);

        // Boutons Panier et Profil (à droite)
        HBox rightSection = new HBox(10); // Espacement entre les deux boutons
        rightSection.setAlignment(Pos.CENTER_RIGHT);

        // Bouton Panier
        cartButton = new StackPane();
        ImageView cartIcon = new ImageView(new Image(getClass().getResourceAsStream("/images/logo/pannier.png")));
        cartIcon.setFitWidth(30);
        cartIcon.setFitHeight(30);
        cartButton.getChildren().add(cartIcon);

     // Ajouter la pastille rouge
        cartBadge = new Text("0"); // Nombre d’articles (par défaut 0)
        cartBadge.setStyle("-fx-fill: red; -fx-font-weight: bold; -fx-font-size: 14px;");
        StackPane.setAlignment(cartBadge, Pos.TOP_RIGHT);
        cartBadge.setVisible(false); // Masquer la pastille par défaut
        cartButton.getChildren().add(cartBadge);

        // Action du bouton Panier
        cartButton.setOnMouseClicked(e -> setContent(new CartPage().getView()));

        // Menu déroulant pour le profil
        profileMenuButton = new MenuButton();
        ImageView profileIcon = new ImageView(new Image(getClass().getResourceAsStream("/images/logo/profil.png")));
        profileIcon.setFitWidth(30);
        profileIcon.setFitHeight(30);
        profileMenuButton.setGraphic(profileIcon);

        MenuItem userSettingsItem = new MenuItem("User Settings");
        userSettingsItem.setOnAction(e -> setContent(new UserSettingsUI().getView()));

        MenuItem logoutItem = new MenuItem("Logout");
        logoutItem.setOnAction(e -> {
            System.out.println("Déconnexion...");
            SessionManager.clearSession();
            disableMenu(); // Désactiver le menu après déconnexion
            setContent(new LoginUI(this).getView());
        });

        profileMenuButton.getItems().addAll(userSettingsItem, logoutItem);

        rightSection.getChildren().addAll(cartButton, profileMenuButton);

        // Ajouter les sections au BorderPane
        header.setLeft(leftSection);
        header.setCenter(centerSection);
        header.setRight(rightSection);

        root.setTop(header); // Ajouter le header au layout principal
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

    // Obtenir le StackPane (par exemple pour les notifications)
    public StackPane getRootPane() {
        return rootPane;
    }

    // Activer les boutons lorsque l'utilisateur est connecté
    public void enableMenu() {
        profileMenuButton.setDisable(false);
        profileMenuButton.setVisible(true);
        cartButton.setVisible(true);
        catalogButton.setVisible(true);
        initializeCartBadge();
    }

    // Désactiver les boutons lorsque l'utilisateur est déconnecté
    public void disableMenu() {
        profileMenuButton.setDisable(true);
        profileMenuButton.setVisible(false);
        cartButton.setVisible(false);
        catalogButton.setVisible(false);
    }
    
    //méthode pour mettre à jour le badge
    public void updateCartBadge(int itemCount) {
        if (itemCount > 0) {
            cartBadge.setText(String.valueOf(itemCount));
            cartBadge.setVisible(true);
        } else {
            cartBadge.setVisible(false); // Masquer le badge si aucun article
        }
    }
    
 // Méthode pour initialiser la pastille rouge au chargement
    private void initializeCartBadge() {
        int userId = SessionManager.getCurrentUser().getId(); // Obtenir l'ID utilisateur
        OrderDAO orderDAO = new OrderDAO();
        OrderItemDAO orderItemDAO = new OrderItemDAO();

        try {
            // Vérifier si une commande "in progress" existe pour l'utilisateur
            Order inProgressOrder = orderDAO.getInProgressOrder(userId);
            if (inProgressOrder != null) {
                // Calculer le total des quantités dans la commande
                int totalItems = orderItemDAO.getOrderItems(inProgressOrder.getOrderId())
                        .stream()
                        .mapToInt(OrderItem::getQuantity)
                        .sum();
                updateCartBadge(totalItems); // Mettre à jour la pastille avec le total
            } else {
                updateCartBadge(0); // Pas de commande "in progress", afficher 0
            }
        } catch (SQLException e) {
            e.printStackTrace();
            updateCartBadge(0); // En cas d'erreur, afficher 0
        }
    }
}
