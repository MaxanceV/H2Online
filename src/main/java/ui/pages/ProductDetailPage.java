package ui.pages;

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
import sqlbdd.ProductSQL;
import tools.CartUtils;
import tools.SessionManager;
import ui.elements.ProductCard;

import java.util.List;

public class ProductDetailPage {
    private BorderPane layout;
    private Scene scene;

    public ProductDetailPage(Product product) {
        layout = new BorderPane();

        VBox mainContent = new VBox(20); 
        mainContent.setPadding(new Insets(20)); 
        mainContent.setAlignment(Pos.TOP_LEFT);

        VBox leftCenterSection = new VBox(10);
        leftCenterSection.setAlignment(Pos.TOP_LEFT);

        Label productName = new Label(product.getName());
        productName.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        ImageView productImage = new ImageView(new Image(getClass().getResourceAsStream("/images/products/" + product.getImage())));
        productImage.setFitWidth(300);
        productImage.setPreserveRatio(true);

        Label description = new Label(product.getDescription());
        description.setWrapText(true);

        product.reloadDetails();
        Label brandsLabel = new Label("Brand(s): " + String.join(", ", product.getBrands()));
        Label categoriesLabel = new Label("Category(s): " + String.join(", ", product.getCategories()));

        leftCenterSection.getChildren().addAll(productName, productImage, description, brandsLabel, categoriesLabel);

        VBox rightSection = new VBox(10);
        rightSection.setAlignment(Pos.TOP_LEFT);

        if (product.getStockQuantity() > 0) {
            Label quantityLabel = new Label("Quantity:");
            Spinner<Integer> quantitySpinner = new Spinner<>();
            SpinnerValueFactory<Integer> valueFactory =
                    new SpinnerValueFactory.IntegerSpinnerValueFactory(1, product.getStockQuantity(), 1);
            quantitySpinner.setValueFactory(valueFactory);

            javafx.scene.control.Button addToCartButton = new javafx.scene.control.Button("Add to cart");
            addToCartButton.setOnAction(e -> {
                CartUtils.addToCart(SessionManager.getCurrentUser().getId(), product, quantitySpinner.getValue());
            });

            rightSection.getChildren().addAll(quantityLabel, quantitySpinner, addToCartButton);
        } else {
            Label outOfStockLabel = new Label("Out of stock");
            outOfStockLabel.setStyle("-fx-text-fill: red; -fx-font-size: 16px; -fx-font-weight: bold;");
            rightSection.getChildren().add(outOfStockLabel);
        }
        
        Button viewCartButton = new Button("View Cart");
        viewCartButton.setOnAction(e -> {
            SessionManager.getMainLayout().setContent(new CartPage().getView());
        });

        rightSection.getChildren().add(viewCartButton);


        HBox contentSections = new HBox(20);
        contentSections.getChildren().addAll(leftCenterSection, rightSection);
        mainContent.getChildren().add(contentSections);

        VBox recommendationsBox = new VBox(10);
        recommendationsBox.setAlignment(Pos.TOP_LEFT);

        Label recommendationsTitle = new Label("You might also like");
        recommendationsTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        List<Product> recommendations = getRecommendations(product); 
        HBox recommendedProducts = new HBox(10);
        recommendedProducts.setAlignment(Pos.CENTER_LEFT);

        for (Product recommendedProduct : recommendations) {
            ProductCard recommendedCard = new ProductCard(recommendedProduct);
            recommendedProducts.getChildren().add(recommendedCard);
        }

        recommendationsBox.getChildren().addAll(recommendationsTitle, recommendedProducts);
        mainContent.getChildren().add(recommendationsBox);

        ScrollPane scrollPane = new ScrollPane(mainContent);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);

        layout.setCenter(scrollPane);

        

        scene = new Scene(layout, 800, 600);
    }

    private List<Product> getRecommendations(Product product) {
        ProductSQL productDAO = new ProductSQL();
        try {
            return productDAO.getRecommendations(product);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return List.of(); 
    }


    public BorderPane getView() {
        return layout;
    }
}
