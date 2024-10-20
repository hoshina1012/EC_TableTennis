package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Helps;
import com.example.demo.repository.HelpsRepository;

@Service
public class HelpsService {

    @Autowired
    private HelpsRepository helpsRepository;

    public Helps saveHelp(Helps help) {
        return helpsRepository.save(help);
    }

    // すべてのお問い合わせデータを取得するメソッド
    public List<Helps> findAllHelps() {
        return helpsRepository.findAll();
    }
    
    public long countHelpsByUserId(Long userId) {
        return helpsRepository.countByUserId(userId);
    }
    
    public Helps findById(Long helpId) {
        return helpsRepository.findById(helpId).orElse(null);
    }
    
    public List<Helps> findAllHelpsByIdAsc() {
        return helpsRepository.findAllByOrderByIdAsc();
    }
}
