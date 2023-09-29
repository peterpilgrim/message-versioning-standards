package uk.gov.hmcts.example.messageversioningstandards;

import jakarta.jms.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import org.springframework.jms.annotation.JmsListener;

@Slf4j
@RequiredArgsConstructor
@Component
public class ExampleMQServer {

    private JmsTemplate jmsTemplate;

    @JmsListener(destination = "queue-1")
    public void listen(Message message) throws Exception{

        var exampleRequest = message.getBody(String.class);

        jmsTemplate.convertAndSend("queue-2",
           exampleRequest
        );
    }
}