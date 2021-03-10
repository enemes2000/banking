package com.github.enemes2000.model;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CustomerCountTransaction {


    private String name;

    private long numberOfTx;

    private String customerId;

    private  final List<CustomerCountTransaction> list = new ArrayList<>();


    public String getName() {
        return name;
    }

    public long getNumberOfTx() {
        return numberOfTx;
    }

    public String getCustomerId() {
        return customerId;
    }

    public CustomerCountTransaction add(CustomerCountTransaction c){
        list.add(c);
        return this;
    }

    public Collection<CustomerCountTransaction> getList(){
        return list.stream().collect(Collectors.toMap(CustomerCountTransaction::getCustomerId,
                Function.identity(),
                (a,b) ->
                        new CustomerCountTransaction(b.getName(),
                                a.getNumberOfTx() + b.getNumberOfTx() ,
                                b.getCustomerId()) )).values();
    }

    public CustomerCountTransaction(String name, long numberOfTx, String customerId){
        this.name = name;
        this.numberOfTx = numberOfTx;
        this.customerId = customerId;
    }

    @Override
    public String toString() {
        return String.format("CustomerCountTransaction(%s,%d,%s)",name,numberOfTx,customerId);
    }
}
