package com.intern.kartcorner.model;

public class CategoriesDAO {
    public CategoriesDAO(){

    }
    private String categoryid;
    private String categoryname;
    private String categoryimage;

    public CategoriesDAO(String categoryid, String categoryname, String categoryimage) {
        this.categoryid = categoryid;
        this.categoryname = categoryname;
        this.categoryimage = categoryimage;
    }

    public String getCategoryId() {
        return categoryid;
    }

    public void setCategoryId(String categoryId) {
        this.categoryid = categoryId;
    }

    public String getCategoryname() {
        return categoryname;
    }

    public void setCategoryname(String categoryname) {
        this.categoryname = categoryname;
    }

    public String getCategoryimage() {
        return categoryimage;
    }

    public void setCategoryimage(String categoryimage) {
        this.categoryimage = categoryimage;
    }
}
