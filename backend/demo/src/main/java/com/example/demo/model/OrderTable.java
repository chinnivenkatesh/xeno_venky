package com.example.demo.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "orders")
public class OrderTable extends BaseEntity { // <--- MAKE SURE THIS SAYS 'extends BaseEntity'

    @Column(name = "shopify_order_id", unique = true)
    private Long shopifyOrderId;

    @Column(name = "total_price")
    private Double totalPrice;

    private String currency;
    
    @Column(name = "financial_status")
    private String financialStatus; 

    @ManyToOne
    @JoinColumn(name = "customer_id") 
    private Customer customer;
    
    private LocalDateTime processedAt;

    public OrderTable() {}

    // Getters and Setters... (You can keep the ones you already pasted)
    public Long getShopifyOrderId() { return shopifyOrderId; }
    public void setShopifyOrderId(Long shopifyOrderId) { this.shopifyOrderId = shopifyOrderId; }

    public Double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(Double totalPrice) { this.totalPrice = totalPrice; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getFinancialStatus() { return financialStatus; }
    public void setFinancialStatus(String financialStatus) { this.financialStatus = financialStatus; }

    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }

    public LocalDateTime getProcessedAt() { return processedAt; }
    public void setProcessedAt(LocalDateTime processedAt) { this.processedAt = processedAt; }
}