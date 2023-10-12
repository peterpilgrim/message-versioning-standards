package uk.gov.hmcts.example.messageversioningstandards.processor;

import jakarta.jms.JMSException;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.artemis.api.core.SimpleString;
import org.apache.activemq.artemis.junit.EmbeddedActiveMQExtension;
import org.apache.activemq.command.ActiveMQQueue;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import uk.gov.hmcts.example.messageversioningstandards.MessageSender;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@SpringBootTest
public class ProductV2ProcessorTests {

    private static final long SLEEP_TIME=100;
    @Autowired
    private MessageSender messageSender;

    @Autowired
    private ProductProcessor productProcessor;

    @RegisterExtension
    private static EmbeddedActiveMQExtension server = new EmbeddedActiveMQExtension("embedded-artemis-jms.xml");

    static final SimpleString TEST_QUEUE = new SimpleString("test.queue");
    static final SimpleString TEST_ADDRESS = new SimpleString("test.queueName");

    @BeforeAll
    public static void startEmbedded() throws Exception
    {
        server.createQueue(TEST_ADDRESS, TEST_QUEUE);
    }

    @AfterAll
    public static void stopEmbedded() throws Exception{
        server.stop();
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
