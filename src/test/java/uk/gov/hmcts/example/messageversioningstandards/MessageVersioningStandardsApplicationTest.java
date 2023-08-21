package uk.gov.hmcts.example.messageversioningstandards;

import jakarta.jms.JMSException;
import jakarta.jms.TextMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class MessageVersioningStandardsApplicationTest {

	@Autowired
	private MessageSender messageSender;

	@SpyBean
	private MessageListener messageListener;

	@DisplayName("should load spring boot application context")
	@Test
	void contextLoads() {
	}

	@DisplayName("when SendingMessage then Correct Queue And Message Text")
	@Test
	public void whenSendingMessage_thenCorrectQueueAndMessageText() throws JMSException {
		String queueName = "queue-2";
		String messageText = "Test message";

		messageSender.sendTextMessage(queueName, messageText);

//		assertThat( embeddedBroker.getMessageCount(queueName));
//		TextMessage sentMessage = embeddedBroker.peekTextMessage(queueName);
//		assertEquals(messageText, sentMessage.getText());
	}

	@DisplayName("when Listening then Receiving CorrectMessage")
	@Test
	public void whenListening_thenReceivingCorrectMessage() throws JMSException {
		String queueName = "queue-1";
		String messageText = "Test message";

//		assertEquals(0, embeddedBroker.getDestination(queueName).getDestinationStatistics().getDispatched().getCount());
//		assertEquals(0, embeddedBroker.getDestination(queueName).getDestinationStatistics().getMessages().getCount());

//		embeddedBroker.pushMessage(queueName, messageText);
		messageSender.sendTextMessage(queueName,messageText);

		ArgumentCaptor messageCaptor = ArgumentCaptor.forClass(TextMessage.class);
		Mockito.verify(messageListener, Mockito.timeout(100))
				.sampleJmsListenerMethod((TextMessage) messageCaptor.capture());

		TextMessage receivedMessage = (TextMessage) messageCaptor.getValue();
		assertEquals(messageText, receivedMessage.getText());

//		assertEquals(1, embeddedBroker.getDestination(queueName).getDestinationStatistics().getDispatched().getCount());
//		assertEquals(0, embeddedBroker.getDestination(queueName).getDestinationStatistics().getMessages().getCount());
	}
}
