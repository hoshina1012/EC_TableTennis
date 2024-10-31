package com.example.demo.service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Colors;
import com.example.demo.entity.Orders;
import com.example.demo.entity.Sizes;
import com.example.demo.entity.Types;
import com.example.demo.repository.ColorsRepository;
import com.example.demo.repository.OrdersRepository;
import com.example.demo.repository.SizesRepository;
import com.example.demo.repository.TypesRepository;

@Service
public class OrdersService {
    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private TypesRepository typesRepository;

    @Autowired
    private ColorsRepository colorsRepository;

    @Autowired
    private SizesRepository sizesRepository;

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
	
	// 型を取得するメソッド
    public String getRacketTypeName(Long typeId) {
        return typesRepository.findById(typeId).map(Types::getName).orElse("N/A");
    }

    // 色を取得するメソッド
    public String getRubberColorName(Long colorId) {
        return colorsRepository.findById(colorId).map(Colors::getName).orElse("N/A");
    }

    // サイズを取得するメソッド
    public String getShoeSizeName(Long sizeId) {
        return sizesRepository.findById(sizeId).map(Sizes::getName).orElse("N/A");
    }
}
