package com.example.demo.entity;
import java.sql.Timestamp;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
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
    @CreationTimestamp //コードの作成日時と更新日時を自動的に設定
    @Column(name = "created_at")
    private Timestamp createdAt;
    @UpdateTimestamp //コードの作成日時と更新日時を自動的に設定
    @Column(name = "updated_at")
    private Timestamp updatedAt;
    @Column(name = "amount")
    private int amount;
    
    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private Users user;

    public Users getUser() {
        return user;
    }
    
 // 表示用のフィールド（データベースに保存しないため @Transient を使用）
    @Transient
    private String kindName;

    // getKindName メソッド
    public String getKindName() {
        return kindName;
    }

    // setKindName メソッド
    public void setKindName(String kindName) {
        this.kindName = kindName;
    }
    
    @OneToMany(mappedBy = "order")
    private List<OrderItems> orderItems;
}