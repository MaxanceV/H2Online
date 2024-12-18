package models;

import java.math.BigDecimal;
import java.sql.Timestamp;

import dao.BrandDAO;
import dao.CategoryDAO;

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
    
 // Récupérer la marque du produit
    public String getBrand() {
        try {
            BrandDAO brandDAO = new BrandDAO();
            return brandDAO.getBrandByProductId(this.productId);
        } catch (Exception e) {
            e.printStackTrace();
            return "Unknown Brand";
        }
    }

    // Récupérer la catégorie du produit
    public String getCategory() {
        try {
            CategoryDAO categoryDAO = new CategoryDAO();
            return categoryDAO.getCategoryByProductId(this.productId);
        } catch (Exception e) {
            e.printStackTrace();
            return "Unknown Category";
        }
    }
}
