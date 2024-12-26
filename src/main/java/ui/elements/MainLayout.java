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
import models.User;
import tools.SessionManager;
import ui.pages.CartPage;
import ui.pages.CatalogPage;
import ui.pages.LoginPage;
import ui.pages.ManageOrdersPage;
import ui.pages.ManageProductsPage;
import ui.pages.ManageUsersPage;
import ui.pages.OrderHistoryPage;
import ui.pages.UserSettingsPage;

public class MainLayout {
    private StackPane rootPane;
    private BorderPane root;
    private Scene scene;
    private MenuButton profileMenuButton;
    private MenuButton adminMenuButton; // Nouveau menu pour les tâches Admin
    private StackPane cartButton;
    private Label catalogButton;
    private Text cartBadge;

    public MainLayout(Stage primaryStage) {
        rootPane = new StackPane();
        root = new BorderPane();
        rootPane.getChildren().add(root);

        createHeader(primaryStage);
        createFooter();

        scene = new Scene(rootPane, 800, 600);
    }

    private void createHeader(Stage primaryStage) {
        BorderPane header = new BorderPane();
        header.setStyle("-fx-background-color: #333333; -fx-padding: 5;");

        // Logo et titre (à gauche)
        HBox leftSection = new HBox(10);
        leftSection.setAlignment(Pos.CENTER_LEFT);

        Image logoImage = new Image(getClass().getResourceAsStream("/images/logo/logo_goutte.png"));
        ImageView logo = new ImageView(logoImage);
        logo.setFitWidth(50);
        logo.setFitHeight(50);
        logo.setPreserveRatio(true);

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
        HBox rightSection = new HBox(10);
        rightSection.setAlignment(Pos.CENTER_RIGHT);

        // Bouton Panier
        cartButton = new StackPane();
        ImageView cartIcon = new ImageView(new Image(getClass().getResourceAsStream("/images/logo/pannier.png")));
        cartIcon.setFitWidth(30);
        cartIcon.setFitHeight(30);
        cartButton.getChildren().add(cartIcon);

        cartBadge = new Text("0");
        cartBadge.setStyle("-fx-fill: red; -fx-font-weight: bold; -fx-font-size: 14px;");
        StackPane.setAlignment(cartBadge, Pos.TOP_RIGHT);
        cartBadge.setVisible(false);
        cartButton.getChildren().add(cartBadge);

        cartButton.setOnMouseClicked(e -> setContent(new CartPage().getView()));

        // Menu déroulant pour le profil
        profileMenuButton = new MenuButton();
        ImageView profileIcon = new ImageView(new Image(getClass().getResourceAsStream("/images/logo/profil.png")));
        profileIcon.setFitWidth(30);
        profileIcon.setFitHeight(30);
        profileMenuButton.setGraphic(profileIcon);

        MenuItem orderHistory = new MenuItem("Order History");
        orderHistory.setOnAction(e -> setContent(new OrderHistoryPage().getView()));

        MenuItem userSettingsItem = new MenuItem("User Settings");
        userSettingsItem.setOnAction(e -> setContent(new UserSettingsPage().getView()));

        MenuItem logoutItem = new MenuItem("Logout");
        logoutItem.setOnAction(e -> {
            System.out.println("Déconnexion...");
            SessionManager.clearSession();
            disableMenu();
            setContent(new LoginPage(this).getView());
        });

        profileMenuButton.getItems().addAll(orderHistory, userSettingsItem, logoutItem);

        // Menu déroulant pour les tâches Admin (visible uniquement pour les admins)
        adminMenuButton = new MenuButton("Admin Tasks");
        adminMenuButton.setStyle("-fx-text-fill: white;");
        adminMenuButton.setVisible(false); // Par défaut, caché

        MenuItem manageOrders = new MenuItem("Manage Orders");
        manageOrders.setOnAction(e -> {
        	setContent(new ManageOrdersPage().getView());
        });

        MenuItem manageProducts = new MenuItem("Manage Products");
        manageProducts.setOnAction(e -> {
        	setContent(new ManageProductsPage().getView());
        });

        MenuItem manageUsers = new MenuItem("Manage Users");
        manageUsers.setOnAction(e -> {
            setContent(new ManageUsersPage().getView());
        });

        adminMenuButton.getItems().addAll(manageOrders, manageProducts, manageUsers);

        // Ajouter les boutons au panneau de droite
        rightSection.getChildren().addAll(cartButton, profileMenuButton, adminMenuButton);

        header.setLeft(leftSection);
        header.setCenter(centerSection);
        header.setRight(rightSection);

        root.setTop(header);
    }

    private void createFooter() {
        HBox footer = new HBox();
        footer.setAlignment(Pos.CENTER);
        footer.setStyle("-fx-background-color: #333333; -fx-padding: 10;");

        Label footerLabel = new Label("© 2024 H2Online - All rights reserved");
        footerLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");

        footer.getChildren().add(footerLabel);
        root.setBottom(footer);
    }

    public void enableMenu() {
        profileMenuButton.setDisable(false);
        profileMenuButton.setVisible(true);
        cartButton.setVisible(true);
        catalogButton.setVisible(true);
        initializeCartBadge();

        // Rendre le menu Admin Tasks visible si l'utilisateur est un admin
        User currentUser = SessionManager.getCurrentUser();
        if (currentUser != null && currentUser.isAdmin()) {
            adminMenuButton.setVisible(true);
        }
    }

    public void disableMenu() {
        profileMenuButton.setDisable(true);
        profileMenuButton.setVisible(false);
        cartButton.setVisible(false);
        catalogButton.setVisible(false);
        adminMenuButton.setVisible(false); // Cacher le menu Admin Tasks
    }

    public void setContent(Node content) {
        root.setCenter(content);
    }

    public Scene getScene() {
        return scene;
    }

    public StackPane getRootPane() {
        return rootPane;
    }

    public void updateCartBadge(int itemCount) {
        if (itemCount > 0) {
            cartBadge.setText(String.valueOf(itemCount));
            cartBadge.setVisible(true);
        } else {
            cartBadge.setVisible(false);
        }
    }

    private void initializeCartBadge() {
        int userId = SessionManager.getCurrentUser().getId();
        OrderDAO orderDAO = new OrderDAO();
        OrderItemDAO orderItemDAO = new OrderItemDAO();

        try {
            Order inProgressOrder = orderDAO.getInProgressOrder(userId);
            if (inProgressOrder != null) {
                int totalItems = orderItemDAO.getOrderItems(inProgressOrder.getOrderId())
                        .stream()
                        .mapToInt(orderItem -> orderItem.getQuantity())
                        .sum();
                updateCartBadge(totalItems);
            } else {
                updateCartBadge(0);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            updateCartBadge(0);
        }
    }
}
