package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Products;
import com.example.demo.repository.ProductDetailRepository;
@Service
public class ProductDetailService {
	@Autowired										//クラスをインスタンス化
	private ProductDetailRepository productDetailRepository;

    public Products fetchProductById(Long productId) {
        return productDetailRepository.findById(productId).orElse(null);
    }
	
 // 商品情報をIDで検索し、受け取った編集後の情報で更新する
    public void updateProduct(Products product) {
        // 商品情報をIDで検索し、存在する場合のみ更新を試みる
        productDetailRepository.findById(product.getId()).ifPresent(existingProduct -> {
            existingProduct.setName(product.getName());
            existingProduct.setDescription(product.getDescription());
            existingProduct.setPrice(product.getPrice());
            existingProduct.setStock(product.getStock());
            // 他の情報も更新できるように必要なフィールドを追加
            // 更新をデータベースに保存
            productDetailRepository.save(existingProduct);
        });
    }
    
    public void deleteProduct(Long productId) {
        productDetailRepository.deleteById(productId);
    }
}
