package com.daramassalik.backend.shop;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/shop/products")
@Tag(name = "Shop", description = "Public shop catalog and product management")
public class ProductController {
    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "List available shop products")
    public List<ProductResponse> findCatalog(@RequestParam(required = false) String search,
                                             @RequestParam(required = false) String category) {
        return service.findCatalog(search, category);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a shop product")
    public ProductResponse findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    @SecurityRequirement(name = "bearerAuth")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a shop product")
    public ProductResponse create(@Valid @RequestBody ProductRequest request) {
        return service.create(request);
    }

    @PutMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Update a shop product")
    public ProductResponse update(@PathVariable Long id, @Valid @RequestBody ProductRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Deactivate a shop product")
    public void deactivate(@PathVariable Long id) {
        service.deactivate(id);
    }
}
