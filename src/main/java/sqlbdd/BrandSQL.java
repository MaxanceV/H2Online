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

import models.Brand;
import tools.DBconnection;

/**
 * Provides database operations for managing brands.
 */
public class BrandSQL {

    /**
     * Adds a new brand to the database.
     *
     * @param brand The brand to add.
     * @throws SQLException If a database error occurs.
     */
    public void addBrand(Brand brand) throws SQLException {
        String query = "INSERT INTO brands (name, description) VALUES (?, ?)";
        try (Connection connection = DBconnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, brand.getName());
            stmt.setString(2, brand.getDescription());
            stmt.executeUpdate();
        }
    }

    /**
     * Retrieves all brands from the database.
     *
     * @return A list of all brands.
     * @throws SQLException If a database error occurs.
     */
    public List<Brand> getAllBrands() throws SQLException {
        List<Brand> brands = new ArrayList<>();
        String query = "SELECT * FROM brands";
        try (Connection connection = DBconnection.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                brands.add(mapBrand(rs));
            }
        }
        return brands;
    }

    /**
     * Associates a brand with a product.
     *
     * @param productId The product ID.
     * @param brandId The brand ID.
     * @throws SQLException If a database error occurs.
     */
    public void addProductBrand(int productId, int brandId) throws SQLException {
        String query = "INSERT INTO productsbrands (product_id, brand_id) VALUES (?, ?)";
        try (Connection connection = DBconnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, productId);
            stmt.setInt(2, brandId);
            stmt.executeUpdate();
        }
    }

    /**
     * Maps a result set row to a Brand object.
     *
     * @param rs The result set.
     * @return A Brand object.
     * @throws SQLException If a database error occurs.
     */
    private Brand mapBrand(ResultSet rs) throws SQLException {
        Brand brand = new Brand();
        brand.setBrandId(rs.getInt("brand_id"));
        brand.setName(rs.getString("name"));
        brand.setDescription(rs.getString("description"));
        return brand;
    }

    /**
     * Deletes a brand from the database.
     *
     * @param brandId The ID of the brand to delete.
     * @throws SQLException If a database error occurs.
     */
    public void deleteBrand(int brandId) throws SQLException {
        String query = "DELETE FROM brands WHERE brand_id = ?";
        try (Connection connection = DBconnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, brandId);
            stmt.executeUpdate();
        }
    }

    /**
     * Retrieves all brand names from the database.
     *
     * @return A list of brand names.
     * @throws SQLException If a database error occurs.
     */
    public List<String> getAllBrandNames() throws SQLException {
        List<String> brandNames = new ArrayList<>();
        String query = "SELECT name FROM brands";
        try (Connection connection = DBconnection.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                brandNames.add(rs.getString("name"));
            }
        }
        return brandNames;
    }

    /**
     * Retrieves the brands associated with a given product ID.
     *
     * @param productId The product ID.
     * @return A list of brand names associated with the product.
     */
    public List<String> getBrandsByProductId(int productId) {
        List<String> brands = new ArrayList<>();
        String query = "SELECT b.name FROM brands b " +
                       "JOIN productsbrands pb ON b.brand_id = pb.brand_id " +
                       "WHERE pb.product_id = ?";
        try (Connection connection = DBconnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, productId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                brands.add(resultSet.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return brands;
    }

    /**
     * Retrieves brands associated with multiple products.
     *
     * @param productIds A list of product IDs.
     * @return A map where the key is a product ID and the value is a list of associated brand names.
     */
    public Map<Integer, List<String>> getBrandsForProducts(List<Integer> productIds) {
        Map<Integer, List<String>> productBrands = new HashMap<>();
        String query = "SELECT pb.product_id, b.name " +
                       "FROM productsbrands pb " +
                       "JOIN brands b ON pb.brand_id = b.brand_id " +
                       "WHERE pb.product_id IN (" + productIds.stream().map(String::valueOf).collect(Collectors.joining(", ")) + ")";

        try (Connection connection = DBconnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                int productId = resultSet.getInt("product_id");
                String brandName = resultSet.getString("name");

                productBrands
                    .computeIfAbsent(productId, k -> new ArrayList<>())
                    .add(brandName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productBrands;
    }
}
