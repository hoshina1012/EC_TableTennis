package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Products;
import com.example.demo.entity.RacketTypes;
import com.example.demo.entity.RubberColors;
import com.example.demo.entity.ShoesSizes;
import com.example.demo.repository.ProductDetailRepository;
import com.example.demo.repository.RacketTypesRepository;
import com.example.demo.repository.RubberColorsRepository;
import com.example.demo.repository.ShoesSizesRepository;
@Service
public class ProductDetailService {
	@Autowired										//クラスをインスタンス化
	private ProductDetailRepository productDetailRepository;
	
	@Autowired
    private RacketTypesRepository racketTypesRepository;

    @Autowired
    private RubberColorsRepository rubberColorsRepository;

    @Autowired
    private ShoesSizesRepository shoesSizesRepository;

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
    // 商品IDに基づいてラケットの型を取得
    public List<RacketTypes> findRacketTypesByProductId(Long productId) {
        return racketTypesRepository.findByProductId(productId);
    }

    // 商品IDに基づいてラバーの色を取得
    public List<RubberColors> findRubberColorsByProductId(Long productId) {
        return rubberColorsRepository.findByProductId(productId);
    }

    // 商品IDに基づいてシューズのサイズを取得
    public List<ShoesSizes> findShoesSizesByProductId(Long productId) {
        return shoesSizesRepository.findByProductId(productId);
    }
}
