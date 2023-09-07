package uk.gov.hmcts.example.messageversioningstandards;

import jakarta.jms.ConnectionFactory;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;

import javax.annotation.PostConstruct;

@TestConfiguration
@EnableJms
public class JmsTestContextConfiguration {

//    @Value("${spring.brokerService.connectionUrl}")
    private String connectionUrl = "vm://embedded-broker:61616";

    @PostConstruct
    public void startup() {
        System.out.println("are we dancer?");
    }

    @Primary
    @Bean
    public ConnectionFactory retrieveCnnectionFactory() {
        return new ActiveMQConnectionFactory(connectionUrl);
    }

}