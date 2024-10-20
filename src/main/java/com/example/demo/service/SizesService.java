package com.example.demo.service;

import java.util.List;

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
}
