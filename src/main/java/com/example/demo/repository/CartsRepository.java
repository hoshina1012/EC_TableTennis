package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.Carts;
//JpaRepositoryで、データベースへの操作を簡単に行うためのメソッドを提供する。
//Products エンティティを操作するためのメソッドが自動的に生成される。
public interface CartsRepository extends JpaRepository<Carts, Long> {
    Optional<Carts> findByUserIdAndProductId(Long userId, Long productId);
    List<Carts> findByUserId(Long userId);
    List<Carts> findByUserIdOrderByIdAsc(Long userId);
    Optional<Carts> findByUserIdAndProductIdAndKindId(Long userId, Long productId, Long kindId);
}