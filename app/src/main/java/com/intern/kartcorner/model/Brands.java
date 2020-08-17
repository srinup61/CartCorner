package com.intern.kartcorner.model;

public class Brands {
    public String getCategoryid() {
        return categoryid;
    }

    public void setCategoryid(String categoryid) {
        this.categoryid = categoryid;
    }

    public String getSubcategoryid() {
        return subcategoryid;
    }

    public void setSubcategoryid(String subcategoryid) {
        this.subcategoryid = subcategoryid;
    }

    public String getBrandname() {
        return brandname;
    }

    public void setBrandname(String brandname) {
        this.brandname = brandname;
    }

    public String getBrandid() {
        return brandid;
    }

    public void setBrandid(String brandid) {
        this.brandid = brandid;
    }

    public Brands(String categoryid, String subcategoryid, String brandname, String brandid) {
        this.categoryid = categoryid;
        this.subcategoryid = subcategoryid;
        this.brandname = brandname;
        this.brandid = brandid;
    }
    public Brands(){

    }

    private String categoryid,subcategoryid,brandname,brandid;
}
