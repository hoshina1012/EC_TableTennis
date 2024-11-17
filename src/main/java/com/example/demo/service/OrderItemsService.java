package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.OrderItems;
import com.example.demo.repository.OrderItemsRepository;

@Service
public class OrderItemsService {
	@Autowired
    private OrderItemsRepository orderItemsRepository;
	
	public OrderItems saveOrderItem(OrderItems orderItem) {
        return orderItemsRepository.save(orderItem);
    }
}
