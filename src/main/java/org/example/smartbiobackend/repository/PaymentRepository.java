package org.example.smartbiobackend.repository;

import org.example.smartbiobackend.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {
}
