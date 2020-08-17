package com.intern.kartcorner.model;

public class SubCategoriesDAO {
    public SubCategoriesDAO(){
    }

    public SubCategoriesDAO(String categoryid, String subcategoryid, String subcategoryname, String subcategoryimage) {
        this.categoryid = categoryid;
        this.subcategoryid = subcategoryid;
        this.subcategoryname = subcategoryname;
        this.subcategoryimage = subcategoryimage;
    }

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

    public String getSubcategoryname() {
        return subcategoryname;
    }

    public void setSubcategoryname(String subcategoryname) {
        this.subcategoryname = subcategoryname;
    }

    public String getSubcategoryimage() {
        return subcategoryimage;
    }

    public void setSubcategoryimage(String subcategoryimage) {
        this.subcategoryimage = subcategoryimage;
    }

    private String categoryid, subcategoryid, subcategoryname, subcategoryimage;
}
