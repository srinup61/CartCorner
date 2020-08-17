package com.intern.kartcorner.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Prabhu on 02-12-2017.
 */

public class ProductCommonClass implements Serializable{
    public ProductCommonClass(){

    }

    public ProductCommonClass(String productid, String productname, String productprice, String productdisprice, String productquantity, String productweight, String productcatid, String productsubcatid, String productinstock, String productimage, String productdesc) {
        this.productid = productid;
        this.productname = productname;
        this.productprice = productprice;
        this.productdisprice = productdisprice;
        this.productquantity = productquantity;
        this.productweight = productweight;
        this.productcatid = productcatid;
        this.productsubcatid = productsubcatid;
        this.productinstock = productinstock;
        this.productimage = productimage;
        this.productdesc = productdesc;
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

    public String getProductweight() {
        return productweight;
    }

    public void setProductweight(String productweight) {
        this.productweight = productweight;
    }

    public String getProductcatid() {
        return productcatid;
    }

    public void setProductcatid(String productcatid) {
        this.productcatid = productcatid;
    }

    public String getProductsubcatid() {
        return productsubcatid;
    }

    public void setProductsubcatid(String productsubcatid) {
        this.productsubcatid = productsubcatid;
    }

    public String getProductinstock() {
        return productinstock;
    }

    public void setProductinstock(String productinstock) {
        this.productinstock = productinstock;
    }

    public String getProductimage() {
        return productimage;
    }

    public void setProductimage(String productimage) {
        this.productimage = productimage;
    }

    public String getProductdesc() {
        return productdesc;
    }

    public void setProductdesc(String productdesc) {
        this.productdesc = productdesc;
    }

    public String getProductqu() {
        return productqu;
    }

    public void setProductqu(String productqu) {
        this.productqu = productqu;
    }

    private String productqu,productid,productname,productprice,productdisprice,productquantity,productweight,productcatid,productsubcatid,productinstock,productimage,productdesc;

    public ArrayList<Prices> getPrices() {
        return prices;
    }

    public void setPrices(ArrayList<Prices> prices) {
        this.prices = prices;
    }

    private ArrayList<Prices> prices;

    public String getPriceId() {
        return priceId;
    }

    public void setPriceId(String priceId) {
        this.priceId = priceId;
    }

    private String priceId;
}
