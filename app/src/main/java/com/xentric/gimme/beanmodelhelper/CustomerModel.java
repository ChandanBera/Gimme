package com.xentric.gimme.beanmodelhelper;

import java.io.Serializable;

/**
 * Created by goutam on 4/4/16.
 */
public class CustomerModel implements Serializable{

    String customerId,Name,RefCode,Address,Contact,customerLid;

    public CustomerModel(String cust_id,String name,String refCode, String Address, String Contact,String customerLid){
        customerId = cust_id;
        Name = name;
        RefCode = refCode;
        this.Address = Address;
        this.Contact = Contact;
        this.customerLid = customerLid;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getRefCode() {
        return RefCode;
    }

    public void setRefCode(String refCode) {
        RefCode = refCode;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getContact() {
        return Contact;
    }

    public void setContact(String contact) {
        Contact = contact;
    }

    public String getCustomerLid() {
        return customerLid;
    }

    public void setCustomerLid(String customerLid) {
        this.customerLid = customerLid;
    }
}
