package com.example.BillGeneration.repository;

import com.example.BillGeneration.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
