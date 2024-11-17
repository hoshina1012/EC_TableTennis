package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.OrderItems;

public interface OrderItemsRepository extends JpaRepository<OrderItems, Long> {
	
}
