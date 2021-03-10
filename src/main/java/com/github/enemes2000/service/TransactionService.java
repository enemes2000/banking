package com.github.enemes2000.service;


import com.github.enemes2000.model.CustomerCountTransaction;
import com.github.enemes2000.topology.KafkaStreamsConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyQueryMetadata;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.state.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Collection;
import java.util.Collections;


@Service
public class TransactionService {

    @Autowired
    private KafkaStreamsConfig config;

    @Autowired
    private Environment env;

    @Value("${store.name}")
    private String STORE_NAME;

    ReadOnlyKeyValueStore<String, CustomerCountTransaction> getStore(KafkaStreams streams) {

        return streams.store(
                StoreQueryParameters.fromNameAndType(
                        STORE_NAME,
                        QueryableStoreTypes.keyValueStore()));
    }

    public Collection<CustomerCountTransaction> getKey(String customerId) {

        KafkaStreams streams = config.getStreams();

        ReadOnlyKeyValueStore<String, CustomerCountTransaction> store = getStore(streams);

        KeyQueryMetadata metadata =
                streams.queryMetadataForKey(STORE_NAME, customerId, Serdes.String().serializer());

        HostInfo hostInfo = new HostInfo(env.getProperty("server.host"), Integer.parseInt(env.getProperty("server.port")));

        Collection<CustomerCountTransaction> listOfCustTx = null;

        if (hostInfo.equals(metadata.getActiveHost())) {
            CustomerCountTransaction value = store.get(customerId);

            listOfCustTx =  getListOfCustomerCountTransaction(value);
        }
      else {
            //If a remote instance has the key
            String remoteHost = metadata.getActiveHost().host();

            int remotePort = metadata.getActiveHost().port();

            //query from the remote host
            RestTemplate restTemplate = new RestTemplate();

            CustomerCountTransaction ctx = restTemplate.postForObject(
                    String.format("http://%s:%d/%s", remoteHost,
                            remotePort, "/transaction?customer=customerId"),"",CustomerCountTransaction.class);

           if (ctx != null)
               listOfCustTx = getListOfCustomerCountTransaction(ctx);
        }
       return listOfCustTx;
    }

    private Collection<CustomerCountTransaction> getListOfCustomerCountTransaction(CustomerCountTransaction val){
        if (val != null){
            return  val.getList();
        }else {
            return Collections.EMPTY_LIST;
        }
    }

}
