package models;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import sqlbdd.BrandSQL;
import sqlbdd.CategorySQL;

/**
 * Represents a product entity containing details such as product ID, name,
 * volume per bottle, description, image, price, stock quantity, and timestamps.
 */
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
    
    private BrandSQL brandDAO = new BrandSQL();
    private CategorySQL categoryDAO = new CategorySQL();
    private List<String> brands = new ArrayList<>();
    private List<String> categories = new ArrayList<>();

    /**
     * Default constructor for Product.
     */
    public Product() {}

    /**
     * Constructs a Product with the specified details.
     */
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

    /**
     * Gets the product ID.
     */
    public int getProductId() {
        return productId;
    }

    /**
     * Sets the product ID.
     */
    public void setProductId(int productId) {
        this.productId = productId;
    }

    /**
     * Gets the product name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the product name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the volume per bottle.
     */
    public BigDecimal getVolumePerBottle() {
        return volumePerBottle;
    }

    /**
     * Sets the volume per bottle.
     */
    public void setVolumePerBottle(BigDecimal volumePerBottle) {
        this.volumePerBottle = volumePerBottle;
    }

    /**
     * Gets the product description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the product description.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the image URL of the product.
     */
    public String getImage() {
        return image;
    }

    /**
     * Sets the image URL of the product.
     */
    public void setImage(String image) {
        this.image = image;
    }

    /**
     * Gets the product price.
     */
    public BigDecimal getPrice() {
        return price;
    }

    /**
     * Sets the product price.
     */
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    /**
     * Gets the stock quantity of the product.
     */
    public int getStockQuantity() {
        return stockQuantity;
    }

    /**
     * Sets the stock quantity of the product.
     */
    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    /**
     * Gets the creation timestamp.
     */
    public Timestamp getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the creation timestamp.
     */
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Gets the last updated timestamp.
     */
    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Sets the last updated timestamp.
     */
    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    /**
     * Retrieves the brands associated with the product.
     */
    public List<String> getBrands() {
        if (brands == null) {
            brands = brandDAO.getBrandsByProductId(productId);
            System.out.println("Brands for product " + productId + ": " + brands);
        }
        return brands;
    }

    /**
     * Retrieves the categories associated with the product.
     */
    public List<String> getCategories() {
        if (categories == null) {
            categories = categoryDAO.getCategoriesByProductId(productId);
            System.out.println("Categories for product " + productId + ": " + categories);
        }
        return categories;
    }

    /**
     * Sets the brands associated with the product.
     */
    public void setBrands(List<String> brands) {
        this.brands = brands;
    }

    /**
     * Sets the categories associated with the product.
     */
    public void setCategories(List<String> categories) {
        this.categories = categories;
    }
    
    /**
     * Reloads the details of the product including brands and categories.
     */
    public void reloadDetails() {
        if (brands == null || brands.isEmpty()) {
            BrandSQL brandDAO = new BrandSQL();
            this.brands = brandDAO.getBrandsByProductId(productId);
        }
        if (categories == null || categories.isEmpty()) {
            CategorySQL categoryDAO = new CategorySQL();
            this.categories = categoryDAO.getCategoriesByProductId(productId);
        }
    }
}
