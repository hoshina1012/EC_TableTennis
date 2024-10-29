package com.example.demo.entity;
import java.sql.Timestamp;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
@Entity(name = "orders")
@Data //Lombokアノテーションでgetter、setterなどを自動生成
@Table(name = "orders") //データベース内のテーブル名を指定
public class Orders {
    @Id //主キーを表すフィールド
    //主キーの値が自動生成され、データベースの自動増分（Identity）として扱われる
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //データベーステーブルの列と対応して列名を指定
    @Column(name = "user_id")
	public Long userId;
    @Column(name = "quantity")
	public int quantity;
    @Column(name = "order_status")
    private int orderStatus;
    @CreationTimestamp //コードの作成日時と更新日時を自動的に設定
    @Column(name = "created_at")
    private Timestamp createdAt;
    @UpdateTimestamp //コードの作成日時と更新日時を自動的に設定
    @Column(name = "updated_at")
    private Timestamp updatedAt;
    @Column(name = "kind_id")
	public Long kindId;

    // Many-to-Oneの関連を定義してProductsテーブルと結びつける
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    private Products product;
    
    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private Users user;

    public Users getUser() {
        return user;
    }
}