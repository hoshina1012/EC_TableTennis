package com.example.demo.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.Products;
//JpaRepositoryで、データベースへの操作を簡単に行うためのメソッドを提供する。
//Products エンティティを操作するためのメソッドが自動的に生成される。
public interface ProductDetailRepository extends JpaRepository<Products, Long> {
}