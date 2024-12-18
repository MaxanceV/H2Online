package ui.elements;

import dao.CategoryDAO;
import dao.BrandDAO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.sql.SQLException;
import java.util.List;

public class CatalogFilter {
    private VBox view;

    // Composants de filtrage
    private TextField searchField;
    private Button searchButton;
    private ComboBox<String> brandFilter;
    private ComboBox<String> categoryFilter;
    private Slider minPriceSlider;
    private Slider maxPriceSlider;
    private Button applyFiltersButton;

    public CatalogFilter() {
        view = new VBox(10);
        view.setPadding(new Insets(10));

        // Champs de recherche textuelle
        searchField = new TextField();
        searchField.setPromptText("Search products...");
        searchButton = new Button("🔍");

        HBox searchBox = new HBox(5, searchField, searchButton);
        searchBox.setAlignment(Pos.CENTER_LEFT);

        // Filtres de marques
        Label brandLabel = new Label("Brands");
        brandFilter = new ComboBox<>();
        brandFilter.getItems().add("ALL"); // Option ALL
        brandFilter.setVisibleRowCount(5); // Limite la hauteur
        brandFilter.setPromptText("Select brands...");
        loadBrands();

        // Filtres de catégories
        Label categoryLabel = new Label("Categories");
        categoryFilter = new ComboBox<>();
        categoryFilter.getItems().add("ALL"); // Option ALL
        categoryFilter.setVisibleRowCount(5); // Limite la hauteur
        categoryFilter.setPromptText("Select categories...");
        loadCategories();

        // Filtres de prix
        Label priceLabel = new Label("Price range");
        minPriceSlider = new Slider(0, 100, 0);
        maxPriceSlider = new Slider(0, 100, 100);
        minPriceSlider.setShowTickLabels(true);
        maxPriceSlider.setShowTickLabels(true);
        minPriceSlider.setShowTickMarks(true);
        maxPriceSlider.setShowTickMarks(true);
        HBox priceBox = new HBox(10, new Label("Min:"), minPriceSlider, new Label("Max:"), maxPriceSlider);
        priceBox.setAlignment(Pos.CENTER);

        // Bouton pour appliquer les filtres
        applyFiltersButton = new Button("Apply Filters");

        // Ajout des éléments au filtre
        view.getChildren().addAll(
            searchBox,
            brandLabel, brandFilter,
            categoryLabel, categoryFilter,
            priceLabel, priceBox,
            applyFiltersButton
        );
    }

    public VBox getView() {
        return view;
    }

    private void loadBrands() {
        try {
            BrandDAO brandDAO = new BrandDAO();
            List<String> brands = brandDAO.getAllBrandNames();
            brandFilter.getItems().addAll(brands);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadCategories() {
        try {
            CategoryDAO categoryDAO = new CategoryDAO();
            List<String> categories = categoryDAO.getAllCategoryNames();
            categoryFilter.getItems().addAll(categories);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Getters for the filter values
    public String getSearchQuery() {
        return searchField.getText();
    }

    public List<String> getSelectedBrands() {
        return List.of(brandFilter.getValue());
    }

    public List<String> getSelectedCategories() {
        return List.of(categoryFilter.getValue());
    }

    public double getMinPrice() {
        return minPriceSlider.getValue();
    }

    public double getMaxPrice() {
        return maxPriceSlider.getValue();
    }

    public Button getApplyFiltersButton() {
        return applyFiltersButton;
    }

    public Button getSearchButton() {
        return searchButton;
    }
}
