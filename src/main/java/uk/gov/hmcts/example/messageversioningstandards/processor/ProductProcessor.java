package uk.gov.hmcts.example.messageversioningstandards.processor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.jms.JMSException;
import jakarta.jms.TextMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.example.messageversioningstandards.model.v1.Product;

import java.util.ArrayList;
import java.util.List;

@Component
public class ProductProcessor {

    private static final Logger logger = LoggerFactory.getLogger(ProductProcessor.class);

    private List<String> orderItems = new ArrayList<>();

    public List<String> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<String> orderItems) {
        this.orderItems = orderItems;
    }

    @JmsListener(destination = "processor-queue-1")
    public void sampleJmsListenerMethod(TextMessage message) throws JMSException {
        logger.info("JMS listener received text message: {}", message.getText());

        var objectMapper = new ObjectMapper();

        try {
            var productV1 = objectMapper.readValue(message.getText(), uk.gov.hmcts.example.messageversioningstandards.model.v1.Product.class);
            orderItems.add(String.format("V1 media=%s, name=%s, author=%s", productV1.getMedia(), productV1.getName(), productV1.getAuthor()));
        } catch (JsonProcessingException e) {
            uk.gov.hmcts.example.messageversioningstandards.model.v2.Product productV2= null;
            try {
                productV2 = objectMapper.readValue(message.getText(), uk.gov.hmcts.example.messageversioningstandards.model.v2.Product.class);
                orderItems.add(String.format("V%s media=%s, name=%s, author=%s", productV2.getVersion(), productV2.getMedia(), productV2.getName(), productV2.getAuthor()));
            } catch (JsonProcessingException ex) {
                throw new RuntimeException(ex);
            }
        }

    }

}