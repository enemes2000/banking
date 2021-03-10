package com.github.enemes2000.model;

import com.google.gson.annotations.SerializedName;

public class Transaction {

    @SerializedName("customer_id")
    private String customerId;

    @SerializedName("account_id")
    private String accountId;

    @SerializedName("transaction_type")
    private String transactionType;

    public String getTransactionType() {
        return transactionType;
    }




    public String getCustomerId() {
        return customerId;
    }

    public String getAccountId() {
        return accountId;
    }


}
