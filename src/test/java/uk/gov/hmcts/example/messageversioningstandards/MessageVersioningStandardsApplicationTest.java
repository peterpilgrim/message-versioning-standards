package uk.gov.hmcts.example.messageversioningstandards;

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

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;


@SpringBootTest
class MessageVersioningStandardsApplicationTest {

	@Autowired
	private MessageSender messageSender;

	@SpyBean
	private MessageListener messageListener;

	@Autowired
	private JmsTemplate jmsTemplate;

	@DisplayName("should load spring boot application context")
	@Test
	void contextLoads() {
	}

	@DisplayName("when SendingMessage to JMS queue then Correct Queue And Message Text")
	@Test
	public void whenSendingMessage_thenCorrectQueueAndMessageText() throws JMSException {
		String queueName = "queue-2";
		String messageText = "Test message";

		messageSender.sendTextMessage(queueName, messageText);

		Message sentMessage = jmsTemplate.receive(queueName);;
		assertThat( sentMessage, instanceOf( TextMessage.class));
		assertThat( messageText, is(((TextMessage)sentMessage).getText()));
	}

	@DisplayName("when Listening from JMS queue then Receiving CorrectMessage")
	@Test
	public void whenListening_thenReceivingCorrectMessage() throws JMSException {
		String queueName = "queue-1";
		String messageText = "Test message";

		messageSender.sendTextMessage(queueName,messageText);

		ArgumentCaptor messageCaptor = ArgumentCaptor.forClass(TextMessage.class);
		Mockito.verify(messageListener, Mockito.timeout(100))
				.sampleJmsListenerMethod((TextMessage) messageCaptor.capture());

		TextMessage receivedMessage = (TextMessage) messageCaptor.getValue();
		assertThat( receivedMessage.getText(), is(messageText));
	}
}
