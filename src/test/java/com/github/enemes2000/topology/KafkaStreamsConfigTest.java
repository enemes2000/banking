package com.github.enemes2000.topology;


import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.TopologyTestDriver;
import org.apache.kafka.streams.state.KeyValueStore;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Properties;

@TestPropertySource(locations="classpath:application.properties")
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class KafkaStreamsConfigTest {

    @Autowired
    private Environment env;

    private KeyValueStore store;

    @Before
    public void setup() {

        Topology topology = KafkaStreamsConfig.build();

        String val = env.getProperty("stream.config.applicationid");
        // setup test driver
        Properties config = new Properties();
        config.put(StreamsConfig.APPLICATION_ID_CONFIG, env.getProperty("stream.config.applicationid"));
        config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG,  env.getProperty("stream.config.bootstrapservers"));
        config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, env.getProperty("stream.config.bootstrapservers"));
        config.put(StreamsConfig.APPLICATION_ID_CONFIG, env.getProperty("stream.config.applicationid"));
        config.put(StreamsConfig.CLIENT_ID_CONFIG, env.getProperty("stream.config.clientid"));
        config.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0);
        config.put(StreamsConfig.STATE_DIR_CONFIG,  env.getProperty("spring.stateDir"));

        TopologyTestDriver testDriver = new TopologyTestDriver(topology, config);

        store = testDriver.getKeyValueStore(env.getProperty("store.name"));

    }


    @Test
    public void testStore(){
        System.out.println("Store value" + store.get("1"));
    }
}
