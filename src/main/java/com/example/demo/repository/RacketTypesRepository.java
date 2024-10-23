package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.RacketTypes;

public interface RacketTypesRepository extends JpaRepository<RacketTypes, Long> {
	List<RacketTypes> findByProductId(Long productId);
}