package uk.gov.hmcts.example.messageversioningstandards.processor;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.TextMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.jms.core.JmsTemplate;
import uk.gov.hmcts.example.messageversioningstandards.MessageSender;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
public class ProductProcessorTests {

    @Autowired
    private MessageSender messageSender;

    @Autowired
    private ProductProcessor productProcessor;

    @DisplayName("process product V1 message to Line Item")
    @Test
    public void processProductV1message() throws InterruptedException {

        var queueName = "processor-queue-1";
        var json = """
                {
                    "media":"Book","name":"The Player of Games","author":"Iain M Banks","genre":"Science Fiction",
                    "personas":{
                        "Jernau Morat Gurgeh":{"attributes":["Board Game Player","Chiark Orbital Citizen"]},
                        "Mawhrin-Skel":{"attributes":["Drone","Special Circumstance", "The Culture"]}
                    }
                }
                """;

        productProcessor.getOrderItems().clear();
        messageSender.sendTextMessage(queueName, json);
        Thread.sleep(500);
        assertThat( productProcessor.getOrderItems().size(), is(1));
        assertThat( productProcessor.getOrderItems().get(0), is("V1 media=Book, name=The Player of Games, author=Iain M Banks"));
    }


    @DisplayName("process product V2 message to Line Item")
    @Test
    public void processProductV2message() throws InterruptedException {

        var queueName = "processor-queue-1";
        var json = """
               {
                     "version": "2.3.4",
                     "media": "Book",
                     "name": "The Player of Games",
                     "author": "Iain M Banks",
                     "genre": "Science Fiction",
                     "personas":
                     [
                         {
                             "name": "Jernau Morat Gurgeh",
                             "role": "Board Game Player",
                             "allegiance": "UNALIGNED",
                             "note": "Chiark Orbital Citizen"
                         },
                         {
                             "name": "Mawhrin-Skel",
                             "role": "Drone",
                             "allegiance": "CULTURE",
                             "note": "Special Circumstance"
                         }
                     ]
               }
                """;
        productProcessor.getOrderItems().clear();
        messageSender.sendTextMessage(queueName, json);
        Thread.sleep(500);
        assertThat( productProcessor.getOrderItems().size(), is(1));
        assertThat( productProcessor.getOrderItems().get(0), is("V2.3.4" +
                " media=Book, name=The Player of Games, author=Iain M Banks"));
    }
}
