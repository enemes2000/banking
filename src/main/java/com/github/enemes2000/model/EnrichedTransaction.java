package com.github.enemes2000.model;

public class EnrichedTransaction implements Comparable<EnrichedTransaction>{


    private String accountId;
    private String accountName;
    private String customerId;
    private String customerName;
    private Double amount;
    private String transactionType;

    public String getAccountId() {
        return accountId;
    }

    public String getAccountName() {
        return accountName;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public Double getAmount() {
        return amount;
    }



    public String getTransactionType() {
        return transactionType;
    }




    public EnrichedTransaction(TransactionCustomer transactionCustomer, Account account) {
       this.accountId = account.getId();
       this.accountName = account.getAccountName();
       this.customerId = transactionCustomer.getCustomer().getId();
       this.customerName = transactionCustomer.getCustomer().getName();
       this.amount = account.getAmount();
       this.transactionType = transactionCustomer.getTransaction().getTransactionType();
    }

    @Override
    public int compareTo(EnrichedTransaction that) {
        if((that.accountId == this.accountId)
            && (that.customerId == this.customerId)
            && (Double.compare(that.amount,this.amount) == 0)) return 0;
        return -1;
    }
}
