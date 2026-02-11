package com.example.BillGeneration.service;

import com.example.BillGeneration.entity.Product;
import com.example.BillGeneration.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    // add product
    public Product addProduct(Product product) {
        return productRepository.save(product);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    public void updateStock(Product product, Long quantity) {
        if (product.getQuantity() < quantity) {
            throw new RuntimeException("Insufficient stock");
        }

        Long remainingStock = product.getQuantity() - quantity;
        product.setQuantity(remainingStock);

        productRepository.save(product);

        if(remainingStock <= product.getThreshold()) {
            System.out.println("ALERT! STOCK LOW FOR PRODUCT " + product.getName());
        }
    }

    public Product updateQuantity(Long productId, Long quantity) {
        if (quantity == null || quantity < 0) {
            throw new RuntimeException("Quantity must be zero or greater");
        }
        Product product = getProduct(productId);
        Long current = product.getQuantity() == null ? 0L : product.getQuantity();
        product.setQuantity(current + quantity);
        return productRepository.save(product);
    }
}
