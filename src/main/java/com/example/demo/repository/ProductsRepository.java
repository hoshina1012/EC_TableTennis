package com.example.demo.repository;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.Products;
//JpaRepositoryで、データベースへの操作を簡単に行うためのメソッドを提供する。
//Products エンティティを操作するためのメソッドが自動的に生成される。
public interface ProductsRepository extends JpaRepository<Products, Long> {
    Page<Products> findAllByOrderByIdDesc(Pageable pageable);
    Page<Products> findAllByOrderByIdAsc(Pageable pageable);
    List<Products> findAllByUser_IdOrderByIdDesc(Long userId);
    List<Products> findAllByOrderByIdDesc();
    Page<Products> findAllByCategory_IdOrderByIdDesc(Long categoryId, Pageable pageable);
    Page<Products> findByUser_Id(Long userId, Pageable pageable);
}