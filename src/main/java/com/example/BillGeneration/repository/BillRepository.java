package com.example.BillGeneration.repository;

import com.example.BillGeneration.entity.Bill;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BillRepository extends JpaRepository<Bill, Long> {
}
