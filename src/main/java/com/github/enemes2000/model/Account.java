package com.github.enemes2000.model;

import com.google.gson.annotations.SerializedName;

public class Account {

    @SerializedName("account_id")
    private String id;

    @SerializedName("name")
    private String accountName;

    private Double amount;

    public String getId() {
        return id;
    }

    public String getAccountName() {
        return accountName;
    }

    public Double getAmount() {
        return amount;
    }


}
