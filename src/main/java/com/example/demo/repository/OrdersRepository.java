package com.example.demo.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Orders;

@Repository
public interface OrdersRepository extends JpaRepository<Orders, Long> {
    List<Orders> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<Orders> findByProduct_User_IdOrderByIdDesc(Long userId);
    long countByUserId(Long userId);
    long countByProduct_User_Id(Long userId);
    Page<Orders> findAllByOrderByIdAsc(Pageable pageable);

    @Query(value = "SELECT SUM(quantity) FROM orders WHERE product_id = :productId", nativeQuery = true)
    Long sumQuantityByProductId(@Param("productId") Long productId);
}
