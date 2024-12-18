package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import models.Brand;
import tools.DBconnection;

public class BrandDAO {
    private Connection connection;

    public BrandDAO() {
        this.connection = DBconnection.getConnection();
    }

    // Ajouter une marque
    public void addBrand(Brand brand) throws SQLException {
        String query = "INSERT INTO brands (name, description) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, brand.getName());
            stmt.setString(2, brand.getDescription());
            stmt.executeUpdate();
        }
    }

    // Récupérer toutes les marques
    public List<Brand> getAllBrands() throws SQLException {
        List<Brand> brands = new ArrayList<>();
        String query = "SELECT * FROM brands";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                brands.add(mapBrand(rs));
            }
        }
        return brands;
    }

    // Associer une marque à un produit
    public void addProductBrand(int productId, int brandId) throws SQLException {
        String query = "INSERT INTO products_brands (product_id, brand_id) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
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
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, brandId);
            stmt.executeUpdate();
        }
    }
    
    // Récupérer tous les noms de marques
    public List<String> getAllBrandNames() throws SQLException {
        List<String> brandNames = new ArrayList<>();
        String query = "SELECT name FROM brands";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                brandNames.add(rs.getString("name"));
            }
        }
        return brandNames;
    }
    
    public String getBrandByProductId(int productId) throws SQLException {
        String query = "SELECT b.name FROM brands b " +
                       "JOIN products_brands pb ON b.brand_id = pb.brand_id " +
                       "WHERE pb.product_id = ?";
        try (Connection connection = DBconnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, productId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("name");
                }
            }
        }
        return "No Brand";
    }

}
