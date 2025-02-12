package models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Represents an order entity containing order details such as order ID, user ID,
 * order date, status, total price, payment method, and associated order items.
 */
public class Order {
    private int orderId;
    private int userId;
    private LocalDateTime orderDate;
    private String status; // "in progress", "validated", "delivered"
    private BigDecimal totalPrice;
    private String paymentMethod;
    private List<OrderItem> orderItems;

    /**
     * Default constructor for Order.
     */
    public Order() {}

    /**
     * Constructs an Order with the specified details.
     *
     * @param orderId The unique identifier of the order.
     * @param userId The user ID associated with the order.
     * @param orderDate The date and time when the order was placed.
     * @param status The status of the order (e.g., "in progress", "validated", "delivered").
     * @param totalPrice The total price of the order.
     * @param paymentMethod The payment method used for the order.
     */
    public Order(int orderId, int userId, LocalDateTime orderDate, String status, BigDecimal totalPrice, String paymentMethod) {
        this.orderId = orderId;
        this.userId = userId;
        this.orderDate = orderDate;
        this.status = status;
        this.totalPrice = totalPrice;
        this.paymentMethod = paymentMethod;
    }

    /**
     * Gets the order ID.
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
     * Gets the user ID associated with the order.
     *
     * @return The user ID.
     */
    public int getUserId() {
        return userId;
    }

    /**
     * Sets the user ID.
     *
     * @param userId The user ID to set.
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }

    /**
     * Gets the order date.
     *
     * @return The order date and time.
     */
    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    /**
     * Sets the order date.
     *
     * @param orderDate The order date to set.
     */
    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    /**
     * Gets the order status.
     *
     * @return The order status.
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the order status.
     *
     * @param status The order status to set.
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Gets the total price of the order.
     *
     * @return The total price.
     */
    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    /**
     * Sets the total price of the order.
     *
     * @param totalPrice The total price to set.
     */
    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    /**
     * Gets the payment method used for the order.
     *
     * @return The payment method.
     */
    public String getPaymentMethod() {
        return paymentMethod;
    }

    /**
     * Sets the payment method for the order.
     *
     * @param paymentMethod The payment method to set.
     */
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    /**
     * Gets the list of order items associated with the order.
     *
     * @return The list of order items.
     */
    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    /**
     * Sets the list of order items.
     *
     * @param orderItems The list of order items to set.
     */
    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    /**
     * Adds an order item to the order and updates the total price.
     *
     * @param item The order item to add.
     */
    public void addOrderItem(OrderItem item) {
        this.orderItems.add(item);
        updateTotalPrice();
    }

    /**
     * Removes an order item from the order and updates the total price.
     *
     * @param item The order item to remove.
     */
    public void removeOrderItem(OrderItem item) {
        this.orderItems.remove(item);
        updateTotalPrice();
    }

    /**
     * Updates the total price of the order based on the order items.
     */
    private void updateTotalPrice() {
        BigDecimal total = BigDecimal.ZERO;
        for (OrderItem item : orderItems) {
            total = total.add(item.getSubtotalPrice());
        }
        this.totalPrice = total;
    }
}
