package com.xentric.gimme.beanmodelhelper;

/**
 * Created by goutam on 20/6/16.
 */
public class ClientModel {

    //String clientId;
    String customerName;
    String date;
    String amount;
    String mode;
    String reason;
    String salesPerson;

    public ClientModel(String customerName, String date, String amount, String mode, String reason, String salesPerson) {
        //this.clientId = clientId;
        this.customerName = customerName;
        this.date = date;
        this.amount = amount;
        this.mode = mode;
        this.reason = reason;
        this.salesPerson = salesPerson;
    }

    /*public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }*/

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getSalesPerson() {
        return salesPerson;
    }

    public void setSalesPerson(String salesPerson) {
        this.salesPerson = salesPerson;
    }
}
