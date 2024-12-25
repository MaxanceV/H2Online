package models;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import tools.DBconnection;

public class OrderItem {
    private int orderItemId;
    private int orderId;
    private int productId;
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotalPrice;

    // Constructeurs
    public OrderItem() {}

    public OrderItem(int orderItemId, int orderId, int productId, int quantity, BigDecimal unitPrice, BigDecimal subtotalPrice) {
        this.orderItemId = orderItemId;
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.subtotalPrice = subtotalPrice;
    }

    // Getters et Setters
    public int getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(int orderItemId) {
        this.orderItemId = orderItemId;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;

        // Vérifier si unitPrice est null
        if (this.unitPrice == null) {
            try (Connection connection = DBconnection.getConnection()) {
                // Vérifier si le unitPrice existe dans OrderItem
                String queryOrderItem = "SELECT unit_price FROM orderitems WHERE order_item_id = ?";
                try (PreparedStatement stmt = connection.prepareStatement(queryOrderItem)) {
                    stmt.setInt(1, this.getOrderItemId());
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) {
                            this.unitPrice = rs.getBigDecimal("unit_price");
                        }
                    }
                }

                // Si toujours null, récupérer depuis la table products
                if (this.unitPrice == null) {
                    String queryProduct = "SELECT price FROM products WHERE product_id = ?";
                    try (PreparedStatement stmt = connection.prepareStatement(queryProduct)) {
                        stmt.setInt(1, this.getProductId());
                        try (ResultSet rs = stmt.executeQuery()) {
                            if (rs.next()) {
                                this.unitPrice = rs.getBigDecimal("price");
                            }
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Error: Unable to retrieve unit price for product ID " + this.getProductId());
            }
        }

        // Mettre à jour le prix total si unitPrice est défini
        if (this.unitPrice != null) {
            updateSubtotalPrice();
        } else {
            System.out.println("Warning: unitPrice is still null. Cannot update subtotal price.");
        }
    }


    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
        updateSubtotalPrice();
    }

    public BigDecimal getSubtotalPrice() {
        return subtotalPrice;
    }

    public void setSubtotalPrice(BigDecimal subtotalPrice) {
        this.subtotalPrice = subtotalPrice;
    }

    // Méthode pour mettre à jour le prix total de cet article
    public void updateSubtotalPrice() {
//    	System.out.println("id : " + productId + " unitprice : " + unitPrice + " quantity : " + quantity);
        if (this.unitPrice != null && this.quantity > 0) {
            this.subtotalPrice = this.unitPrice.multiply(new BigDecimal(this.quantity));
        } else {
            System.out.println("Warning: Cannot update subtotal price because unitPrice is null or quantity is invalid.");
        }
    }
    
    public String getProductName() throws SQLException {
        // On suppose que chaque OrderItem a un productId
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
