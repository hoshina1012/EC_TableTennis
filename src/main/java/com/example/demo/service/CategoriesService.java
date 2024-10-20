package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.demo.entity.Categories;
import com.example.demo.repository.CategoriesRepository;

@Service
public class CategoriesService {

    @Autowired
    private CategoriesRepository categoriesRepository;

    // すべてのカテゴリーを取得するメソッド
    public List<Categories> findAll() {
        return categoriesRepository.findAll();
    }
}
