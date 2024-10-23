package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.ShoesSizes;

public interface ShoesSizesRepository extends JpaRepository<ShoesSizes, Long> {
	List<ShoesSizes> findByProductId(Long productId);
}