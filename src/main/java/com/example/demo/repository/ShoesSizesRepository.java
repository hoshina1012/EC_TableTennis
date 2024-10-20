package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.ShoesSizes;

public interface ShoesSizesRepository extends JpaRepository<ShoesSizes, Long> {
}