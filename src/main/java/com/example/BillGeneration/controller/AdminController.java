package com.example.BillGeneration.controller;

import com.example.BillGeneration.dto.UpdateQuantityRequest;
import com.example.BillGeneration.entity.Product;
import com.example.BillGeneration.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private ProductService productService;

    @PostMapping("/products")
    public Product addProduct(@RequestBody Product product) {
        return productService.addProduct(product);
    }

    @GetMapping("/products")
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @PatchMapping("/products/{id}/quantity")
    public Product updateQuantity(@PathVariable Long id, @Valid @RequestBody UpdateQuantityRequest request) {
        return productService.updateQuantity(id, request.getQuantity());
    }
}
