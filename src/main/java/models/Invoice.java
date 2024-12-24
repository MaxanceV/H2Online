package models;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import tools.DBconnection;

public class Invoice {
    private int invoiceId;
    private int orderId;
    private BigDecimal totalAmount;
    private String paymentStatus;
    private java.sql.Timestamp invoiceDate;
    
	public int getInvoiceId() {
		return invoiceId;
	}
	public void setInvoiceId(int invoiceId) {
		this.invoiceId = invoiceId;
	}
	public int getOrderId() {
		return orderId;
	}
	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}
	public BigDecimal getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}
	public String getPaymentStatus() {
		return paymentStatus;
	}
	public void setPaymentStatus(String paymentStatus) {
		this.paymentStatus = paymentStatus;
	}
	public java.sql.Timestamp getInvoiceDate() {
		return invoiceDate;
	}
	public void setInvoiceDate(java.sql.Timestamp invoiceDate) {
		this.invoiceDate = invoiceDate;
	}	
    
}
