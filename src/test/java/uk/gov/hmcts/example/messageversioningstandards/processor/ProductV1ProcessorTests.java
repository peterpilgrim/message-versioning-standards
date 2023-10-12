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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.example.messageversioningstandards.ArtemisConfiguration;
import uk.gov.hmcts.example.messageversioningstandards.MessageSender;

import org.apache.activemq.artemis.api.core.ActiveMQBuffer;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
// @Import(ArtemisConfiguration.class)
public class ProductV1ProcessorTests {

    private static final long SLEEP_TIME=100;
    @Autowired
    private MessageSender messageSender;

    @Autowired
    private ProductProcessor productProcessor;


    @RegisterExtension
    private static EmbeddedActiveMQExtension server = new EmbeddedActiveMQExtension("embedded-artemis-jms.xml");

    static final SimpleString TEST_QUEUE = new SimpleString("test.queue");
    static final SimpleString TEST_ADDRESS = new SimpleString("test.queueName");

    static final String TEST_BODY = "Test Message";
    static final Map<String, Object> TEST_PROPERTIES;
    static final String ASSERT_SENT_FORMAT = "Message should have been sent to %s";
    static final String ASSERT_RECEIVED_FORMAT = "Message should have been received from %s";
    static final String ASSERT_COUNT_FORMAT = "Unexpected message count in queue %s";

    static {
        TEST_PROPERTIES = new HashMap<String, Object>(2);
        TEST_PROPERTIES.put("PropertyOne", "Property Value 1");
        TEST_PROPERTIES.put("PropertyTwo", "Property Value 2");
    }

    @BeforeAll
    public static void startEmbedded() throws Exception
    {
        server.createQueue(TEST_ADDRESS, TEST_QUEUE);
    }

    @AfterAll
    public static void stopEmbedded() throws Exception{
        server.stop();
    }



    @Test
    public void testSendString() {
        final ClientMessage sent = server.sendMessage(TEST_ADDRESS, TEST_BODY);
        assertThat(String.format(ASSERT_SENT_FORMAT, TEST_ADDRESS), sent, is(notNullValue()) );

        final ClientMessage received = server.receiveMessage(TEST_QUEUE);
        assertThat(String.format(ASSERT_RECEIVED_FORMAT, TEST_ADDRESS), received, is(notNullValue()) );
        assertThat(received.getReadOnlyBodyBuffer().readString(), is(TEST_BODY ));
    }

    @Test
    public void testSendTwoStringMesssages() {
        final ClientMessage sent1 = server.sendMessage(TEST_ADDRESS, TEST_BODY);
        assertThat( String.format(ASSERT_SENT_FORMAT, TEST_ADDRESS), sent1, is(notNullValue()));
        final ClientMessage sent2 = server.sendMessage(TEST_ADDRESS, TEST_BODY + "-Second");
        assertThat( String.format(ASSERT_SENT_FORMAT, TEST_ADDRESS), sent2, is(notNullValue()));

        {
            final ClientMessage received = server.receiveMessage(TEST_QUEUE);
            assertThat(String.format(ASSERT_RECEIVED_FORMAT, TEST_ADDRESS), received, is(notNullValue()) );
            assertThat(received.getReadOnlyBodyBuffer().readString(), is(TEST_BODY));
        }
        {
            final ClientMessage received = server.receiveMessage(TEST_QUEUE);
            assertThat(String.format(ASSERT_RECEIVED_FORMAT, TEST_ADDRESS), received, is(notNullValue()) );
            assertThat(received.getReadOnlyBodyBuffer().readString(), is(TEST_BODY + "-Second"));
        }
    }

    @Test
    public void testSendBytesAndProperties() {
        final byte[] bodyBytes = TEST_BODY.getBytes();

        final ClientMessage sent = server.sendMessageWithProperties(TEST_ADDRESS, bodyBytes, TEST_PROPERTIES);
        assertThat( String.format(ASSERT_SENT_FORMAT, TEST_ADDRESS), sent, is(notNullValue()));

        final ClientMessage received = server.receiveMessage(TEST_QUEUE);
        assertThat(String.format(ASSERT_RECEIVED_FORMAT, TEST_ADDRESS), received, is(notNullValue()));

        final ActiveMQBuffer body = received.getReadOnlyBodyBuffer();
        final byte[] receivedBody = new byte[body.readableBytes()];
        body.readBytes(receivedBody);
        assertArrayEquals(TEST_BODY.getBytes(), receivedBody);

        TEST_PROPERTIES.forEach((k, v) -> {
            assertThat(received.containsProperty(k), is(true));
            assertThat(received.getStringProperty(k), is(v));
        });
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
