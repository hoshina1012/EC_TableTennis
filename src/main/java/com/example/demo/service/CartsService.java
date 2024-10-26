package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Carts;
import com.example.demo.repository.CartsRepository;

@Service
public class CartsService {
    @Autowired
    private CartsRepository cartsRepository;

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
}
