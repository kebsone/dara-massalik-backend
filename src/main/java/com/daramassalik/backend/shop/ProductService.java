package com.daramassalik.backend.shop;

import com.daramassalik.backend.common.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class ProductService {
    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> findCatalog(String search, String category) {
        List<Product> products;
        if (category != null && !category.isBlank()) {
            products = repository.findByActiveTrueAndCategoryIgnoreCaseOrderByNameAsc(category);
        } else if (search != null && !search.isBlank()) {
            products = repository.searchCatalog(search);
        } else {
            products = repository.findByActiveTrueOrderByNameAsc();
        }
        return products.stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public ProductResponse findById(Long id) {
        Product product = repository.findById(id)
                .filter(Product::isActive)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
        return toResponse(product);
    }

    @Transactional
    public ProductResponse create(ProductRequest request) {
        Product product = new Product();
        apply(product, request);
        return toResponse(repository.save(product));
    }

    @Transactional
    public ProductResponse update(Long id, ProductRequest request) {
        Product product = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
        apply(product, request);
        return toResponse(repository.save(product));
    }

    @Transactional
    public void deactivate(Long id) {
        Product product = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
        product.setActive(false);
    }

    private void apply(Product product, ProductRequest request) {
        product.setName(request.name());
        product.setReference(request.reference());
        product.setCategory(request.category());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setStock(request.stock());
        product.setImageUrl(request.imageUrl());
    }

    private ProductResponse toResponse(Product product) {
        return new ProductResponse(product.getId(), product.getName(), product.getReference(), product.getCategory(),
                product.getDescription(), product.getPrice(), product.getStock(), product.getImageUrl(), product.isActive());
    }
}
