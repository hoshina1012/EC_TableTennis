package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Products;
import com.example.demo.repository.ProductsRepository;
@Service
public class ProductsService {
	@Autowired										//クラスをインスタンス化
	private ProductsRepository productsRepository;  //ProductsRepository型の変数productsRepositoryを宣言
	
	public Page<Products> fetchProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productsRepository.findAllByOrderByIdDesc(pageable);
    }
	
	public List<Products> fetchAllProducts() {
	    return productsRepository.findAllByOrderByIdDesc();
	}
	
	public Page<Products> fetchProducts2(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productsRepository.findAllByOrderByIdAsc(pageable); // 昇順に変更
    }
	
	public List<Products> fetchProductsByUserId(Long userId) {
	    return productsRepository.findAllByUser_IdOrderByIdDesc(userId);
	}
	
	public Products saveProduct(Products product) {
        return productsRepository.save(product);
    }

    public Products findProductById(Long productId) {
        return productsRepository.findById(productId).orElse(null);
    }

    public void reduceStock(Long productId, int quantity) {
        Products product = findProductById(productId);
        if (product != null && product.getStock() >= quantity) {
            product.setStock(product.getStock() - quantity);
            saveProduct(product);
        }
    }
    
    public Page<Products> fetchRackets(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productsRepository.findAllByCategory_IdOrderByIdDesc(1L, pageable);
    }
    
    public Page<Products> fetchRubbers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productsRepository.findAllByCategory_IdOrderByIdDesc(2L, pageable);
    }
    
    public Page<Products> fetchShoes(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productsRepository.findAllByCategory_IdOrderByIdDesc(3L, pageable);
    }
    
    public Page<Products> fetchProductsByUserId(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productsRepository.findByUser_Id(userId, pageable);
    }
}
