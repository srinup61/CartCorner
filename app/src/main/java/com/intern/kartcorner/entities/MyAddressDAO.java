package com.intern.kartcorner.entities;

public class MyAddressDAO {
    public MyAddressDAO(){

    }

    public String getAddressid() {
        return addressid;
    }

    public void setAddressid(String addressid) {
        this.addressid = addressid;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddressnickname() {
        return addressnickname;
    }

    public void setAddressnickname(String addressnickname) {
        this.addressnickname = addressnickname;
    }

    public String getPersonname() {
        return personname;
    }

    public void setPersonname(String personname) {
        this.personname = personname;
    }

    public MyAddressDAO(String addressid, String address, String addressnickname, String personname) {
        this.addressid = addressid;
        this.address = address;
        this.addressnickname = addressnickname;
        this.personname = personname;
    }

    private String addressid;
    private String address;
    private String addressnickname;
    private String personname;

    public String getLatlong() {
        return latlong;
    }

    public void setLatlong(String latlong) {
        this.latlong = latlong;
    }

    private String latlong;

    public String getDefaulttype() {
        return defaulttype;
    }

    public void setDefaulttype(String defaulttype) {
        this.defaulttype = defaulttype;
    }

    private String defaulttype;
}
