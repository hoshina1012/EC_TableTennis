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
import jakarta.persistence.Transient;
import lombok.Data;
@Entity(name = "carts")
@Data //Lombokアノテーションでgetter、setterなどを自動生成
@Table(name = "carts") //データベース内のテーブル名を指定
public class Carts {
    @Id //主キーを表すフィールド
    //主キーの値が自動生成され、データベースの自動増分（Identity）として扱われる
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //データベーステーブルの列と対応して列名を指定
    @Column(name = "user_id")
	public Long userId;
    @Column(name = "product_id")
	public Long productId;
    @Column(name = "quantity")
    private int quantity;
    @CreationTimestamp //コードの作成日時と更新日時を自動的に設定
    @Column(name = "created_at")
    private Timestamp createdAt;
    @UpdateTimestamp //コードの作成日時と更新日時を自動的に設定
    @Column(name = "updated_at")
    private Timestamp updatedAt;
    @Column(name = "category_id")
	public Long categoryId;
    @Column(name = "kind_id")
	public Long kindId;

    @Transient
    private String productName;

    @Transient
    private int productPrice;
    
    @Transient
    private String kindName;
    
    public Long getKindId() {
        return kindId;
    }

    public void setKindId(Long kindId) {
        this.kindId = kindId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }
}