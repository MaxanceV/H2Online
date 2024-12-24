package models;

import java.math.BigDecimal;

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
        // Vérifiez que unitPrice n'est pas null avant d'appeler updateSubtotalPrice
//        System.out.println("id : " + productId + " unitprice : " + unitPrice + " quantity : " + quantity);
        if (this.unitPrice != null) {
            updateSubtotalPrice();
        } else {
            System.out.println("Warning: unitPrice is null. Cannot update subtotal price.");
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

}
