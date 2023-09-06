package uk.gov.hmcts.example.messageversioningstandards.processor;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.TextMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.jms.core.JmsTemplate;
import uk.gov.hmcts.example.messageversioningstandards.MessageSender;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
public class ProductV2ProcessorTests {

    private static final long SLEEP_TIME=100;
    @Autowired
    private MessageSender messageSender;

    @Autowired
    private ProductProcessor productProcessor;

    @BeforeEach
    public void cleanup() {
        clearQueue("tcp://localhost:61616",  "queue-1");
        clearQueue("tcp://localhost:61616",  "processor-queue-1");
    }

    public void clearQueue( String connection, String queueName)
    {
        ActiveMQConnection conn = null;

        try {
            conn = (ActiveMQConnection) new    ActiveMQConnectionFactory(connection).createConnection();
            conn.destroyDestination(new ActiveMQQueue(queueName));
        } catch (JMSException e) {
            System.err.println("**Error** connecting to the browser please check the URL" + e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (JMSException e) {
                    System.err.println("**Error** closing connection" + e);
                }
            }
        }
    }

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

        System.out.printf("productProcessor=%X\n", System.identityHashCode(productProcessor));

        productProcessor.getOrderItems().clear();
        Thread.sleep(SLEEP_TIME);
        messageSender.sendTextMessage(queueName, json);
        Thread.sleep(SLEEP_TIME);
        System.out.printf("productProcessor.orderItems=%X\n", System.identityHashCode(productProcessor.getOrderItems()));
        System.out.printf("productProcessor.orderItems.size=%d\n", productProcessor.getOrderItems().size());
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

        System.out.printf("productProcessor=%X\n", System.identityHashCode(productProcessor));
        System.out.printf("BEFORE productProcessor.orderItems=%X\n", System.identityHashCode(productProcessor.getOrderItems()));
        System.out.printf("BEFORE productProcessor.orderItems.size=%d\n", productProcessor.getOrderItems().size());

        productProcessor.getOrderItems().clear();
        Thread.sleep(SLEEP_TIME);
        messageSender.sendTextMessage(queueName, json);
        Thread.sleep(SLEEP_TIME);
        System.out.printf("AFTER productProcessor.orderItems=%X\n", System.identityHashCode(productProcessor.getOrderItems()));
        System.out.printf("AFTER productProcessor.orderItems.size=%d\n", productProcessor.getOrderItems().size());
        assertThat( productProcessor.getOrderItems().size(), is(1));
        assertThat( productProcessor.getOrderItems().get(0), is("V2.3.4" +
                " media=Book, name=The Player of Games, author=Iain M Banks"));
    }
}
