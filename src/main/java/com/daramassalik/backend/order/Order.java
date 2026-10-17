package com.daramassalik.backend.order;

import com.daramassalik.backend.auth.AppUser;
import com.daramassalik.backend.profile.Address;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_number", nullable = false, unique = true, length = 40)
    private String orderNumber;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private OrderStatus status;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(nullable = false, length = 3)
    private String currency = "EUR";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipping_address_id")
    private Address shippingAddress;

    @Column(name = "shipping_first_name", nullable = false, length = 80)
    private String shippingFirstName;
    @Column(name = "shipping_last_name", nullable = false, length = 80)
    private String shippingLastName;
    @Column(name = "shipping_address_line1", nullable = false, length = 255)
    private String shippingAddressLine1;
    @Column(name = "shipping_address_line2", length = 255)
    private String shippingAddressLine2;
    @Column(name = "shipping_postal_code", nullable = false, length = 20)
    private String shippingPostalCode;
    @Column(name = "shipping_city", nullable = false, length = 120)
    private String shippingCity;
    @Column(name = "shipping_country", nullable = false, length = 120)
    private String shippingCountry;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    void onCreate() { Instant now = Instant.now(); createdAt = now; updatedAt = now; }
    @PreUpdate
    void onUpdate() { updatedAt = Instant.now(); }

    public Long getId() { return id; }
    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }
    public AppUser getUser() { return user; }
    public void setUser(AppUser user) { this.user = user; }
    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public String getCurrency() { return currency; }
    public Address getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(Address shippingAddress) { this.shippingAddress = shippingAddress; }
    public String getShippingFirstName() { return shippingFirstName; }
    public void setShippingFirstName(String value) { shippingFirstName = value; }
    public String getShippingLastName() { return shippingLastName; }
    public void setShippingLastName(String value) { shippingLastName = value; }
    public String getShippingAddressLine1() { return shippingAddressLine1; }
    public void setShippingAddressLine1(String value) { shippingAddressLine1 = value; }
    public String getShippingAddressLine2() { return shippingAddressLine2; }
    public void setShippingAddressLine2(String value) { shippingAddressLine2 = value; }
    public String getShippingPostalCode() { return shippingPostalCode; }
    public void setShippingPostalCode(String value) { shippingPostalCode = value; }
    public String getShippingCity() { return shippingCity; }
    public void setShippingCity(String value) { shippingCity = value; }
    public String getShippingCountry() { return shippingCountry; }
    public void setShippingCountry(String value) { shippingCountry = value; }
    public List<OrderItem> getItems() { return items; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void addItem(OrderItem item) { items.add(item); item.setOrder(this); }
}
