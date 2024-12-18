package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import models.Category;
import tools.DBconnection;

public class CategoryDAO {
    private Connection connection;

    public CategoryDAO() {
        this.connection = DBconnection.getConnection();
    }

    // Ajouter une catégorie
    public void addCategory(Category category) throws SQLException {
        String query = "INSERT INTO categories (name, description) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, category.getName());
            stmt.setString(2, category.getDescription());
            stmt.executeUpdate();
        }
    }

    // Récupérer toutes les catégories
    public List<Category> getAllCategories() throws SQLException {
        List<Category> categories = new ArrayList<>();
        String query = "SELECT * FROM categories";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                categories.add(mapCategory(rs));
            }
        }
        return categories;
    }

    // Associer une catégorie à un produit
    public void addProductCategory(int productId, int categoryId) throws SQLException {
        String query = "INSERT INTO productscategories (product_id, category_id) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
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
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, categoryId);
            stmt.executeUpdate();
        }
    }
    
 // Récupérer tous les noms de catégories
    public List<String> getAllCategoryNames() throws SQLException {
        List<String> categoryNames = new ArrayList<>();
        String query = "SELECT name FROM categories";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                categoryNames.add(rs.getString("name"));
            }
        }
        return categoryNames;
    }
    
    public String getCategoryByProductId(int productId) throws SQLException {
        String query = "SELECT c.name FROM categories c " +
                       "JOIN productscategories pc ON c.category_id = pc.category_id " +
                       "WHERE pc.product_id = ?";
        try (Connection connection = DBconnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, productId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("name");
                }
            }
        }
        return "No Category";
    }

}