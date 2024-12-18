package ui.pages;

import dao.ProductDAO;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import models.Product;
import ui.elements.CatalogFilter;
import ui.elements.ProductCard;

import java.sql.SQLException;
import java.util.List;

public class CatalogPage {
    private BorderPane view;
    private FlowPane productPane;
    private CatalogFilter catalogFilter;

    public CatalogPage() {
        view = new BorderPane();

        // Initialisation du panneau des produits
        productPane = new FlowPane();
        productPane.setHgap(10);
        productPane.setVgap(10);

        // Créer et configurer le filtre du catalogue
        catalogFilter = new CatalogFilter();
        catalogFilter.getApplyFiltersButton().setOnAction(e -> applyFilters());
        catalogFilter.getSearchButton().setOnAction(e -> applyFilters());

        // Charger les produits initialement
        loadProducts();

        // Ajouter les sections au layout principal
        view.setCenter(productPane);
        view.setLeft(catalogFilter.getView()); // Place le filtre à gauche
    }

    public BorderPane getView() {
        return view;
    }

    private void loadProducts() {
        productPane.getChildren().clear();
        ProductDAO productDAO = new ProductDAO();
        try {
            List<Product> products = productDAO.getAllProducts();
            for (Product product : products) {
                ProductCard card = new ProductCard(
                    product,
                    () -> showProductDetails(product), // Callback pour cliquer sur l'image
                    () -> addToCart(product)          // Callback pour ajouter au panier
                );
                productPane.getChildren().add(card);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void applyFilters() {
        String searchQuery = catalogFilter.getSearchQuery();
        List<String> selectedBrands = catalogFilter.getSelectedBrands();
        List<String> selectedCategories = catalogFilter.getSelectedCategories();
        double minPrice = catalogFilter.getMinPrice();
        double maxPrice = catalogFilter.getMaxPrice();

        System.out.println("Filters applied:");
        System.out.println("Search: " + searchQuery);
        System.out.println("Selected Brands: " + selectedBrands);
        System.out.println("Selected Categories: " + selectedCategories);
        System.out.println("Price range: " + minPrice + " - " + maxPrice);

        // Appliquer les filtres pour recharger les produits filtrés (exemple simplifié)
        productPane.getChildren().clear();
        ProductDAO productDAO = new ProductDAO();
        try {
            List<Product> products = productDAO.getAllProducts(); // Remplacer par une méthode filtrée si nécessaire
            for (Product product : products) {
                if (isProductMatchingFilters(product, searchQuery, selectedBrands, selectedCategories, minPrice, maxPrice)) {
                    ProductCard card = new ProductCard(
                        product,
                        () -> showProductDetails(product),
                        () -> addToCart(product)
                    );
                    productPane.getChildren().add(card);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean isProductMatchingFilters(Product product, String searchQuery, List<String> selectedBrands, List<String> selectedCategories, double minPrice, double maxPrice) {
        boolean matchesSearch = product.getName().toLowerCase().contains(searchQuery.toLowerCase()) ||
                                product.getDescription().toLowerCase().contains(searchQuery.toLowerCase());

        boolean matchesBrand = selectedBrands.isEmpty() || selectedBrands.contains("ALL") || selectedBrands.contains(product.getBrand());
        boolean matchesCategory = selectedCategories.isEmpty() || selectedCategories.contains("ALL") || selectedCategories.contains(product.getCategory());
        boolean matchesPrice = product.getPrice().doubleValue() >= minPrice && product.getPrice().doubleValue() <= maxPrice;

        return matchesSearch && matchesBrand && matchesCategory && matchesPrice;
    }

    private void showProductDetails(Product product) {
        System.out.println("Show details for: " + product.getName());
        // Ajouter le code pour afficher une page détaillée du produit
    }

    private void addToCart(Product product) {
        System.out.println("Added to cart: " + product.getName());
        // Ajouter le code pour gérer l'ajout au panier
    }
}
