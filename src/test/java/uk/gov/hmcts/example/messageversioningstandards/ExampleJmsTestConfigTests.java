package uk.gov.hmcts.example.messageversioningstandards;


import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.TextMessage;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.ActiveProfiles;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import static org.mockito.Mockito.*;

@SpringBootTest( classes = JmsTestConfig.class)
@ActiveProfiles("test")
public class ExampleJmsTestConfigTests {

//    @Autowired
//    private MessageSender messageSender;
//
//    @Autowired
//    private MessageListener messageListener;

    @Autowired
    private JmsTemplate jmsTemplate;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @DisplayName("should load spring boot application context")
    @Test
    void contextLoads() {
    }

    @DisplayName("convert and send simple and then receive and convert")
    @Test
    public void convertAndSendSimpleThenReceiveAndConvert() {

        when(jmsTemplate.receiveAndConvert(anyString())).thenReturn("HELLO, WORLD!");

        jmsTemplate.convertAndSend("foo", "Hello, world!".toUpperCase());
        jmsTemplate.setReceiveTimeout(1_000);
        assertThat(jmsTemplate.receiveAndConvert("foo"), is("HELLO, WORLD!"));
    }

}
