package com.daramassalik.backend.order;

import com.daramassalik.backend.shop.Product;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "product_name", nullable = false, length = 160)
    private String productName;
    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;
    @Column(nullable = false)
    private int quantity;
    @Column(name = "line_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal lineTotal;

    public Long getId() { return id; }
    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    public String getProductName() { return productName; }
    public void setProductName(String value) { productName = value; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal value) { unitPrice = value; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int value) { quantity = value; }
    public BigDecimal getLineTotal() { return lineTotal; }
    public void setLineTotal(BigDecimal value) { lineTotal = value; }
}
