package uk.gov.hmcts.example.messageversioningstandards.processor;

import jakarta.jms.*;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.artemis.api.core.SimpleString;
import org.apache.activemq.artemis.api.core.client.ClientMessage;
import org.apache.activemq.artemis.core.config.impl.SecurityConfiguration;
import org.apache.activemq.artemis.core.server.embedded.EmbeddedActiveMQ;
import org.apache.activemq.artemis.junit.EmbeddedActiveMQExtension;
import org.apache.activemq.artemis.spi.core.security.ActiveMQJAASSecurityManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.example.messageversioningstandards.ArtemisConfiguration;
import uk.gov.hmcts.example.messageversioningstandards.MessageSender;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
// @Import(ArtemisConfiguration.class)
public class ProductV1ProcessorTests {

    private static final long SLEEP_TIME=100;
    @Autowired
    private MessageSender messageSender;

    @Autowired
    private ProductProcessor productProcessor;

//    private static EmbeddedActiveMQ embedded = new EmbeddedActiveMQ();


    @RegisterExtension
    private static EmbeddedActiveMQExtension server = new EmbeddedActiveMQExtension();


    @BeforeAll
    public static void startEmbedded() throws Exception
    {
//        SecurityConfiguration securityConfiguration = new SecurityConfiguration();
//        securityConfiguration.addUser("admin", "password");
//        securityConfiguration.addRole("admin", "admin");
//
//        ActiveMQJAASSecurityManager securityManager = new ActiveMQJAASSecurityManager();
//        embedded.setSecurityManager(securityManager);
//        embedded.start();
        server.createQueue(TEST_ADDRESS, TEST_QUEUE);
    }

    @AfterAll
    public static void stopEmbedded() throws Exception{
//        embedded.start();
        server.stop();
    }

    static final SimpleString TEST_QUEUE = new SimpleString("test.queue");
    static final SimpleString TEST_ADDRESS = new SimpleString("test.queueName");

    static final String TEST_BODY = "Test Message";
    static final String ASSERT_SENT_FORMAT = "Message should have been sent to %s";
    static final String ASSERT_RECEIVED_FORMAT = "Message should have been received from %s";
    static final String ASSERT_COUNT_FORMAT = "Unexpected message count in queue %s";

    @Test
    public void testJMS1_1() throws Exception {
        final String QUEUE = "myQueue";
        server.start();
        // brokerURL  "vm://0"
        ConnectionFactory cf = new ActiveMQConnectionFactory("vm://0");
        Connection c = cf.createConnection();
        c.start();
        Session s = c.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Destination d = s.createQueue(QUEUE);
        MessageProducer p = s.createProducer(d);
        p.send(s.createMessage());
        MessageConsumer consumer = s.createConsumer(d);
        Message m = consumer.receive(500);
        server.stop();
        assertThat(m, is(notNullValue()));
    }

    @Test
    public void testSendString() {
        final ClientMessage sent = server.sendMessage(TEST_ADDRESS, TEST_BODY);
        assertThat(String.format(ASSERT_SENT_FORMAT, TEST_ADDRESS), sent, is(notNullValue()) );

        final ClientMessage received = server.receiveMessage(TEST_QUEUE);
        assertThat(String.format(ASSERT_RECEIVED_FORMAT, TEST_ADDRESS), received, is(notNullValue()) );
        assertThat(received.getReadOnlyBodyBuffer().readString(), is(TEST_BODY ));
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
        System.out.printf("*BEFORE* productProcessor.orderItems=%X\n", System.identityHashCode(productProcessor.getOrderItems()));
        System.out.printf("*BEFORE* productProcessor.orderItems.size=%d\n", productProcessor.getOrderItems().size());

        productProcessor.getOrderItems().clear();
        Thread.sleep(SLEEP_TIME);
        messageSender.sendTextMessage(queueName, json);
        Thread.sleep(SLEEP_TIME);
        System.out.printf("*AFTER*  productProcessor.orderItems=%X\n", System.identityHashCode(productProcessor.getOrderItems()));
        System.out.printf("*AFTER*  productProcessor.orderItems.size=%d\n", productProcessor.getOrderItems().size());
        assertThat( productProcessor.getOrderItems().size(), is(1));
        assertThat( productProcessor.getOrderItems().get(0), is("V1 media=Book, name=The Player of Games, author=Iain M Banks"));
    }

}
