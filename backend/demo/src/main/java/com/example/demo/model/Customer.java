package com.example.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Column;

@Entity
@Table(name = "customers")
public class Customer extends BaseEntity {

    // Shopify Customer ID (Distinct from our database ID)
    @Column(name = "shopify_id", unique = true)
    private Long shopifyId;

    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    
    @Column(name = "total_spent")
    private Double totalSpent; // We need this for the "Top 5 customers" requirement

    // Empty Constructor
    public Customer() {}

    // Getters and Setters
    public Long getShopifyId() { return shopifyId; }
    public void setShopifyId(Long shopifyId) { this.shopifyId = shopifyId; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public Double getTotalSpent() { return totalSpent; }
    public void setTotalSpent(Double totalSpent) { this.totalSpent = totalSpent; }
}