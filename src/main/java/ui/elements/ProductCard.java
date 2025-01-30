package ui.elements;

import java.io.File;
import java.nio.file.Paths;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import models.Product;
import tools.CartUtils;
import tools.SessionManager;
import ui.pages.ProductDetailPage;

public class ProductCard extends VBox {

    private static final String DEFAULT_IMAGE_PATH = "/images/products/defaut.png";
    private static final String CART_IMAGE_PATH = "/images/logo/pannier.png";

    public ProductCard(Product product) {
        this.setAlignment(Pos.CENTER);
        this.setSpacing(10);
        this.setStyle("-fx-border-color: black; -fx-border-width: 1; -fx-padding: 10;");

        ImageView productImageView = new ImageView(loadProductImage(product.getImage()));
        productImageView.setFitWidth(100);
        productImageView.setFitHeight(100);
        productImageView.setPreserveRatio(true);

        productImageView.setOnMouseEntered(e -> productImageView.setStyle("-fx-opacity: 0.8; -fx-cursor: hand;"));
        productImageView.setOnMouseExited(e -> productImageView.setStyle("-fx-opacity: 1;"));

        productImageView.setOnMouseClicked(e -> {
            MainLayout mainLayout = SessionManager.getMainLayout();
            if (mainLayout != null) {
                ProductDetailPage detailPage = new ProductDetailPage(product);
                mainLayout.setContent(detailPage.getView());
            } else {
                System.err.println("Error: MainLayout is not initialized!");
            }
        });

        String productName = product.getName().length() > 17 
                ? product.getName().substring(0, 17) + "..." 
                : product.getName();
        Text name = new Text(productName);
        name.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-fill: black;");
        name.setTextAlignment(TextAlignment.LEFT);

        Text volume = new Text(product.getVolumePerBottle() + "L");
        volume.setStyle("-fx-font-size: 12px; -fx-fill: gray;");
        volume.setTextAlignment(TextAlignment.LEFT);

        VBox nameVolumeBox = new VBox(2);
        nameVolumeBox.setAlignment(Pos.TOP_LEFT);
        nameVolumeBox.getChildren().addAll(name, volume);

        Text price = new Text(product.getPrice() + " €");
        price.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-fill: black;");
        price.setTextAlignment(TextAlignment.CENTER);

        if (product.getStockQuantity() > 0) {
            Button addToCartButton = new Button();
            ImageView cartImageView = new ImageView(loadCartImage());
            cartImageView.setFitWidth(20); // Taille du logo
            cartImageView.setFitHeight(20);
            cartImageView.setPreserveRatio(true);
            addToCartButton.setGraphic(cartImageView);

            Tooltip tooltip = new Tooltip("Add to cart");
            Tooltip.install(addToCartButton, tooltip);

            addToCartButton.setOnAction(e -> {
                CartUtils.addToCart(SessionManager.getCurrentUser().getId(), product, 1);
            });

            this.getChildren().addAll(productImageView, nameVolumeBox, price, addToCartButton);
        } else {
            Text outOfStock = new Text("Out of stock");
            outOfStock.setStyle("-fx-fill: red; -fx-font-weight: bold;");
            this.getChildren().addAll(productImageView, nameVolumeBox, price, outOfStock);
        }
    }

    private Image loadProductImage(String imageName) {
        String imagePath = "/images/products/" + imageName;
        try {
            File file = Paths.get(getClass().getResource(imagePath).toURI()).toFile();
            if (file.exists()) {
                return new Image(file.toURI().toString());
            }
        } catch (Exception e) {
            System.out.println("image not found for " + imageName + ". Use of default image.");
        }
        return new Image(getClass().getResourceAsStream(DEFAULT_IMAGE_PATH));
    }

    private Image loadCartImage() {
        try {
            return new Image(getClass().getResourceAsStream(CART_IMAGE_PATH));
        } catch (Exception e) {
            System.out.println("Cart image not found.");
            return null;
        }
    }
}
