package uk.gov.hmcts.example.messageversioningstandards;

import jakarta.annotation.PostConstruct;
import jakarta.jms.ConnectionFactory;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockReset;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class JmsTestConfiguration {

    @PostConstruct
    public void init() {
        System.out.printf("%s.init() this=%X\n", this.getClass().getName(), System.identityHashCode(this) );
    }

    @Primary
    @Bean
    public JmsListenerContainerFactory<?> makeJmsListenerContainerFactory() {
        var connectionFactory = mock( JmsListenerContainerFactory.class, MockReset.withSettings(MockReset.AFTER));
        return connectionFactory;
    }

    @Primary
    @Bean
    public ConnectionFactory makeConnectionFactory() {
       var connectionFactory = mock(ActiveMQConnectionFactory.class, MockReset.withSettings(MockReset.AFTER));
       return connectionFactory;
    }

    @Primary
    @Bean
    public JmsTemplate makeJmsTemplate() {
        var jmsTemplate = mock(JmsTemplate.class, MockReset.withSettings(MockReset.AFTER));
        return jmsTemplate;
    }


}