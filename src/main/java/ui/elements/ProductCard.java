package ui.elements;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import models.Product;
import tools.SessionManager;
import ui.pages.ProductDetail;

import java.io.File;
import java.nio.file.Paths;

public class ProductCard extends VBox {

    private static final String DEFAULT_IMAGE_PATH = "/images/products/defaut.png";
    private static final String CART_IMAGE_PATH = "/images/logo/pannier.png";

    public ProductCard(Product product) {
        // Style général de la carte
        this.setAlignment(Pos.CENTER);
        this.setSpacing(10);
        this.setStyle("-fx-border-color: black; -fx-border-width: 1; -fx-padding: 10;");

        // Charger l'image du produit ou une image par défaut
        ImageView productImageView = new ImageView(loadProductImage(product.getImage()));
        productImageView.setFitWidth(100);
        productImageView.setFitHeight(100);
        productImageView.setPreserveRatio(true);

        // Ajouter un effet de hover sur l'image
        productImageView.setOnMouseEntered(e -> productImageView.setStyle("-fx-opacity: 0.8; -fx-cursor: hand;"));
        productImageView.setOnMouseExited(e -> productImageView.setStyle("-fx-opacity: 1;"));

        // Ajouter un gestionnaire de clic pour l'image
        productImageView.setOnMouseClicked(e -> {
            MainLayout mainLayout = SessionManager.getMainLayout();
            if (mainLayout != null) {
                ProductDetail detailPage = new ProductDetail(product);
                mainLayout.setContent(detailPage.getView());
            } else {
                System.err.println("Error: MainLayout is not initialized!");
            }
        });


        // Nom et prix du produit
        Text name = new Text(product.getName());
        Text price = new Text(product.getPrice() + " €");

        // Vérifier le stock du produit
        if (product.getStockQuantity() > 0) {
            // Bouton "Ajouter au panier" avec l'image
            Button addToCartButton = new Button();
            ImageView cartImageView = new ImageView(loadCartImage());
            cartImageView.setFitWidth(20); // Taille du logo
            cartImageView.setFitHeight(20);
            cartImageView.setPreserveRatio(true);
            addToCartButton.setGraphic(cartImageView);

            // Ajouter un tooltip au bouton
            Tooltip tooltip = new Tooltip("Add to cart");
            Tooltip.install(addToCartButton, tooltip);

            // Action du bouton
            addToCartButton.setOnAction(e -> {
                // Ajouter au panier (à implémenter selon votre logique)
                System.out.println("Ajouté au panier : " + product.getName());
            });

            // Ajouter le bouton au VBox
            this.getChildren().addAll(productImageView, name, price, addToCartButton);
        } else {
            // Afficher "Out of stock" si le stock est insuffisant
            Text outOfStock = new Text("Out of stock");
            outOfStock.setStyle("-fx-fill: red; -fx-font-weight: bold;");
            this.getChildren().addAll(productImageView, name, price, outOfStock);
        }
    }

    private Image loadProductImage(String imageName) {
        // Chemin complet de l'image
        String imagePath = "/images/products/" + imageName;
        try {
            // Vérifier si l'image existe
            File file = Paths.get(getClass().getResource(imagePath).toURI()).toFile();
            if (file.exists()) {
                return new Image(file.toURI().toString());
            }
        } catch (Exception e) {
            // En cas d'erreur, utiliser l'image par défaut
            System.out.println("Image introuvable pour " + imageName + ". Utilisation de l'image par défaut.");
        }
        // Charger l'image par défaut
        return new Image(getClass().getResourceAsStream(DEFAULT_IMAGE_PATH));
    }

    private Image loadCartImage() {
        try {
            return new Image(getClass().getResourceAsStream(CART_IMAGE_PATH));
        } catch (Exception e) {
            System.out.println("Image du panier introuvable. Assurez-vous que le fichier est présent dans le dossier.");
            return null;
        }
    }
}
