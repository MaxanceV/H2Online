package ui;

import dao.ProductDAO;
import javafx.scene.layout.FlowPane;
import models.Product;
import tools.ProductCard;

import java.sql.SQLException;
import java.util.List;

public class CatalogPage {
    private FlowPane view;

    public CatalogPage() {
        view = new FlowPane();
        view.setHgap(10);
        view.setVgap(10);

        ProductDAO productDAO = new ProductDAO();
        try {
            List<Product> products = productDAO.getAllProducts();
            for (Product product : products) {
                ProductCard card = new ProductCard(
                    product,
                    () -> showProductDetails(product), // Callback pour cliquer sur l'image
                    () -> addToCart(product)          // Callback pour ajouter au panier
                );
                view.getChildren().add(card);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public FlowPane getView() {
        return view;
    }

    private void showProductDetails(Product product) {
        System.out.println("Afficher les détails pour : " + product.getName());
        // Ajouter le code pour afficher une page détaillée du produit
    }

    private void addToCart(Product product) {
        System.out.println("Produit ajouté au panier : " + product.getName());
        // Ajouter le code pour gérer l'ajout au panier
    }
}
