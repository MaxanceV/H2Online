package models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class Order {
    private int orderId;
    private int userId;
    private LocalDateTime orderDate;
    private String status; // "in progress", "validated", "delivered"
    private BigDecimal totalPrice;
    private String paymentMethod;
    private List<OrderItem> orderItems;

    // Constructeurs
    public Order() {}

    public Order(int orderId, int userId, LocalDateTime orderDate, String status, BigDecimal totalPrice, String paymentMethod) {
        this.orderId = orderId;
        this.userId = userId;
        this.orderDate = orderDate;
        this.status = status;
        this.totalPrice = totalPrice;
        this.paymentMethod = paymentMethod;
    }

    // Getters et Setters
    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    // Méthodes utilitaires
    public void addOrderItem(OrderItem item) {
        this.orderItems.add(item);
        updateTotalPrice();
    }

    public void removeOrderItem(OrderItem item) {
        this.orderItems.remove(item);
        updateTotalPrice();
    }

    private void updateTotalPrice() {
        BigDecimal total = BigDecimal.ZERO;
        for (OrderItem item : orderItems) {
            total = total.add(item.getSubtotalPrice());
        }
        this.totalPrice = total;
    }
    
}
