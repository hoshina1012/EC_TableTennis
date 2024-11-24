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
import jakarta.persistence.Transient;
import lombok.Data;
@Entity(name = "order_items")
@Data //Lombokアノテーションでgetter、setterなどを自動生成
@Table(name = "order_items") //データベース内のテーブル名を指定
public class OrderItems {
    @Id //主キーを表すフィールド
    //主キーの値が自動生成され、データベースの自動増分（Identity）として扱われる
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //データベーステーブルの列と対応して列名を指定
    @Column(name = "quantity")
	public int quantity;
    @Column(name = "kind_id")
	public Long kindId;
    @Column(name = "order_status")
    private int orderStatus;

    // Many-to-Oneの関連を定義してProductsテーブルと結びつける
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    private Products product;
    
    @ManyToOne
    @JoinColumn(name = "order_id", referencedColumnName = "id")
    private Orders order;
    
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
    
    public void setOrder(Orders order) {
        this.order = order;
    }
    
    public void setProduct(Products product) {
        this.product = product;
    }
    
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kind_id", referencedColumnName = "id", insertable = false, updatable = false)
    private RacketTypes racketType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kind_id", referencedColumnName = "id", insertable = false, updatable = false)
    private RubberColors rubberColor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kind_id", referencedColumnName = "id", insertable = false, updatable = false)
    private ShoesSizes shoeSize;

    // ゲッターとセッター
    public RacketTypes getRacketType() {
        return racketType;
    }

    public void setRacketType(RacketTypes racketType) {
        this.racketType = racketType;
    }

    public RubberColors getRubberColor() {
        return rubberColor;
    }

    public void setRubberColor(RubberColors rubberColor) {
        this.rubberColor = rubberColor;
    }

    public ShoesSizes getShoeSize() {
        return shoeSize;
    }

    public void setShoeSize(ShoesSizes shoeSize) {
        this.shoeSize = shoeSize;
    }
}