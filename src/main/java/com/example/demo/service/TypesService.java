package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Types;
import com.example.demo.repository.TypesRepository;

@Service
public class TypesService {

    @Autowired
    private TypesRepository typesRepository;

    // すべてのカテゴリーを取得するメソッド
    public List<Types> findAll() {
        return typesRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
    }
    
    public Types findById(Long typeId) {
        Optional<Types> optionalType = typesRepository.findById(typeId);
        return optionalType.orElseThrow(() -> new RuntimeException("型が見つかりませんでした。ID: " + typeId));
    }
}
