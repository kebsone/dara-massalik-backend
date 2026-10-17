package com.daramassalik.backend.order;

import com.daramassalik.backend.auth.AppUser;
import com.daramassalik.backend.common.ResourceNotFoundException;
import com.daramassalik.backend.profile.Address;
import com.daramassalik.backend.profile.AddressRepository;
import com.daramassalik.backend.shop.Product;
import com.daramassalik.backend.shop.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Year;
import java.util.List;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final AddressRepository addressRepository;

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository, AddressRepository addressRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.addressRepository = addressRepository;
    }

    @Transactional
    public OrderResponse create(AppUser user, CreateOrderRequest request) {
        Address address = null;
        if (request.shippingAddressId() != null) {
            address = addressRepository.findByIdAndUser(request.shippingAddressId(), user)
                    .orElseThrow(() -> new ResourceNotFoundException("Address not found"));
        } else {
            address = addressRepository.findAllByUserOrderByDefaultAddressDescCreatedAtAsc(user).stream().findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("A delivery address is required."));
        }

        Order order = new Order();
        order.setUser(user);
        order.setOrderNumber("P-" + java.util.UUID.randomUUID());
        order.setStatus(OrderStatus.CREATED);
        order.setShippingAddress(address);
        order.setShippingFirstName(address.getFirstName());
        order.setShippingLastName(address.getLastName());
        order.setShippingAddressLine1(address.getAddressLine1());
        order.setShippingAddressLine2(address.getAddressLine2());
        order.setShippingPostalCode(address.getPostalCode());
        order.setShippingCity(address.getCity());
        order.setShippingCountry(address.getCountry());

        BigDecimal total = BigDecimal.ZERO;
        for (OrderItemRequest itemRequest : request.items()) {
            Product product = productRepository.findById(itemRequest.productId())
                    .filter(Product::isActive)
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + itemRequest.productId()));
            if (product.getStock() < itemRequest.quantity()) {
                throw new IllegalArgumentException("Not enough stock for product: " + product.getName());
            }
            BigDecimal lineTotal = product.getPrice().multiply(BigDecimal.valueOf(itemRequest.quantity()));
            OrderItem item = new OrderItem();
            item.setProduct(product);
            item.setProductName(product.getName());
            item.setUnitPrice(product.getPrice());
            item.setQuantity(itemRequest.quantity());
            item.setLineTotal(lineTotal);
            order.addItem(item);
            total = total.add(lineTotal);
        }
        order.setTotalAmount(total);
        orderRepository.save(order);
        order.setOrderNumber("DM-" + Year.now().getValue() + "-" + String.format("%06d", order.getId()));
        return toResponse(orderRepository.save(order));
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> findMine(AppUser user) {
        return orderRepository.findAllByUserOrderByCreatedAtDesc(user).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public OrderResponse findMineById(AppUser user, Long id) {
        return orderRepository.findByIdAndUser(id, user).map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + id));
    }

    private OrderResponse toResponse(Order order) {
        return new OrderResponse(order.getId(), order.getOrderNumber(), order.getStatus().name(), order.getTotalAmount(), order.getCurrency(),
                order.getShippingFirstName(), order.getShippingLastName(), order.getShippingAddressLine1(), order.getShippingAddressLine2(),
                order.getShippingPostalCode(), order.getShippingCity(), order.getShippingCountry(), order.getCreatedAt(),
                order.getItems().stream().map(item -> new OrderItemResponse(item.getProduct() == null ? null : item.getProduct().getId(),
                        item.getProductName(), item.getUnitPrice(), item.getQuantity(), item.getLineTotal())).toList());
    }
}
