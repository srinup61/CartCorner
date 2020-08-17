package com.intern.kartcorner.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Prabhu on 08-12-2017.
 */

public class OrdersCommonClass implements Parcelable {
    public OrdersCommonClass(){

    }


    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getProductid() {
        return productid;
    }

    public void setProductid(String productid) {
        this.productid = productid;
    }

    public String getProductname() {
        return productname;
    }

    public void setProductname(String productname) {
        this.productname = productname;
    }

    public String getProductprice() {
        return productprice;
    }

    public void setProductprice(String productprice) {
        this.productprice = productprice;
    }

    public String getProductdisprice() {
        return productdisprice;
    }

    public void setProductdisprice(String productdisprice) {
        this.productdisprice = productdisprice;
    }

    public String getProductquantity() {
        return productquantity;
    }

    public void setProductquantity(String productquantity) {
        this.productquantity = productquantity;
    }

    public String getProductimage() {
        return productimage;
    }

    public void setProductimage(String productimage) {
        this.productimage = productimage;
    }

    public String getProductweight() {
        return productweight;
    }

    public void setProductweight(String productweight) {
        this.productweight = productweight;
    }

    public String getProductquno() {
        return productquno;
    }

    public void setProductquno(String productquno) {
        this.productquno = productquno;
    }

    private String userid;
    private String productid;
    private String productname;
    private String productprice;
    private String productdisprice;

    public String getProductunitcost() {
        return productunitcost;
    }

    public void setProductunitcost(String productunitcost) {
        this.productunitcost = productunitcost;
    }

    private String productunitcost;
    private String productquantity;
    private String productimage;
    private String productweight;

    public OrdersCommonClass(String userid, String productid, String productname, String productprice, String productdisprice, String productunitcost, String productquantity, String productimage, String productweight, String productquno, String priceid) {
        this.userid = userid;
        this.productid = productid;
        this.productname = productname;
        this.productprice = productprice;
        this.productdisprice = productdisprice;
        this.productunitcost = productunitcost;
        this.productquantity = productquantity;
        this.productimage = productimage;
        this.productweight = productweight;
        this.productquno = productquno;
        this.priceid = priceid;
    }

    private String productquno;

    public String getPriceid() {
        return priceid;
    }

    public void setPriceid(String priceid) {
        this.priceid = priceid;
    }

    private String priceid;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }
}
