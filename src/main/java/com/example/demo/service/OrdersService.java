package com.example.demo.service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Orders;
import com.example.demo.repository.OrdersRepository;

@Service
public class OrdersService {
    @Autowired
    private OrdersRepository ordersRepository;

    public void saveOrder(Orders order) {
        ordersRepository.save(order);
    }

    public List<Orders> findByUserId(Long userId) {
        return ordersRepository.findByUserIdOrderByCreatedAtDesc(userId);  // 降順で取得
    }  //ProductsRepository型の変数productsRepositoryを宣言
	
	public List<Orders> fetchOrders() {         //List<Products>型のメゾットを定義
		List<Orders> orders = ordersRepository.findAll();
        // IDで昇順にソート
        Collections.sort(orders, Comparator.comparing(Orders::getId));
        return orders;
	}
	
	public Page<Orders> fetchOrders2(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ordersRepository.findAllByOrderByIdAsc(pageable);
    }
	
	public List<Orders> findOrdersBySellerId(Long sellerId) {
	    return ordersRepository.findByProduct_User_IdOrderByIdDesc(sellerId);
	}
	
	public Orders findOrderById(Long orderId) {
	    return ordersRepository.findById(orderId).orElse(null);
	}
	
	public long countOrdersByUserId(Long userId) {
        return ordersRepository.countByUserId(userId);
    }
	
	public long countOrdersByProductOwnerId(Long ownerId) {
        return ordersRepository.countByProduct_User_Id(ownerId);
    }
	
	public long sumQuantityByProductId(Long productId) {
        Long sum = ordersRepository.sumQuantityByProductId(productId);
        return sum != null ? sum : 0;
    }
	
	public long calculateAmount(Orders order) {
        return order.getProduct().getPrice() * order.getQuantity();
    }
}
