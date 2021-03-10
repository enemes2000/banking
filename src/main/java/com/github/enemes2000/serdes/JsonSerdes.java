package com.github.enemes2000.serdes;

import com.github.enemes2000.model.*;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;

public class JsonSerdes {

    public static Serde<Customer> Customer(){
        JsonSerializer<Customer> jsonSerializer = new JsonSerializer<>();
        JsonDeserializer<Customer> jsonDeserializer = new JsonDeserializer<>(Customer.class);
        return Serdes.serdeFrom(jsonSerializer, jsonDeserializer);
    }

    public static Serde<Account> Account(){
        JsonSerializer<Account> jsonSerializer = new JsonSerializer<>();
        JsonDeserializer<Account> jsonDeserializer = new JsonDeserializer<>(Account.class);
        return Serdes.serdeFrom(jsonSerializer, jsonDeserializer);
    }

    public static Serde<Transaction> Transaction(){
        JsonSerializer<Transaction> jsonSerializer = new JsonSerializer<>();
        JsonDeserializer<Transaction> jsonDeserializer = new JsonDeserializer<>(Transaction.class);
        return Serdes.serdeFrom(jsonSerializer, jsonDeserializer);
    }

    public static  Serde<TransactionCustomer> TrasactionCustomer(){
        JsonSerializer<TransactionCustomer> jsonSerializer = new JsonSerializer<>();
        JsonDeserializer<TransactionCustomer> jsonDeserializer = new JsonDeserializer<>(TransactionCustomer.class);
        return Serdes.serdeFrom(jsonSerializer, jsonDeserializer);
    }

    public static  Serde<EnrichedTransaction> EnrichedTransaction(){
        JsonSerializer<EnrichedTransaction> jsonSerializer = new JsonSerializer<>();
        JsonDeserializer<EnrichedTransaction> jsonDeserializer = new JsonDeserializer<>(EnrichedTransaction.class);
        return Serdes.serdeFrom(jsonSerializer, jsonDeserializer);
    }

    public static Serde<CustomerCountTransaction> CustomerCountTransaction() {
        JsonSerializer<CustomerCountTransaction> jsonSerializer = new JsonSerializer<>();
        JsonDeserializer<CustomerCountTransaction> jsonDeserializer = new JsonDeserializer<>(CustomerCountTransaction.class);
        return Serdes.serdeFrom(jsonSerializer, jsonDeserializer);
    }
}
