package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Colors;
import com.example.demo.repository.ColorsRepository;

@Service
public class ColorsService {

    @Autowired
    private ColorsRepository colorsRepository;

    // すべてのカテゴリーを取得するメソッド
    public List<Colors> findAll() {
        return colorsRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
    }
}
