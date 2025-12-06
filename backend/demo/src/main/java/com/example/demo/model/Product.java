package com.example.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Column;

@Entity
@Table(name = "products")
public class Product extends BaseEntity {

    @Column(name = "shopify_product_id", unique = true)
    private Long shopifyProductId;

    private String title;
    private String vendor;
    private String productType;
    
    // Store price as Double or BigDecimal
    private Double price;

    public Product() {}

    // Getters and Setters
    public Long getShopifyProductId() { return shopifyProductId; }
    public void setShopifyProductId(Long shopifyProductId) { this.shopifyProductId = shopifyProductId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getVendor() { return vendor; }
    public void setVendor(String vendor) { this.vendor = vendor; }

    public String getProductType() { return productType; }
    public void setProductType(String productType) { this.productType = productType; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
}