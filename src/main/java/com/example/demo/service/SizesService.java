package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Sizes;
import com.example.demo.repository.SizesRepository;

@Service
public class SizesService {

    @Autowired
    private SizesRepository sizesRepository;

    // すべてのカテゴリーを取得するメソッド
    public List<Sizes> findAll() {
        return sizesRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
    }
    
    public Sizes findById(Long sizeId) {
        Optional<Sizes> optionalSize = sizesRepository.findById(sizeId);
        return optionalSize.orElseThrow(() -> new RuntimeException("サイズが見つかりませんでした。ID: " + sizeId));
    }
}
