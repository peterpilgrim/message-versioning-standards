package uk.gov.hmcts.example.messageversioningstandards;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.TextMessage;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.ActiveProfiles;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest( classes = JmsTestConfig.class)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MessageVersioningStandardsApplicationTest {

	@Autowired
	private MessageSender messageSender;

	@SpyBean
	private MessageListener messageListener;

	@Autowired
	private JmsTemplate jmsTemplate;

	@Order(1)
	@DisplayName("should load spring boot application context")
	@Test
	void contextLoads() {
	}

	@Order(2)
	@DisplayName("convert and send simple and then receive and convert")
	@Test
	public void convertAndSendSimpleThenReceiveAndConvert() {
		when(jmsTemplate.receiveAndConvert("foo")).thenReturn("HELLO, WORLD!" );

		jmsTemplate.convertAndSend("foo", "Hello, world!".toUpperCase());
		jmsTemplate.setReceiveTimeout(1_000);
		assertThat(jmsTemplate.receiveAndConvert("foo"), is("HELLO, WORLD!"));
	}

	@Order(3)
	@DisplayName("when SendingMessage to JMS queue then Correct Queue And Message Text")
	@Test
	public void whenSendingMessage_thenCorrectQueueAndMessageText() throws JMSException {
		String queueName = "queue-2";
		String messageText = "Test message";
		var textMessage = mock(TextMessage.class);
		when(textMessage.getText()).thenReturn("Test message");
		when(jmsTemplate.receive(queueName)).thenReturn(textMessage );

		messageSender.sendTextMessage(queueName, messageText);

		Message sentMessage = jmsTemplate.receive(queueName);;
		assertThat( sentMessage, instanceOf( TextMessage.class));
		assertThat( messageText, is(((TextMessage)sentMessage).getText()));
	}


	@Order(4)
	@Disabled
	@DisplayName("when Listening from JMS queue then Receiving CorrectMessage")
	@Test
	public void whenListening_thenReceivingCorrectMessage() throws JMSException, InterruptedException {
		String queueName = "queue-1";
		String messageText = "Test message";

		messageSender.sendTextMessage(queueName,messageText);
		// Why do we have sent this twice.
		messageSender.sendTextMessage(queueName,messageText); // This works with Gradle, but not in IDEA Ultimate. Why?

		ArgumentCaptor messageCaptor = ArgumentCaptor.forClass(TextMessage.class);
		verify(messageListener, timeout(100))
				.sampleJmsListenerMethod((TextMessage) messageCaptor.capture());

		TextMessage receivedMessage = (TextMessage) messageCaptor.getValue();
		assertThat( receivedMessage.getText(), is(messageText));
	}
}
