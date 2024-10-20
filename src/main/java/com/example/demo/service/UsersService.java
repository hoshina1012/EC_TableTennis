package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Users;
import com.example.demo.repository.UsersRepository;
@Service
public class UsersService {
	@Autowired
	private UsersRepository usersRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
	
	public List<Users> fetchUsers() {
		return usersRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
	}

	public Page<Users> fetchUsers2(int page, int size) {
	    Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "id"));
	    return usersRepository.findAll(pageable);
	}
	
	public Users saveUser(Users user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return usersRepository.save(user);
    }
    
    public boolean emailExists(String email) {
        return usersRepository.findByMailAddress(email) != null;
    }

    public Users findByMailAddress(String mailAddress) {
        return usersRepository.findByMailAddress(mailAddress);
    }

    public long countProductsByUser(Users user) {
        return user.getProducts().size();
    }

    public Users findById(Long userId) {
        return usersRepository.findById(userId).orElse(null);
    }
    
    public void save(Users user) {
        usersRepository.save(user); // 権限の更新を反映
    }
}
