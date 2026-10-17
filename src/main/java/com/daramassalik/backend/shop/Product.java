package com.daramassalik.backend.shop;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "shop_products")
public class Product {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 160)
    private String name;

    @Column(nullable = false, unique = true, length = 80)
    private String reference;

    @Column(nullable = false, length = 80)
    private String category;

    @Column(length = 1500)
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private int stock;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(nullable = false)
    private boolean active = true;

    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
