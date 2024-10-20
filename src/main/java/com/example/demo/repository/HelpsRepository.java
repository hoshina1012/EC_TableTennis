package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Helps;

@Repository
public interface HelpsRepository extends JpaRepository<Helps, Long> {
    long countByUserId(Long userId);
    List<Helps> findAllByOrderByIdAsc();
}
