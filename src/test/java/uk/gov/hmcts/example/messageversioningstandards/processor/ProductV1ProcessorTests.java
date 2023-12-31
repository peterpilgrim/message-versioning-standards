package uk.gov.hmcts.example.messageversioningstandards.processor;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import uk.gov.hmcts.example.messageversioningstandards.MessageSender;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@SpringBootTest
public class ProductV1ProcessorTests {

    private static final long SLEEP_TIME=100;
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
