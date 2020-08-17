package com.intern.kartcorner.model;

import java.io.Serializable;

public class Prices implements Serializable {

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public String getProductDPrice() {
        return productDPrice;
    }

    public void setProductDPrice(String productDPrice) {
        this.productDPrice = productDPrice;
    }

    public String getProductWeight() {
        return productWeight;
    }

    public void setProductWeight(String productWeight) {
        this.productWeight = productWeight;
    }

    public String getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(String productQuantity) {
        this.productQuantity = productQuantity;
    }

    private String productPrice;
    private String productDPrice;
    private String productWeight;
    private String productQuantity;

    public String getPriceId() {
        return priceId;
    }

    public void setPriceId(String priceId) {
        this.priceId = priceId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    private String priceId;
    private String productId;
}
