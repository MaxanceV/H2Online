package ui.pages;

import dao.ProductDAO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import models.Product;
import tools.CartUtils;
import tools.SessionManager;
import ui.elements.ProductCard;

import java.util.List;

public class ProductDetail {
    private BorderPane layout;
    private Scene scene;

    public ProductDetail(Product product) {
        layout = new BorderPane();

        // Conteneur principal pour tout le contenu de la page (à placer dans un ScrollPane)
        VBox mainContent = new VBox(20); // Espacement entre les sections
        mainContent.setPadding(new Insets(20)); // Marges autour du contenu
        mainContent.setAlignment(Pos.TOP_LEFT);

        // Partie gauche et centre
        VBox leftCenterSection = new VBox(10);
        leftCenterSection.setAlignment(Pos.TOP_LEFT);

        // Nom du produit
        Label productName = new Label(product.getName());
        productName.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        // Image du produit
        ImageView productImage = new ImageView(new Image(getClass().getResourceAsStream("/images/products/" + product.getImage())));
        productImage.setFitWidth(300);
        productImage.setPreserveRatio(true);

        // Description
        Label description = new Label(product.getDescription());
        description.setWrapText(true);

        product.reloadDetails();
        // Marques et catégories
        Label brandsLabel = new Label("Brand(s): " + String.join(", ", product.getBrands()));
        Label categoriesLabel = new Label("Category(s): " + String.join(", ", product.getCategories()));

        leftCenterSection.getChildren().addAll(productName, productImage, description, brandsLabel, categoriesLabel);

        // Partie droite : Sélecteur de quantité et bouton "Add to cart"
        VBox rightSection = new VBox(10);
        rightSection.setAlignment(Pos.TOP_LEFT);

        if (product.getStockQuantity() > 0) {
            // Sélecteur de quantité
            Label quantityLabel = new Label("Quantity:");
            Spinner<Integer> quantitySpinner = new Spinner<>();
            SpinnerValueFactory<Integer> valueFactory =
                    new SpinnerValueFactory.IntegerSpinnerValueFactory(1, product.getStockQuantity(), 1);
            quantitySpinner.setValueFactory(valueFactory);

            // Bouton "Add to cart"
            javafx.scene.control.Button addToCartButton = new javafx.scene.control.Button("Add to cart");
            addToCartButton.setOnAction(e -> {
                CartUtils.addToCart(SessionManager.getCurrentUser().getId(), product, quantitySpinner.getValue());
            });

            rightSection.getChildren().addAll(quantityLabel, quantitySpinner, addToCartButton);
        } else {
            // Produit en rupture de stock
            Label outOfStockLabel = new Label("Out of stock");
            outOfStockLabel.setStyle("-fx-text-fill: red; -fx-font-size: 16px; -fx-font-weight: bold;");
            rightSection.getChildren().add(outOfStockLabel);
        }
        
        // Bouton pour voir le panier
        Button viewCartButton = new Button("View Cart");
        viewCartButton.setOnAction(e -> {
            SessionManager.getMainLayout().setContent(new CartPage().getView());
        });

        // Ajouter le bouton dans tous les cas (qu'il y ait du stock ou non)
        rightSection.getChildren().add(viewCartButton);


        // Ajouter les sections gauche et droite au contenu principal
        HBox contentSections = new HBox(20); // Espacement entre les sections
        contentSections.getChildren().addAll(leftCenterSection, rightSection);
        mainContent.getChildren().add(contentSections);

        // Recommandations (en bas)
        VBox recommendationsBox = new VBox(10);
        recommendationsBox.setAlignment(Pos.TOP_LEFT);

        Label recommendationsTitle = new Label("You might also like");
        recommendationsTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        List<Product> recommendations = getRecommendations(product); // Obtenir les recommandations
        HBox recommendedProducts = new HBox(10);
        recommendedProducts.setAlignment(Pos.CENTER_LEFT);

        for (Product recommendedProduct : recommendations) {
            ProductCard recommendedCard = new ProductCard(recommendedProduct);
            recommendedProducts.getChildren().add(recommendedCard);
        }

        recommendationsBox.getChildren().addAll(recommendationsTitle, recommendedProducts);
        mainContent.getChildren().add(recommendationsBox);

        // Mettre le contenu principal dans un ScrollPane
        ScrollPane scrollPane = new ScrollPane(mainContent);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);

        // Ajouter le ScrollPane à la partie centrale
        layout.setCenter(scrollPane);

        

        scene = new Scene(layout, 800, 600); // Taille par défaut
    }

    // Fonction pour obtenir des recommandations (produits de mêmes catégories ou marques)
    private List<Product> getRecommendations(Product product) {
        ProductDAO productDAO = new ProductDAO();
        try {
            return productDAO.getRecommendations(product);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return List.of(); // Retourne une liste vide en cas d'erreur
    }


    // Méthode pour obtenir la vue principale
    public BorderPane getView() {
        return layout;
    }
}
