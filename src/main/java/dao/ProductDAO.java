package dao;

import models.Product;
import tools.DBconnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {
    private Connection connection;

    public ProductDAO() {
        this.connection = DBconnection.getConnection();
    }

    // Ajouter un produit
    public void addProduct(Product product) throws SQLException {
        String query = "INSERT INTO products (name, volume_per_bottle, description, image, price, stock_quantity) " +
                       "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, product.getName());
            stmt.setBigDecimal(2, product.getVolumePerBottle());
            stmt.setString(3, product.getDescription());
            stmt.setString(4, product.getImage());
            stmt.setBigDecimal(5, product.getPrice());
            stmt.setInt(6, product.getStockQuantity());
            stmt.executeUpdate();
        }
    }
    
    

    // Récupérer un produit par son ID
    public Product getProductById(int productId) throws SQLException {
        String query = "SELECT * FROM products WHERE product_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, productId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapProduct(rs);
                }
            }
        }
        return null; // Retourne null si le produit n'existe pas
    }

    // Récupérer tous les produits
    public List<Product> getAllProducts() throws SQLException {
        List<Product> products = new ArrayList<>();
        String query = "SELECT * FROM products";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                products.add(mapProduct(rs));
            }
        }
        return products;
    }

    // Mettre à jour un produit
    public void updateProduct(Product product) throws SQLException {
        String query = "UPDATE products SET name = ?, volume_per_bottle = ?, description = ?, image = ?, price = ?, stock_quantity = ? WHERE product_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
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

    // Supprimer un produit
    public void deleteProduct(int productId) throws SQLException {
        String query = "DELETE FROM products WHERE product_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, productId);
            stmt.executeUpdate();
        }
    }

    // Mapper les résultats d'une requête à un objet Product
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
}
