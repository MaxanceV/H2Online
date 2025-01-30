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

public class BrandSQL {

    // Ajouter une marque
    public void addBrand(Brand brand) throws SQLException {
        String query = "INSERT INTO brands (name, description) VALUES (?, ?)";
        try (Connection connection = DBconnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, brand.getName());
            stmt.setString(2, brand.getDescription());
            stmt.executeUpdate();
        }
    }

    // Récupérer toutes les marques
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

    // Associer une marque à un produit
    public void addProductBrand(int productId, int brandId) throws SQLException {
        String query = "INSERT INTO productsbrands (product_id, brand_id) VALUES (?, ?)";
        try (Connection connection = DBconnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, productId);
            stmt.setInt(2, brandId);
            stmt.executeUpdate();
        }
    }

    // Mapper une marque depuis le ResultSet
    private Brand mapBrand(ResultSet rs) throws SQLException {
        Brand brand = new Brand();
        brand.setBrandId(rs.getInt("brand_id"));
        brand.setName(rs.getString("name"));
        brand.setDescription(rs.getString("description"));
        return brand;
    }

    // Supprimer une marque
    public void deleteBrand(int brandId) throws SQLException {
        String query = "DELETE FROM brands WHERE brand_id = ?";
        try (Connection connection = DBconnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, brandId);
            stmt.executeUpdate();
        }
    }

    // Récupérer tous les noms de marques
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

    // Récupérer les marques associées à un produit
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


    // Récupérer les marques pour plusieurs produits
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
