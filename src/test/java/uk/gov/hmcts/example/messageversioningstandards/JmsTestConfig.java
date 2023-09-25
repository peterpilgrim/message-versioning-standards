package uk.gov.hmcts.example.messageversioningstandards;

import jakarta.annotation.PostConstruct;
import jakarta.jms.ConnectionFactory;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;

import static org.mockito.Mockito.*;

@Configuration
@Profile("test")
public class JmsTestConfig {

    @PostConstruct
    public void init() {
        System.out.printf("%s.init() this=%X\n", this.getClass().getName(), System.identityHashCode(this) );
    }

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
        var jmsTemplate = mock(JmsTemplate.class);
        return jmsTemplate;
    }

}
