package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.RubberColors;

public interface RubberColorsRepository extends JpaRepository<RubberColors, Long> {
	List<RubberColors> findByProductId(Long productId);
}