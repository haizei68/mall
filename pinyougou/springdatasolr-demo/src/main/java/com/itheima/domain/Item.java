package com.itheima.domain;

import org.apache.solr.client.solrj.beans.Field;

import java.math.BigDecimal;

public class Item {

    //SKU的ID
    @Field
    private Long id;

    //SKU的标题  表示Item的title的属性
    @Field("item_title")
    private String title;

    //价格
    @Field("item_price")
    private BigDecimal price;

    //图片
    @Field("item_image")
    private String image;

    //SPU的id
    @Field("item_goodsid")
    private Long goodsId;

    //分类名字
    @Field("item_category")
    private String category;


    //品牌名字
    @Field("item_brand")
    private String brand;

    //商家名字
    @Field("item_seller")
    private String seller;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getSeller() {
        return seller;
    }

    public void setSeller(String seller) {
        this.seller = seller;
    }

    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", price=" + price +
                ", image='" + image + '\'' +
                ", goodsId=" + goodsId +
                ", category='" + category + '\'' +
                ", brand='" + brand + '\'' +
                ", seller='" + seller + '\'' +
                '}';
    }
}
