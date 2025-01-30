package ui.pages;

import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import models.Product;
import sqlbdd.BrandSQL;
import sqlbdd.CategorySQL;
import sqlbdd.ProductSQL;
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
    private ScrollPane scrollPane; 

    public CatalogPage() {
        view = new BorderPane();

        productPane = new FlowPane();
        productPane.setHgap(10);
        productPane.setVgap(10);

        scrollPane = new ScrollPane(productPane);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true); 
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED); 
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); 

        catalogFilter = new CatalogFilter();
        catalogFilter.getApplyFiltersButton().setOnAction(e -> applyFilters());
        catalogFilter.getSearchButton().setOnAction(e -> applyFilters());

        loadProducts();

        view.setCenter(scrollPane);
        view.setLeft(catalogFilter.getView()); 
    }

    public BorderPane getView() {
        return view;
    }

    private void loadProducts() {
        productPane.getChildren().clear();
        ProductSQL productDAO = new ProductSQL();
        BrandSQL brandDAO = new BrandSQL();
        CategorySQL categoryDAO = new CategorySQL();

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
        double minVolume = catalogFilter.getMinVolume();
        double maxVolume = catalogFilter.getMaxVolume();


        productPane.getChildren().clear();
        ProductSQL productDAO = new ProductSQL();
        BrandSQL brandDAO = new BrandSQL();
        CategorySQL categoryDAO = new CategorySQL();

        try {
            List<Product> products = productDAO.getAllProducts();

            // Charger les marques et catégories pour les produits
            List<Integer> productIds = products.stream().map(Product::getProductId).collect(Collectors.toList());
            Map<Integer, List<String>> brandsByProduct = brandDAO.getBrandsForProducts(productIds);
            Map<Integer, List<String>> categoriesByProduct = categoryDAO.getCategoriesForProducts(productIds);

            for (Product product : products) {
                product.setBrands(brandsByProduct.getOrDefault(product.getProductId(), new ArrayList<>()));
                product.setCategories(categoriesByProduct.getOrDefault(product.getProductId(), new ArrayList<>()));

                if (isProductMatchingFilters(product, searchQuery, selectedBrands, selectedCategories, minPrice, maxPrice, minVolume, maxVolume)) {
                    ProductCard card = new ProductCard(product);
                    productPane.getChildren().add(card);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean isProductMatchingFilters(Product product, String searchQuery, List<String> selectedBrands, List<String> selectedCategories, double minPrice, double maxPrice, double minVolume, double maxVolume) {
        boolean matchesSearch = searchQuery == null || searchQuery.isEmpty() ||
                product.getName().toLowerCase().contains(searchQuery.toLowerCase()) ||
                product.getDescription().toLowerCase().contains(searchQuery.toLowerCase());

        boolean matchesBrand = selectedBrands.isEmpty() ||
                product.getBrands().stream().anyMatch(selectedBrands::contains);

        boolean matchesCategory = selectedCategories.isEmpty() ||
                product.getCategories().stream().anyMatch(selectedCategories::contains);

        boolean matchesPrice = product.getPrice().doubleValue() >= minPrice && product.getPrice().doubleValue() <= maxPrice;

        boolean matchesVolume = product.getVolumePerBottle().doubleValue() >= minVolume && product.getVolumePerBottle().doubleValue() <= maxVolume;

        return matchesSearch && matchesBrand && matchesCategory && matchesPrice && matchesVolume;
    }

}
