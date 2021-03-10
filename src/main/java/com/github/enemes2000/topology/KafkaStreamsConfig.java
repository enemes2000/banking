package com.github.enemes2000.topology;

import com.github.enemes2000.model.*;
import com.github.enemes2000.serdes.JsonSerdes;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.KeyValueStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.Properties;

@Configuration
public class KafkaStreamsConfig {

    @Autowired
    private Environment env;

    private KafkaStreams streams;

    static String STORE_NAME = "counttx3";

    public KafkaStreams getStreams() {
        return streams;
    }

    private void setStreams(KafkaStreams streams) {
        this.streams = streams;
    }

    @Bean
    public static Topology build(){
        //builder used to construct the topology
        StreamsBuilder builder = new StreamsBuilder();

        //define the transaction events stream
        KStream<String, Transaction> transactionKafkaStream =
                builder.stream("transaction", Consumed.with(Serdes.String(), JsonSerdes.Transaction()))
                        //Since this stream does not have a key select a new key : here
                        // we selected the customerId as the new key for this stream
                .selectKey((k,v) ->{
                    if (v != null)
                        System.out.println("Value of customerid " + v.getCustomerId());
                    return v.getCustomerId();
                });

        //Define the account events in a GlobalKtable
        GlobalKTable<String, Account> accountGlobalKTable =
                builder.globalTable("account", Consumed.with(Serdes.String(), JsonSerdes.Account()));

        //Define the customer events in a KTable
        KTable<String, Customer> customerKTable =
                builder.table("customer", Consumed.with(Serdes.String(), JsonSerdes.Customer()));

        //Define the joined parameters Serdes for Transaction -> Customer
        Joined<String, Transaction, Customer> customerTxParams = Joined.with(
          Serdes.String(),
          JsonSerdes.Transaction(),
          JsonSerdes.Customer()
        );

        //Define the valueJoiner to combine(Transaction, Customer) -> TransactionCustomer
        ValueJoiner<Transaction, Customer, TransactionCustomer> transactionValueJoiner = TransactionCustomer::new;

        //Create a new stream where TransactionCustomer is joined with the Customer KTable
        KStream<String, TransactionCustomer> transactionCustomerKStream = transactionKafkaStream.join(
                customerKTable,
                transactionValueJoiner,
                customerTxParams
        );

        //This is just a test to print the values from the previous stream
        transactionCustomerKStream.print(Printed.<String, TransactionCustomer>toSysOut().withLabel("This is my transactionCustomerKStream"));


        //Map transactionCustomer and account to create a KeyValueMapper
        //that will be used to join the TransactionCustomer with the Account global table
        KeyValueMapper<String, TransactionCustomer, String> txKeyValueMapperWith =
                (leftKey, transactionCustomer) -> transactionCustomer.getTransaction().getAccountId();

        ValueJoiner<TransactionCustomer, Account, EnrichedTransaction> enrichedTransactionValueJoiner = EnrichedTransaction::new;

        //Create an inner join between the TransactionCustomer and the Account global table
        KStream<String, EnrichedTransaction> enrichedTransactionKStream =
                transactionCustomerKStream.join(
                        accountGlobalKTable,
                        txKeyValueMapperWith,
                        enrichedTransactionValueJoiner
                );

        //Prepare the aggregation by building an initializer
        Initializer<CustomerCountTransaction> init = () -> new CustomerCountTransaction("", 1, "");

        //Define an aggregator for CustomerTransaction from EnrichedTransaction
        Aggregator<String, EnrichedTransaction, CustomerCountTransaction> aggregator =
                (key,value, aggregate) -> {
                    CustomerCountTransaction c = new CustomerCountTransaction(
                            value.getCustomerName(), 1, value.getCustomerId());
                    return aggregate.add(c);
                };


        //Group by customerId
        KGroupedStream<String, EnrichedTransaction> grouped =
                enrichedTransactionKStream.groupBy((k,v) -> v.getCustomerId(),
                        Grouped.with(Serdes.String(), JsonSerdes.EnrichedTransaction()));

        //Perform the aggreagation
        // Materialized the underlying state store for interactive queries
        //Data from the underlying state store  will be exposed to a service TransactionService
        // that will also be exposed to a controller
        KTable<String, CustomerCountTransaction> custTxTable =
                grouped.aggregate(init, aggregator,
                        Materialized.<String, CustomerCountTransaction, KeyValueStore<Bytes, byte[]>>
                        as(STORE_NAME)
                        .withKeySerde(Serdes.String())
                        .withValueSerde(JsonSerdes.CustomerCountTransaction()));


        //Push the data to a new topic : transactiontotalcustomer
        custTxTable.toStream().to("transactiontotalcustomer", Produced.with(Serdes.String(), JsonSerdes.CustomerCountTransaction()));

        return builder.build();

    }

    @Bean
    public KafkaStreams KafkaStreams(){

        String host = env.getProperty("server.host");
        int port = Integer.parseInt(env.getProperty("server.port"));
        String stateDir = env.getProperty("spring.stateDir");

        String endpoint = String.format("%s:%s", host, port);

        Properties properties = getConfig(endpoint, stateDir);

        final  KafkaStreams kafkaStreams = new KafkaStreams(build(), properties);

        Runtime.getRuntime().addShutdownHook(new Thread(kafkaStreams::close));
        kafkaStreams.start();
        setStreams(kafkaStreams);

        return kafkaStreams;
    }

    private Properties getConfig(String endpoint, String stateDir){
        Properties props = new Properties();
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, env.getProperty("stream.config.bootstrapservers"));
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, env.getProperty("stream.config.applicationid"));
        props.put(StreamsConfig.CLIENT_ID_CONFIG, env.getProperty("stream.config.clientid"));
        props.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0);
        props.put(StreamsConfig.APPLICATION_SERVER_CONFIG, endpoint);
        props.put(StreamsConfig.STATE_DIR_CONFIG, stateDir);


        return props;
    }
}
