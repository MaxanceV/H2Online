package models;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import tools.DBconnection;

/**
 * Represents an order item entity containing details such as order item ID,
 * order ID, product ID, quantity, unit price, and subtotal price.
 */
public class OrderItem {
    private int orderItemId;
    private int orderId;
    private int productId;
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotalPrice;

    /**
     * Default constructor for OrderItem.
     */
    public OrderItem() {}

    /**
     * Constructs an OrderItem with the specified details.
     *
     * @param orderItemId The unique identifier of the order item.
     * @param orderId The order ID associated with the order item.
     * @param productId The product ID associated with the order item.
     * @param quantity The quantity of the product ordered.
     * @param unitPrice The unit price of the product.
     * @param subtotalPrice The subtotal price of the order item.
     */
    public OrderItem(int orderItemId, int orderId, int productId, int quantity, BigDecimal unitPrice, BigDecimal subtotalPrice) {
        this.orderItemId = orderItemId;
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.subtotalPrice = subtotalPrice;
    }

    /**
     * Gets the order item ID.
     *
     * @return The order item ID.
     */
    public int getOrderItemId() {
        return orderItemId;
    }

    /**
     * Sets the order item ID.
     *
     * @param orderItemId The order item ID to set.
     */
    public void setOrderItemId(int orderItemId) {
        this.orderItemId = orderItemId;
    }

    /**
     * Gets the order ID associated with the order item.
     *
     * @return The order ID.
     */
    public int getOrderId() {
        return orderId;
    }

    /**
     * Sets the order ID.
     *
     * @param orderId The order ID to set.
     */
    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    /**
     * Gets the product ID associated with the order item.
     *
     * @return The product ID.
     */
    public int getProductId() {
        return productId;
    }

    /**
     * Sets the product ID.
     *
     * @param productId The product ID to set.
     */
    public void setProductId(int productId) {
        this.productId = productId;
    }

    /**
     * Gets the quantity of the product ordered.
     *
     * @return The quantity of the product.
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Sets the quantity of the product ordered and updates the subtotal price.
     *
     * @param quantity The quantity to set.
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
        updateSubtotalPrice();
    }

    /**
     * Gets the unit price of the product.
     *
     * @return The unit price.
     */
    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    /**
     * Sets the unit price of the product and updates the subtotal price.
     *
     * @param unitPrice The unit price to set.
     */
    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
        updateSubtotalPrice();
    }

    /**
     * Gets the subtotal price of the order item.
     *
     * @return The subtotal price.
     */
    public BigDecimal getSubtotalPrice() {
        return subtotalPrice;
    }

    /**
     * Sets the subtotal price of the order item.
     *
     * @param subtotalPrice The subtotal price to set.
     */
    public void setSubtotalPrice(BigDecimal subtotalPrice) {
        this.subtotalPrice = subtotalPrice;
    }

    /**
     * Updates the subtotal price of the order item based on unit price and quantity.
     */
    public void updateSubtotalPrice() {
        if (this.unitPrice != null && this.quantity > 0) {
            this.subtotalPrice = this.unitPrice.multiply(new BigDecimal(this.quantity));
        } else {
            System.out.println("Warning: Cannot update subtotal price because unitPrice is null or quantity is invalid.");
        }
    }
    
    /**
     * Retrieves the product name from the database based on the product ID.
     *
     * @return The product name.
     * @throws SQLException If a database access error occurs.
     */
    public String getProductName() throws SQLException {
        String query = "SELECT name FROM products WHERE product_id = ?";
        try (Connection connection = DBconnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, this.getProductId());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("name");
                }
            }
        }
        throw new SQLException("Product name not found for product ID: " + this.getProductId());
    }
}
