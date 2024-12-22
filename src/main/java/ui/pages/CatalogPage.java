package ui.pages;

import dao.BrandDAO;
import dao.CategoryDAO;
import dao.ProductDAO;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import models.Product;
import ui.elements.CatalogFilter;
import ui.elements.ProductCard;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        BrandDAO brandDAO = new BrandDAO();
        CategoryDAO categoryDAO = new CategoryDAO();

        try {
            List<Product> products = productDAO.getAllProducts();

            // Préparer une liste des IDs de produits pour récupérer leurs marques et catégories
            List<Integer> productIds = products.stream().map(Product::getProductId).collect(Collectors.toList());

            // Récupérer les marques et catégories pour les produits
            Map<Integer, List<String>> brandsByProduct = brandDAO.getBrandsForProducts(productIds);
            Map<Integer, List<String>> categoriesByProduct = categoryDAO.getCategoriesForProducts(productIds);

            // Ajouter les marques et catégories aux objets produits
            for (Product product : products) {
            	product.setBrands(brandsByProduct.getOrDefault(product.getProductId(), Collections.emptyList()));
            	product.setCategories(categoriesByProduct.getOrDefault(product.getProductId(), Collections.emptyList()));

//            	System.out.println("Loaded brands for product " + product.getProductId() + ": " + product.getBrands());
//            	System.out.println("Loaded categories for product " + product.getProductId() + ": " + product.getCategories());


                // Créer une carte pour le produit
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

//        System.out.println("Filters applied:");
//        System.out.println("Search: " + searchQuery);
//        System.out.println("Selected Brands: " + selectedBrands);
//        System.out.println("Selected Categories: " + selectedCategories);
//        System.out.println("Price range: " + minPrice + " - " + maxPrice);

        productPane.getChildren().clear();
        ProductDAO productDAO = new ProductDAO();
        BrandDAO brandDAO = new BrandDAO();
        CategoryDAO categoryDAO = new CategoryDAO();

        try {
            List<Product> products = productDAO.getAllProducts();

            // Charger les marques et catégories pour les produits
            List<Integer> productIds = products.stream().map(Product::getProductId).collect(Collectors.toList());
            Map<Integer, List<String>> brandsByProduct = brandDAO.getBrandsForProducts(productIds);
            Map<Integer, List<String>> categoriesByProduct = categoryDAO.getCategoriesForProducts(productIds);

            for (Product product : products) {
                product.setBrands(brandsByProduct.getOrDefault(product.getProductId(), new ArrayList<>()));
                product.setCategories(categoriesByProduct.getOrDefault(product.getProductId(), new ArrayList<>()));

//                System.out.println("Evaluating product: " + product.getName());
//                System.out.println("Product brands: " + product.getBrands());
//                System.out.println("Product categories: " + product.getCategories());

                if (isProductMatchingFilters(product, searchQuery, selectedBrands, selectedCategories, minPrice, maxPrice)) {
                    ProductCard card = new ProductCard(
                        product,
                        () -> showProductDetails(product),
                        () -> addToCart(product)
                    );
                    productPane.getChildren().add(card);
                } else {
                    System.out.println("Product did not match filters: " + product.getName());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean isProductMatchingFilters(Product product, String searchQuery, List<String> selectedBrands, List<String> selectedCategories, double minPrice, double maxPrice) {
        boolean matchesSearch = searchQuery == null || searchQuery.isEmpty() ||
                product.getName().toLowerCase().contains(searchQuery.toLowerCase()) ||
                product.getDescription().toLowerCase().contains(searchQuery.toLowerCase());

        boolean matchesBrand = selectedBrands.isEmpty() || 
                product.getBrands().stream().anyMatch(selectedBrands::contains);

        boolean matchesCategory = selectedCategories.isEmpty() || 
                product.getCategories().stream().anyMatch(selectedCategories::contains);

        boolean matchesPrice = product.getPrice().doubleValue() >= minPrice && product.getPrice().doubleValue() <= maxPrice;

//        System.out.println("Product: " + product.getName());
//        System.out.println("Matches search: " + matchesSearch);
//        System.out.println("Matches brand: " + matchesBrand);
//        System.out.println("Matches category: " + matchesCategory);
//        System.out.println("Matches price: " + matchesPrice);

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
