package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.entity.OrderItems;

public interface OrderItemsRepository extends JpaRepository<OrderItems, Long> {
	@Query("SELECT oi FROM order_items oi JOIN oi.order o WHERE o.userId = :userId")
	List<OrderItems> findByUserId(@Param("userId") Long userId);
	
	@Query("SELECT oi FROM order_items oi ORDER BY oi.id DESC")
	List<OrderItems> findAllOrderByIdDesc();

    List<OrderItems> findByOrderId(Long orderId);
    
    long countByProduct_User_Id(Long userId);
    
    @Query("SELECT COUNT(oi) FROM order_items oi WHERE oi.product.user.id = :userId")
    long countByProductOwnerId(@Param("userId") Long userId);

    @Query("SELECT SUM(oi.quantity) FROM order_items oi WHERE oi.product.id = :productId")
    Long sumQuantityByProductId(@Param("productId") Long productId);
}