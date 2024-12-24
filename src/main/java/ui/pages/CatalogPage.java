package ui.pages;

import dao.BrandDAO;
import dao.CategoryDAO;
import dao.ProductDAO;
import javafx.scene.control.ScrollPane;
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
    private ScrollPane scrollPane; // Ajout du ScrollPane

    public CatalogPage() {
        view = new BorderPane();

        // Initialisation du panneau des produits
        productPane = new FlowPane();
        productPane.setHgap(10);
        productPane.setVgap(10);

        // Encapsuler le FlowPane dans un ScrollPane
        scrollPane = new ScrollPane(productPane);
        scrollPane.setFitToWidth(true); // Ajuste la largeur pour correspondre au contenu
        scrollPane.setFitToHeight(true); // Ajuste la hauteur pour correspondre au contenu
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED); // Activer la barre verticale si nécessaire
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); // Désactiver la barre horizontale

        // Créer et configurer le filtre du catalogue
        catalogFilter = new CatalogFilter();
        catalogFilter.getApplyFiltersButton().setOnAction(e -> applyFilters());
        catalogFilter.getSearchButton().setOnAction(e -> applyFilters());

        // Charger les produits initialement
        loadProducts();

        // Ajouter les sections au layout principal
        view.setCenter(scrollPane); // Utiliser le ScrollPane comme contenu central
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

                // Créer une carte pour le produit
                ProductCard card = new ProductCard(product);
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

                if (isProductMatchingFilters(product, searchQuery, selectedBrands, selectedCategories, minPrice, maxPrice)) {
                    ProductCard card = new ProductCard(product);
                    productPane.getChildren().add(card);
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

        return matchesSearch && matchesBrand && matchesCategory && matchesPrice;
    }
}
