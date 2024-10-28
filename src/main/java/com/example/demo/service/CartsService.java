package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Carts;
import com.example.demo.entity.Colors;
import com.example.demo.entity.Sizes;
import com.example.demo.entity.Types;
import com.example.demo.repository.CartsRepository;
import com.example.demo.repository.ColorsRepository;
import com.example.demo.repository.SizesRepository;
import com.example.demo.repository.TypesRepository;

@Service
public class CartsService {
    @Autowired
    private CartsRepository cartsRepository;
    
    @Autowired
    private TypesRepository typesRepository; // 型の名前を取得するリポジトリ
    @Autowired
    private ColorsRepository colorsRepository; // 色の名前を取得するリポジトリ
    @Autowired
    private SizesRepository sizesRepository; // サイズの名前を取得するリポジトリ

    public List<Carts> fetchCarts() {
        return cartsRepository.findAll();
    }

    public Carts saveCart(Carts cart) {
        return cartsRepository.save(cart);
    }

    public Optional<Carts> findByUserIdAndProductId(Long userId, Long productId) {
        return cartsRepository.findByUserIdAndProductId(userId, productId);
    }

    public List<Carts> findByUserId(Long userId) {
        return cartsRepository.findByUserIdOrderByIdAsc(userId);
    }

    public void deleteCartById(Long cartId) {
        cartsRepository.deleteById(cartId);
    }

    public Carts findCartById(Long cartId) {
        return cartsRepository.findById(cartId).orElse(null);
    }
    
    public Optional<Carts> findByUserIdAndProductIdAndKindId(Long userId, Long productId, Long kindId) {
        return cartsRepository.findByUserIdAndProductIdAndKindId(userId, productId, kindId);
    }
    
    public String getKindName(Long categoryId, Long kindId) {
        if (categoryId == 1) { // ラケットの場合
            return typesRepository.findById(kindId)
                    .map(Types::getName)
                    .orElse("不明な型");
        } else if (categoryId == 2) { // ラバーの場合
            return colorsRepository.findById(kindId)
                    .map(Colors::getName)
                    .orElse("不明な色");
        } else if (categoryId == 3) { // シューズの場合
            return sizesRepository.findById(kindId)
                    .map(Sizes::getName)
                    .orElse("不明なサイズ");
        }
        return "不明な種類";
    }
}
