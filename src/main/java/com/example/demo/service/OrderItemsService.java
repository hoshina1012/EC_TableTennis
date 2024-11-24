package com.example.demo.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.OrderItems;
import com.example.demo.repository.ColorsRepository;
import com.example.demo.repository.OrderItemsRepository;
import com.example.demo.repository.SizesRepository;
import com.example.demo.repository.TypesRepository;

@Service
public class OrderItemsService {
	@Autowired
    private OrderItemsRepository orderItemsRepository;

    @Autowired
    private TypesRepository typesRepository;

    @Autowired
    private ColorsRepository colorsRepository;

    @Autowired
    private SizesRepository sizesRepository;
	
	public OrderItems saveOrderItem(OrderItems orderItem) {
        return orderItemsRepository.save(orderItem);
    }
	
	public List<OrderItems> findByUserId(Long userId) {
	    return orderItemsRepository.findByUserId(userId);
	}
	
	public List<OrderItems> fetchAllOrderItems() {
        return orderItemsRepository.findAll();  // これで全ての注文アイテムを取得できます
    }
	
	public OrderItems findById(Long id) {
	    return orderItemsRepository.findById(id)
	        .orElseThrow(() -> new IllegalArgumentException("指定されたIDの注文アイテムが見つかりません: " + id));
	}
	
	public List<OrderItems> fetchAllOrderItemsByIdDesc() {
	    return orderItemsRepository.findAllOrderByIdDesc();
	}
	
	public List<OrderItems> findByOrderId(Long orderId) {
        return orderItemsRepository.findByOrderId(orderId);
    }
	
	public Map<Long, String> getTypeNamesByOrderItems(List<OrderItems> orderItems) {
        return orderItems.stream().collect(Collectors.toMap(
        		OrderItems::getId,
                orderItem -> {
                    Long categoryId = orderItem.getProduct().getCategoryId();
                    int categoryInt = (categoryId != null) ? categoryId.intValue() : -1; // Longをintに変換し、nullの場合は-1
                    switch (categoryInt) {
                        case 1: // ラケット
                            return typesRepository.findById(orderItem.getKindId())
                                    .map(type -> type.getName())
                                    .orElse("不明な型");
                        case 2: // ラバー
                            return colorsRepository.findById(orderItem.getKindId())
                                    .map(color -> color.getName())
                                    .orElse("不明な色");
                        case 3: // シューズ
                            return sizesRepository.findById(orderItem.getKindId())
                                    .map(size -> size.getName())
                                    .orElse("不明なサイズ");
                        default:
                            return "不明な種類";
                    }
                }
        ));
    }
	
	public long countOrdersByProductOwnerId(Long userId) {
	    return orderItemsRepository.countByProduct_User_Id(userId);
	}

}
