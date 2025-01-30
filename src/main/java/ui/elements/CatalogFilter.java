package ui.elements;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import sqlbdd.BrandSQL;
import sqlbdd.CategorySQL;

import org.controlsfx.control.RangeSlider;

import java.sql.SQLException;
import java.util.List;

public class CatalogFilter {
    private GridPane view;

    private TextField searchField;
    private Button searchButton;
    private ScrollPane brandFilterScrollPane; 
    private VBox brandFilter; 
    private ScrollPane categoryFilterScrollPane; 
    private VBox categoryFilter; 
    private RangeSlider priceRangeSlider; 
    private RangeSlider volumeRangeSlider;
    private Button applyFiltersButton;
    

    public CatalogFilter() {
        view = new GridPane();
        view.setPadding(new Insets(5));
        view.setHgap(5);
        view.setVgap(10);

        searchField = new TextField();
        searchField.setPromptText("Search products...");
        searchButton = new Button("🔍");

        HBox searchBox = new HBox(5, searchField, searchButton);
        searchBox.setAlignment(Pos.CENTER_LEFT);

        // Filtres de marques
        Label brandLabel = new Label("Brands");
        brandFilter = new VBox(5); // Espacement entre les CheckBoxes
        loadBrands();
        brandFilterScrollPane = createScrollablePane(brandFilter, 150); // Limite de hauteur : 150px

        // Filtres de catégories
        Label categoryLabel = new Label("Categories");
        categoryFilter = new VBox(5);
        loadCategories();
        categoryFilterScrollPane = createScrollablePane(categoryFilter, 150); // Limite de hauteur : 150px

        // Slider pour plage de prix
        Label priceLabel = new Label("Price range");
        priceRangeSlider = new RangeSlider(0, 50, 0, 50); // Plage de 0 à 50
        priceRangeSlider.setShowTickLabels(true);
        priceRangeSlider.setShowTickMarks(true);

        VBox priceBox = new VBox(5, priceLabel, priceRangeSlider);
        priceBox.setAlignment(Pos.CENTER_LEFT);
        
        // Ajout du filtre de volume dans le constructeur
        Label volumeLabel = new Label("Volume range (L)");
        volumeRangeSlider = new RangeSlider(0.0, 2.0, 0.0, 2.0); // Plage par défaut de 0L à 2L
        volumeRangeSlider.setShowTickLabels(true);
        volumeRangeSlider.setShowTickMarks(true);
        volumeRangeSlider.setMajorTickUnit(0.5); // Graduation toutes les 0.5L
        volumeRangeSlider.setBlockIncrement(0.1); // Incrément de 0.1L

        VBox volumeBox = new VBox(5, volumeLabel, volumeRangeSlider);
        volumeBox.setAlignment(Pos.CENTER_LEFT);

        // Bouton pour appliquer les filtres
        applyFiltersButton = new Button("Apply Filters");

        // Ajout des éléments à la vue
        view.add(searchBox, 0, 0, 1, 1);
        view.add(brandLabel, 0, 1);
        view.add(brandFilterScrollPane, 0, 2);
        view.add(categoryLabel, 0, 3);
        view.add(categoryFilterScrollPane, 0, 4);
        view.add(priceBox, 0, 5, 1, 1);
        view.add(volumeBox, 0, 6, 1, 1); 
        view.add(applyFiltersButton, 0, 7, 1, 1);
    }

    public GridPane getView() {
        return view;
    }

    private void loadBrands() {
        try {
            BrandSQL brandDAO = new BrandSQL();
            List<String> brands = brandDAO.getAllBrandNames();
            for (String brand : brands) {
                CheckBox checkBox = new CheckBox(brand);
                brandFilter.getChildren().add(checkBox);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadCategories() {
        try {
            CategorySQL categoryDAO = new CategorySQL();
            List<String> categories = categoryDAO.getAllCategoryNames();
            for (String category : categories) {
                CheckBox checkBox = new CheckBox(category);
                categoryFilter.getChildren().add(checkBox);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private ScrollPane createScrollablePane(VBox content, double height) {
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(height);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        return scrollPane;
    }

    public String getSearchQuery() {
        return searchField.getText();
    }

    public List<String> getSelectedBrands() {
        return brandFilter.getChildren().filtered(node -> node instanceof CheckBox && ((CheckBox) node).isSelected())
                          .stream()
                          .map(node -> ((CheckBox) node).getText())
                          .toList();
    }

    public List<String> getSelectedCategories() {
        return categoryFilter.getChildren().filtered(node -> node instanceof CheckBox && ((CheckBox) node).isSelected())
                             .stream()
                             .map(node -> ((CheckBox) node).getText())
                             .toList();
    }

    public double getMinPrice() {
        return priceRangeSlider.getLowValue(); 
    }

    public double getMaxPrice() {
        return priceRangeSlider.getHighValue();
    }

    public Button getApplyFiltersButton() {
        return applyFiltersButton;
    }

    public Button getSearchButton() {
        return searchButton;
    }
    
    public double getMinVolume() {
        return volumeRangeSlider.getLowValue();
    }

    public double getMaxVolume() {
        return volumeRangeSlider.getHighValue();
    }
}
