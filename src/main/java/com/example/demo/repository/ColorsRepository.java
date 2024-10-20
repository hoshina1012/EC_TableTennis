package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.Colors;

public interface ColorsRepository extends JpaRepository<Colors, Long> {
}
