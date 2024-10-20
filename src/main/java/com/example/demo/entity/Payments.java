package com.example.demo.entity;
import java.sql.Timestamp;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
@Entity(name = "payments")
@Data //Lombokアノテーションでgetter、setterなどを自動生成
@Table(name = "payments") //データベース内のテーブル名を指定
public class Payments {
    @Id //主キーを表すフィールド
    //主キーの値が自動生成され、データベースの自動増分（Identity）として扱われる
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //データベーステーブルの列と対応して列名を指定
    @Column(name = "user_id")
	public Long userId;
    @Column(name = "card_number")
	public Long cardNumber;
    @Column(name = "card_kind")
    private int cardKind;
    @Column(name = "expiration_year")
    private int expirationYear;
    @Column(name = "expiration_month")
    private int expirationMonth;
    @Column(name = "card_holder")
    private String cardHolder;
    @Column(name = "security_code")
    private int securityCode;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    @Column(name = "zip_code1")
    private int zipCode1;
    @Column(name = "zip_code2")
    private int zipCode2;
    @Column(name = "address")
    private String address;
    @CreationTimestamp //コードの作成日時と更新日時を自動的に設定
    @Column(name = "created_at")
    private Timestamp createdAt;
    @UpdateTimestamp //コードの作成日時と更新日時を自動的に設定
    @Column(name = "updated_at")
    private Timestamp updatedAt;
}