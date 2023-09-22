package uk.gov.hmcts.example.messageversioningstandards;


import jakarta.jms.ConnectionFactory;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;

import static org.mockito.Mockito.*;

@TestConfiguration
public class JmsTestConfig {

    @Primary
    @Bean
    public JmsListenerContainerFactory<?> jmsListenerContainerFactory() {
        var connectionFactory = mock( JmsListenerContainerFactory.class);
        return connectionFactory;
    }

    @Bean
    public ConnectionFactory connectionFactory() {
       var connectionFactory = mock(ActiveMQConnectionFactory.class);
       return connectionFactory;
    }

    @Bean
    public JmsTemplate jmsTemplate() {
        var jmsTemplate = mock(jmsTemplate());
        return jmsTemplate;
    }

}
