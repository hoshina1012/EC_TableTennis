package com.example.demo.entity;
import java.sql.Timestamp;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
@Entity(name = "users")
@Data //Lombokアノテーションでgetter、setterなどを自動生成
@Table(name = "users") //データベース内のテーブル名を指定
public class Users {
    @Id //主キーを表すフィールド
    //主キーの値が自動生成され、データベースの自動増分（Identity）として扱われる
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //データベーステーブルの列と対応して列名を指定
    @Column(name = "mail_address")
    @Email(message = "有効なメールアドレスを入力してください")
    @NotEmpty(message = "メールアドレスは必須です")
    private String mailAddress;
    @Column(name = "password")
    @NotEmpty(message = "パスワードは必須です")
    private String password;
    @Column(name = "user_name")
    @NotEmpty(message = "ユーザー名は必須です")
    private String userName;
    @Column(name = "user_authority")
    private int userAuthority;
    @Column(name = "user_status")
    private int userStatus;
    @CreationTimestamp //コードの作成日時と更新日時を自動的に設定
    @Column(name = "created_at")
    private Timestamp createdAt;
    @UpdateTimestamp //コードの作成日時と更新日時を自動的に設定
    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Products> products;
}