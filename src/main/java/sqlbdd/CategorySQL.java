/**
 * Data Access Object for managing Category entities in the database.
 * This class handles all database operations related to categories including
 * CRUD operations and relationship management with products.
 */
package sqlbdd;

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

public class CategorySQL {

    /**
     * Adds a new category to the database.
     *
     * @param category The Category object to be added
     * @throws SQLException If a database access error occurs
     */
    public void addCategory(Category category) throws SQLException {
        String query = "INSERT INTO categories (name, description) VALUES (?, ?)";
        try (Connection connection = DBconnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, category.getName());
            stmt.setString(2, category.getDescription());
            stmt.executeUpdate();
        }
    }

    /**
     * Retrieves all categories from the database.
     *
     * @return List of all Category objects
     * @throws SQLException If a database access error occurs
     */
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

    /**
     * Associates a category with a product in the database.
     *
     * @param productId The ID of the product
     * @param categoryId The ID of the category
     * @throws SQLException If a database access error occurs
     */
    public void addProductCategory(int productId, int categoryId) throws SQLException {
        String query = "INSERT INTO productscategories (product_id, category_id) VALUES (?, ?)";
        try (Connection connection = DBconnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, productId);
            stmt.setInt(2, categoryId);
            stmt.executeUpdate();
        }
    }

    /**
     * Maps a database result set row to a Category object.
     *
     * @param rs The ResultSet containing category data
     * @return A new Category object populated with the data
     * @throws SQLException If a database access error occurs
     */
    private Category mapCategory(ResultSet rs) throws SQLException {
        Category category = new Category();
        category.setCategoryId(rs.getInt("category_id"));
        category.setName(rs.getString("name"));
        category.setDescription(rs.getString("description"));
        return category;
    }

    /**
     * Deletes a category from the database.
     *
     * @param categoryId The ID of the category to delete
     * @throws SQLException If a database access error occurs
     */
    public void deleteCategory(int categoryId) throws SQLException {
        String query = "DELETE FROM categories WHERE category_id = ?";
        try (Connection connection = DBconnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, categoryId);
            stmt.executeUpdate();
        }
    }

    /**
     * Retrieves all category names from the database.
     *
     * @return List of category names
     * @throws SQLException If a database access error occurs
     */
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

    /**
     * Retrieves all categories associated with a specific product.
     *
     * @param productId The ID of the product
     * @return List of category names associated with the product
     */
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

    /**
     * Retrieves categories for multiple products in a single database query.
     * Returns a map where the key is the product ID and the value is a list of category names.
     *
     * @param productIds List of product IDs to fetch categories for
     * @return Map of product IDs to their associated category names
     */
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