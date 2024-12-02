package com.xigua.demo.config;

import com.xigua.demo.kafka.consumer.KafkaConsumer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;

@Configuration
@EnableKafka
public class KafkaConfig {

    @Bean
    public KafkaConsumer listener() {
        return new KafkaConsumer();
    }

}
