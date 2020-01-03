package com.atguigu.gmall.bean;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @author ming
 * @create 2019-11-2019/11/21 8:17
 */
public class UmsMemberLevel implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private BigInteger id;
    private String name;
    private Integer growth_point;
    private Integer default_status;
    private BigDecimal free_freight_point;
    private Integer comment_growth_point;
    private Integer priviledge_free_freight;
    private Integer priviledge_sign_in;
    private Integer priviledge_comment;
    private Integer priviledge_promotion;
    private Integer priviledge_member_price;
    private Integer priviledge_birthday;
    private String note;

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getGrowth_point() {
        return growth_point;
    }

    public void setGrowth_point(Integer growth_point) {
        this.growth_point = growth_point;
    }

    public Integer getDefault_status() {
        return default_status;
    }

    public void setDefault_status(Integer default_status) {
        this.default_status = default_status;
    }

    public BigDecimal getFree_freight_point() {
        return free_freight_point;
    }

    public void setFree_freight_point(BigDecimal free_freight_point) {
        this.free_freight_point = free_freight_point;
    }

    public Integer getComment_growth_point() {
        return comment_growth_point;
    }

    public void setComment_growth_point(Integer comment_growth_point) {
        this.comment_growth_point = comment_growth_point;
    }

    public Integer getPriviledge_free_freight() {
        return priviledge_free_freight;
    }

    public void setPriviledge_free_freight(Integer priviledge_free_freight) {
        this.priviledge_free_freight = priviledge_free_freight;
    }

    public Integer getPriviledge_sign_in() {
        return priviledge_sign_in;
    }

    public void setPriviledge_sign_in(Integer priviledge_sign_in) {
        this.priviledge_sign_in = priviledge_sign_in;
    }

    public Integer getPriviledge_comment() {
        return priviledge_comment;
    }

    public void setPriviledge_comment(Integer priviledge_comment) {
        this.priviledge_comment = priviledge_comment;
    }

    public Integer getPriviledge_promotion() {
        return priviledge_promotion;
    }

    public void setPriviledge_promotion(Integer priviledge_promotion) {
        this.priviledge_promotion = priviledge_promotion;
    }

    public Integer getPriviledge_member_price() {
        return priviledge_member_price;
    }

    public void setPriviledge_member_price(Integer priviledge_member_price) {
        this.priviledge_member_price = priviledge_member_price;
    }

    public Integer getPriviledge_birthday() {
        return priviledge_birthday;
    }

    public void setPriviledge_birthday(Integer priviledge_birthday) {
        this.priviledge_birthday = priviledge_birthday;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
