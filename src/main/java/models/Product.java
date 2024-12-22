package models;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import dao.BrandDAO;
import dao.CategoryDAO;
import tools.DBconnection;

public class Product {
    private int productId;
    private String name;
    private BigDecimal volumePerBottle;
    private String description;
    private String image;
    private BigDecimal price;
    private int stockQuantity;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    
    private BrandDAO brandDAO = new BrandDAO();
    private CategoryDAO categoryDAO = new CategoryDAO();
    private List<String> brands = new ArrayList<>();
    private List<String> categories = new ArrayList<>();

    // Constructeurs
    public Product() {}

    public Product(int productId, String name, BigDecimal volumePerBottle, String description, 
                   String image, BigDecimal price, int stockQuantity, Timestamp createdAt, Timestamp updatedAt) {
        this.productId = productId;
        this.name = name;
        this.volumePerBottle = volumePerBottle;
        this.description = description;
        this.image = image;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters et setters
    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getVolumePerBottle() {
        return volumePerBottle;
    }

    public void setVolumePerBottle(BigDecimal volumePerBottle) {
        this.volumePerBottle = volumePerBottle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
    
//    public List<String> getBrands() {
//        if (brands == null) { // Charger une seule fois
//            brands = new ArrayList<>();
//            try (Connection connection = DBconnection.getConnection()) {
//                BrandDAO brandDAO = new BrandDAO();
//                brands = brandDAO.getBrandsByProductId(productId); // Retourne une liste
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        }
//        return brands;
//    }
//
//    public List<String> getCategories() {
//        if (categories == null) { // Charger une seule fois
//            categories = new ArrayList<>();
//            try (Connection connection = DBconnection.getConnection()) {
//                CategoryDAO categoryDAO = new CategoryDAO();
//                categories = categoryDAO.getCategoriesByProductId(productId); // Retourne une liste
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        }
//        return categories;
//    }
//    


    public List<String> getBrands() {
        if (brands == null) {
            brands = brandDAO.getBrandsByProductId(productId);
            System.out.println("Brands for product " + productId + ": " + brands);
        }
        return brands;
    }

    public List<String> getCategories() {
        if (categories == null) {
            categories = categoryDAO.getCategoriesByProductId(productId);
            System.out.println("Categories for product " + productId + ": " + categories);
        }
        return categories;
    }



    public void setBrands(List<String> brands) {
        this.brands = brands;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

}
