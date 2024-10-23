package com.example.demo.entity;
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
@Entity(name = "racket_types")
@Data //Lombokアノテーションでgetter、setterなどを自動生成
@Table(name = "racket_types") //データベース内のテーブル名を指定
public class RacketTypes {
    @Id //主キーを表すフィールド
    //主キーの値が自動生成され、データベースの自動増分（Identity）として扱われる
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //データベーステーブルの列と対応して列名を指定
    @Column(name = "product_id")
	public Long productId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id", referencedColumnName = "id")
    private Types type;

    // getter, setter
    public Types getType() {
        return type;
    }

    public void setType(Types type) {
        this.type = type;
    }
}