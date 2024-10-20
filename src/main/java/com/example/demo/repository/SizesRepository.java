package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.Sizes;

public interface SizesRepository extends JpaRepository<Sizes, Long> {
}
