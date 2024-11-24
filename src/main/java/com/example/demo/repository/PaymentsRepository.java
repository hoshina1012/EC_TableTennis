package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Payments;

@Repository
public interface PaymentsRepository extends JpaRepository<Payments, Long> {
	Optional<Payments> findById(Long Id);
}
