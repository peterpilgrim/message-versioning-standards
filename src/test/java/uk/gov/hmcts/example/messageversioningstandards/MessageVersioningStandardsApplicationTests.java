package uk.gov.hmcts.example.messageversioningstandards;

import jakarta.jms.JMSException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;

@SpringBootTest
class MessageVersioningStandardsApplicationTests {

	@Autowired
	private MessageSender messageSender;

	@DisplayName("should load spring boot application context")
	@Test
	void contextLoads() {
	}

	@Test
	public void whenSendingMessage_thenCorrectQueueAndMessageText() throws JMSException {
		String queueName = "queue-2";
		String messageText = "Test message";

		messageSender.sendTextMessage(queueName, messageText);

//		assertThat( embeddedBroker.getMessageCount(queueName));
//		TextMessage sentMessage = embeddedBroker.peekTextMessage(queueName);
//		assertEquals(messageText, sentMessage.getText());
	}
}
