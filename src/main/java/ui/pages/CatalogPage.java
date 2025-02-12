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

/**
 * Represents a catalog page that displays products in a FlowPane. Provides
 * filtering functionality via a {@link CatalogFilter} and updates the displayed
 * products accordingly.
 */
public class CatalogPage {
    private BorderPane view;
    private FlowPane productPane;
    private CatalogFilter catalogFilter;
    private ScrollPane scrollPane;

    /**
     * Constructs the {@code CatalogPage}, initializing the layout,
     * {@link CatalogFilter}, and loading the products to display.
     */
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

    /**
     * Retrieves the main layout for this page.
     *
     * @return The {@link BorderPane} containing the catalog layout.
     */
    public BorderPane getView() {
        return view;
    }

    /**
     * Loads all products from the database, retrieves their associated brands and
     * categories, and displays them in the {@link FlowPane} as {@link ProductCard}s.
     */
    private void loadProducts() {
        productPane.getChildren().clear();
        ProductSQL productDAO = new ProductSQL();
        BrandSQL brandDAO = new BrandSQL();
        CategorySQL categoryDAO = new CategorySQL();

        try {
            List<Product> products = productDAO.getAllProducts();

            // Prepare a list of product IDs to retrieve brands and categories
            List<Integer> productIds = products.stream().map(Product::getProductId).collect(Collectors.toList());

            // Retrieve the brands and categories for the products
            Map<Integer, List<String>> brandsByProduct = brandDAO.getBrandsForProducts(productIds);
            Map<Integer, List<String>> categoriesByProduct = categoryDAO.getCategoriesForProducts(productIds);

            // Set the brands and categories on each product and add to the UI
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

    /**
     * Applies the filters specified in the {@link CatalogFilter} to the products,
     * and updates the {@link FlowPane} to display only matching items.
     */
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

            // Load brands and categories for the products
            List<Integer> productIds = products.stream().map(Product::getProductId).collect(Collectors.toList());
            Map<Integer, List<String>> brandsByProduct = brandDAO.getBrandsForProducts(productIds);
            Map<Integer, List<String>> categoriesByProduct = categoryDAO.getCategoriesForProducts(productIds);

            for (Product product : products) {
                product.setBrands(brandsByProduct.getOrDefault(product.getProductId(), new ArrayList<>()));
                product.setCategories(categoriesByProduct.getOrDefault(product.getProductId(), new ArrayList<>()));

                if (isProductMatchingFilters(product, searchQuery, selectedBrands, selectedCategories,
                                             minPrice, maxPrice, minVolume, maxVolume)) {
                    ProductCard card = new ProductCard(product);
                    productPane.getChildren().add(card);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Determines whether a product matches the given filter criteria for search query,
     * selected brands, selected categories, price range, and volume range.
     *
     * @param product The product to test against the filter criteria.
     * @param searchQuery The search text to filter by name or description.
     * @param selectedBrands The list of selected brand names.
     * @param selectedCategories The list of selected category names.
     * @param minPrice The minimum price to filter.
     * @param maxPrice The maximum price to filter.
     * @param minVolume The minimum volume to filter.
     * @param maxVolume The maximum volume to filter.
     * @return True if the product matches all the specified filters; false otherwise.
     */
    private boolean isProductMatchingFilters(
            Product product,
            String searchQuery,
            List<String> selectedBrands,
            List<String> selectedCategories,
            double minPrice,
            double maxPrice,
            double minVolume,
            double maxVolume
    ) {
        boolean matchesSearch = searchQuery == null || searchQuery.isEmpty() ||
                product.getName().toLowerCase().contains(searchQuery.toLowerCase()) ||
                product.getDescription().toLowerCase().contains(searchQuery.toLowerCase());

        boolean matchesBrand = selectedBrands.isEmpty() ||
                product.getBrands().stream().anyMatch(selectedBrands::contains);

        boolean matchesCategory = selectedCategories.isEmpty() ||
                product.getCategories().stream().anyMatch(selectedCategories::contains);

        boolean matchesPrice = product.getPrice().doubleValue() >= minPrice
                               && product.getPrice().doubleValue() <= maxPrice;

        boolean matchesVolume = product.getVolumePerBottle().doubleValue() >= minVolume
                                && product.getVolumePerBottle().doubleValue() <= maxVolume;

        return matchesSearch && matchesBrand && matchesCategory && matchesPrice && matchesVolume;
    }
}
