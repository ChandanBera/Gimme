package com.xentric.gimme.beanmodelhelper;

/**
 * Created by goutam on 16/6/16.
 */
public class BankModel {
    String Id;
    String BankName;

    public BankModel(String id, String bankName) {
        Id = id;
        BankName = bankName;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getBankName() {
        return BankName;
    }

    public void setBankName(String bankName) {
        BankName = bankName;
    }
}
