package com.intern.kartcorner.entities;

/**
 * Created by Prabhu on 12-12-2017.
 */

public class PlaceOrderCommonClass {
    public PlaceOrderCommonClass(){

    }
    private String orderid;
    private String deliverydate;
    private String deliverytime;
    private String totalamount;
    private String deliverycharges;
    private String discountamount;
    private String orderstatus;

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }

    public String getDeliverydate() {
        return deliverydate;
    }

    public void setDeliverydate(String deliverydate) {
        this.deliverydate = deliverydate;
    }

    public String getDeliverytime() {
        return deliverytime;
    }

    public void setDeliverytime(String deliverytime) {
        this.deliverytime = deliverytime;
    }

    public String getTotalamount() {
        return totalamount;
    }

    public void setTotalamount(String totalamount) {
        this.totalamount = totalamount;
    }

    public String getDeliverycharges() {
        return deliverycharges;
    }

    public void setDeliverycharges(String deliverycharges) {
        this.deliverycharges = deliverycharges;
    }

    public String getDiscountamount() {
        return discountamount;
    }

    public void setDiscountamount(String discountamount) {
        this.discountamount = discountamount;
    }

    public String getOrderstatus() {
        return orderstatus;
    }

    public void setOrderstatus(String orderstatus) {
        this.orderstatus = orderstatus;
    }

    public String getProducts() {
        return products;
    }

    public void setProducts(String products) {
        this.products = products;
    }

    public PlaceOrderCommonClass(String orderid, String deliverydate, String deliverytime, String totalamount, String deliverycharges, String discountamount, String orderstatus, String products) {
        this.orderid = orderid;
        this.deliverydate = deliverydate;
        this.deliverytime = deliverytime;
        this.totalamount = totalamount;
        this.deliverycharges = deliverycharges;
        this.discountamount = discountamount;
        this.orderstatus = orderstatus;
        this.products = products;
    }

    private String products;
}
