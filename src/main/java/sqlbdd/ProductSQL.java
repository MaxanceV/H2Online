package sqlbdd;

import models.Product;
import tools.DBconnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Provides database operations for managing products.
 */
public class ProductSQL {

    /**
     * Adds a new product to the database.
     *
     * @param product The product to add.
     * @throws SQLException If a database access error occurs.
     */
    public void addProduct(Product product) throws SQLException {
        String query = "INSERT INTO products (name, volume_per_bottle, description, image, price, stock_quantity) " +
                       "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connection = DBconnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, product.getName());
            stmt.setBigDecimal(2, product.getVolumePerBottle());
            stmt.setString(3, product.getDescription());
            stmt.setString(4, product.getImage());
            stmt.setBigDecimal(5, product.getPrice());
            stmt.setInt(6, product.getStockQuantity());
            stmt.executeUpdate();
        }
    }

    /**
     * Retrieves a product from the database by its ID.
     *
     * @param productId The ID of the product to retrieve.
     * @return The product if found, otherwise null.
     * @throws SQLException If a database access error occurs.
     */
    public Product getProductById(int productId) throws SQLException {
        String query = "SELECT * FROM products WHERE product_id = ?";
        try (Connection connection = DBconnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, productId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapProduct(rs);
                }
            }
        }
        return null; // Returns null if the product does not exist
    }

    /**
     * Retrieves all products from the database.
     *
     * @return A list containing all products.
     * @throws SQLException If a database access error occurs.
     */
    public List<Product> getAllProducts() throws SQLException {
        List<Product> products = new ArrayList<>();
        String query = "SELECT * FROM products";
        try (Connection connection = DBconnection.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                products.add(mapProduct(rs));
            }
        }
        return products;
    }

    /**
     * Updates an existing product in the database.
     *
     * @param product The product with updated information.
     * @throws SQLException If a database access error occurs.
     */
    public void updateProduct(Product product) throws SQLException {
        String query = "UPDATE products SET name = ?, volume_per_bottle = ?, description = ?, image = ?, price = ?, stock_quantity = ? WHERE product_id = ?";
        try (Connection connection = DBconnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, product.getName());
            stmt.setBigDecimal(2, product.getVolumePerBottle());
            stmt.setString(3, product.getDescription());
            stmt.setString(4, product.getImage());
            stmt.setBigDecimal(5, product.getPrice());
            stmt.setInt(6, product.getStockQuantity());
            stmt.setInt(7, product.getProductId());
            stmt.executeUpdate();
        }
    }

    /**
     * Deletes a product from the database by its ID.
     *
     * @param productId The ID of the product to delete.
     * @throws SQLException If a database access error occurs.
     */
    public void deleteProduct(int productId) throws SQLException {
        String query = "DELETE FROM products WHERE product_id = ?";
        try (Connection connection = DBconnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, productId);
            stmt.executeUpdate();
        }
    }

    /**
     * Maps a {@link ResultSet} row to a {@link Product} object.
     *
     * @param rs The result set containing product information.
     * @return A populated Product object.
     * @throws SQLException If a database access error occurs.
     */
    private Product mapProduct(ResultSet rs) throws SQLException {
        Product product = new Product();
        product.setProductId(rs.getInt("product_id"));
        product.setName(rs.getString("name"));
        product.setVolumePerBottle(rs.getBigDecimal("volume_per_bottle"));
        product.setDescription(rs.getString("description"));
        product.setImage(rs.getString("image"));
        product.setPrice(rs.getBigDecimal("price"));
        product.setStockQuantity(rs.getInt("stock_quantity"));
        product.setCreatedAt(rs.getTimestamp("created_at"));
        product.setUpdatedAt(rs.getTimestamp("updated_at"));
        return product;
    }

    /**
     * Retrieves a list of products by their IDs.
     *
     * @param productIds A list of product IDs to retrieve.
     * @return A list containing matching products; returns an empty list if no IDs are provided.
     * @throws SQLException If a database access error occurs.
     */
    public List<Product> getProductsByIds(List<Integer> productIds) throws SQLException {
        List<Product> products = new ArrayList<>();
        if (productIds.isEmpty()) return products;

        String placeholders = String.join(",", productIds.stream().map(id -> "?").toArray(String[]::new));
        String query = "SELECT * FROM products WHERE product_id IN (" + placeholders + ")";
        try (Connection connection = DBconnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            for (int i = 0; i < productIds.size(); i++) {
                stmt.setInt(i + 1, productIds.get(i));
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    products.add(mapProduct(rs));
                }
            }
        }
        return products;
    }

    /**
     * Retrieves a list of recommended products based on similar brands or categories.
     *
     * @param product The product for which recommendations are sought.
     * @return A list of recommended products (up to 15), excluding the given product.
     */
    public List<Product> getRecommendations(Product product) {
        List<Product> recommendations = new ArrayList<>();
        String query = "SELECT DISTINCT p.* " +
                       "FROM products p " +
                       "LEFT JOIN productsbrands pb ON p.product_id = pb.product_id " +
                       "LEFT JOIN brands b ON pb.brand_id = b.brand_id " +
                       "LEFT JOIN productscategories pc ON p.product_id = pc.product_id " +
                       "LEFT JOIN categories c ON pc.category_id = c.category_id " +
                       "WHERE p.product_id != ? " +
                       "AND (b.name IN ( " +
                       "    SELECT b2.name " +
                       "    FROM productsbrands pb2 " +
                       "    JOIN brands b2 ON pb2.brand_id = b2.brand_id " +
                       "    WHERE pb2.product_id = ? " +
                       ") OR c.name IN ( " +
                       "    SELECT c2.name " +
                       "    FROM productscategories pc2 " +
                       "    JOIN categories c2 ON pc2.category_id = c2.category_id " +
                       "    WHERE pc2.product_id = ? " +
                       ")) " +
                       "LIMIT 15";

        try (Connection connection = DBconnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, product.getProductId()); // Exclude the current product
            stmt.setInt(2, product.getProductId()); // Filter by similar brands
            stmt.setInt(3, product.getProductId()); // Filter by similar categories

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    recommendations.add(mapProduct(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return recommendations;
    }

}
