package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Payments;
import com.example.demo.repository.PaymentsRepository;

@Service
public class PaymentsService {

    @Autowired
    private PaymentsRepository paymentsRepository;

    public void savePayment(Payments payment) {
        paymentsRepository.save(payment);
    }
}
