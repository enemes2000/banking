package com.github.enemes2000.model;

public class TransactionCustomer {
    private Customer customer;
    private Transaction transaction;

    public TransactionCustomer(final Transaction transaction, final Customer customer) {
        this.customer = customer;
        this.transaction = transaction;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Transaction getTransaction() {
        return transaction;
    }
}
