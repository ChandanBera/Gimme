package com.xentric.gimme.beanmodelhelper;

/**
 * Created by goutam on 16/6/16.
 */
public class InvoiceModel {
    String invoiceId;
    String date;
    String billNo;
    String totalQuantity;
    String amount;

    public InvoiceModel(String invoiceId, String date, String billNo, String totalQuantity, String amount) {
        this.invoiceId = invoiceId;
        this.date = date;
        this.billNo = billNo;
        this.totalQuantity = totalQuantity;
        this.amount = amount;
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getBillNo() {
        return billNo;
    }

    public void setBillNo(String billNo) {
        this.billNo = billNo;
    }

    public String getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(String totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
