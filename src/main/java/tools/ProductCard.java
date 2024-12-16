package tools;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import models.Product;

import java.io.File;
import java.nio.file.Paths;

public class ProductCard extends VBox {

    private static final String DEFAULT_IMAGE_PATH = "/images/products/defaut.png";

    public ProductCard(Product product, Runnable onImageClick, Runnable onAddToCart) {
        // Style général de la carte
        this.setAlignment(Pos.CENTER);
        this.setSpacing(10);
        this.setStyle("-fx-border-color: black; -fx-border-width: 1; -fx-padding: 10;");

        // Charger l'image du produit ou une image par défaut
        ImageView imageView = new ImageView(loadProductImage(product.getImage()));
        imageView.setFitWidth(100);
        imageView.setFitHeight(100);
        imageView.setPreserveRatio(true);

        // Ajouter une classe CSS à l'image pour l'effet de hover
        imageView.getStyleClass().add("product-image");

        // Ajouter un gestionnaire de clic pour l'image
        imageView.setOnMouseClicked(e -> onImageClick.run());

        // Nom et prix du produit
        Text name = new Text(product.getName());
        Text price = new Text(product.getPrice() + " €");

        // Bouton "Ajouter au panier"
        Button addToCartButton = new Button("Ajouter au panier");
        addToCartButton.setOnAction(e -> onAddToCart.run());

        // Ajouter les éléments au VBox
        this.getChildren().addAll(imageView, name, price, addToCartButton);
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
}
