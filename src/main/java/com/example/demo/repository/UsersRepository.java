package com.example.demo.repository;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.Users;
//JpaRepositoryで、データベースへの操作を簡単に行うためのメソッドを提供する。
//Products エンティティを操作するためのメソッドが自動的に生成される。
public interface UsersRepository extends JpaRepository<Users, Long> {
    Users findByMailAddress(String mailAddress);
    Users findByMailAddressAndPassword(String mailAddress, String password);
    List<Users> findAllByUserStatusOrderByIdAsc(int userStatus);
    Page<Users> findAllByUserStatusOrderByIdAsc(int userStatus, Pageable pageable);
}