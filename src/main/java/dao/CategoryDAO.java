package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import models.Category;
import tools.DBconnection;

public class CategoryDAO {

    // Ajouter une catégorie
    public void addCategory(Category category) throws SQLException {
        String query = "INSERT INTO categories (name, description) VALUES (?, ?)";
        try (Connection connection = DBconnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, category.getName());
            stmt.setString(2, category.getDescription());
            stmt.executeUpdate();
        }
    }

    // Récupérer toutes les catégories
    public List<Category> getAllCategories() throws SQLException {
        List<Category> categories = new ArrayList<>();
        String query = "SELECT * FROM categories";
        try (Connection connection = DBconnection.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                categories.add(mapCategory(rs));
            }
        }
        return categories;
    }

    // Associer une catégorie à un produit
    public void addProductCategory(int productId, int categoryId) throws SQLException {
        String query = "INSERT INTO productscategories (product_id, category_id) VALUES (?, ?)";
        try (Connection connection = DBconnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, productId);
            stmt.setInt(2, categoryId);
            stmt.executeUpdate();
        }
    }

    // Mapper une catégorie depuis le ResultSet
    private Category mapCategory(ResultSet rs) throws SQLException {
        Category category = new Category();
        category.setCategoryId(rs.getInt("category_id"));
        category.setName(rs.getString("name"));
        category.setDescription(rs.getString("description"));
        return category;
    }

    // Supprimer une catégorie
    public void deleteCategory(int categoryId) throws SQLException {
        String query = "DELETE FROM categories WHERE category_id = ?";
        try (Connection connection = DBconnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, categoryId);
            stmt.executeUpdate();
        }
    }

    // Récupérer tous les noms de catégories
    public List<String> getAllCategoryNames() throws SQLException {
        List<String> categoryNames = new ArrayList<>();
        String query = "SELECT name FROM categories";
        try (Connection connection = DBconnection.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                categoryNames.add(rs.getString("name"));
            }
        }
        return categoryNames;
    }

    // Récupérer les catégories associées à un produit
    public List<String> getCategoriesByProductId(int productId) {
        List<String> categories = new ArrayList<>();
        String query = "SELECT c.name FROM categories c " +
                       "JOIN productscategories pc ON c.category_id = pc.category_id " +
                       "WHERE pc.product_id = ?";
        try (Connection connection = DBconnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, productId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                categories.add(resultSet.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories;
    }


    // Récupérer les catégories pour plusieurs produits
    public Map<Integer, List<String>> getCategoriesForProducts(List<Integer> productIds) {
        Map<Integer, List<String>> productCategories = new HashMap<>();
        String query = "SELECT pc.product_id, c.name " +
                       "FROM productscategories pc " +
                       "JOIN categories c ON pc.category_id = c.category_id " +
                       "WHERE pc.product_id IN (" + productIds.stream().map(String::valueOf).collect(Collectors.joining(", ")) + ")";

        try (Connection connection = DBconnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                int productId = resultSet.getInt("product_id");
                String categoryName = resultSet.getString("name");

                productCategories
                    .computeIfAbsent(productId, k -> new ArrayList<>())
                    .add(categoryName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productCategories;
    }

}
