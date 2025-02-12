package models;

import java.math.BigDecimal;

/**
 * Represents an invoice entity with details such as invoice ID, order ID, total amount,
 * payment status, and invoice date.
 */
public class Invoice {
    private int invoiceId;
    private int orderId;
    private BigDecimal totalAmount;
    private String paymentStatus;
    private java.sql.Timestamp invoiceDate;
    
    /**
     * Gets the invoice ID.
     *
     * @return The invoice ID.
     */
    public int getInvoiceId() {
        return invoiceId;
    }

    /**
     * Sets the invoice ID.
     *
     * @param invoiceId The invoice ID to set.
     */
    public void setInvoiceId(int invoiceId) {
        this.invoiceId = invoiceId;
    }

    /**
     * Gets the order ID associated with the invoice.
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
     * Gets the total amount of the invoice.
     *
     * @return The total amount as BigDecimal.
     */
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    /**
     * Sets the total amount of the invoice.
     *
     * @param totalAmount The total amount to set.
     */
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    /**
     * Gets the payment status of the invoice.
     *
     * @return The payment status.
     */
    public String getPaymentStatus() {
        return paymentStatus;
    }

    /**
     * Sets the payment status of the invoice.
     *
     * @param paymentStatus The payment status to set.
     */
    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    /**
     * Gets the invoice date.
     *
     * @return The invoice date as a Timestamp.
     */
    public java.sql.Timestamp getInvoiceDate() {
        return invoiceDate;
    }

    /**
     * Sets the invoice date.
     *
     * @param invoiceDate The invoice date to set.
     */
    public void setInvoiceDate(java.sql.Timestamp invoiceDate) {
        this.invoiceDate = invoiceDate;
    }    
}
