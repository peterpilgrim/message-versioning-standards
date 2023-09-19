package uk.gov.hmcts.example.messageversioningstandards;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.TextMessage;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.jms.core.JmsTemplate;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import org.apache.activemq.ActiveMQConnectionFactory;
import jakarta.jms.*;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
class MessageVersioningStandardsApplicationTest {

	final static int MQ_PORT = 61616;
	@Container
	public GenericContainer<?> activeMQContainer = new GenericContainer<>(DockerImageName.parse("rmohr/activemq"))
			.withExposedPorts(MQ_PORT);

	@Autowired
	private MessageSender messageSender;

	@SpyBean
	private MessageListener messageListener;

	@Autowired
	private JmsTemplate jmsTemplate;


	private Connection connection;
	private Session session;
	private MessageProducer producer;
	private MessageConsumer consumer;

	@BeforeEach
	public void setup() throws JMSException {
		String brokerUrl = "tcp://localhost:" + activeMQContainer.getMappedPort(MQ_PORT);
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerUrl);
		connection = connectionFactory.createConnection();
		connection.start();

		// Creating session for sending messages
		session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

		// Getting the queue
		Queue queue = session.createQueue("testQueue");

		// Creating the producer & consumer
		producer = session.createProducer(queue);
		consumer = session.createConsumer(queue);
	}

	@AfterEach
	public void tearDown() throws JMSException {
		// Cleaning up resources
		if (producer != null) producer.close();
		if (consumer != null) consumer.close();
		if (session != null) session.close();
		if (connection != null) connection.close();
	}

	@Order(1)
	@DisplayName("should load spring boot application context")
	@Test
	void contextLoads() {
	}

	@Order(2)
	@DisplayName("convert and send simple and then receive and convert")
	@Test
	public void convertAndSendSimpleThenReceiveAndConvert() {
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
		Mockito.verify(messageListener, Mockito.timeout(100))
				.sampleJmsListenerMethod((TextMessage) messageCaptor.capture());

		TextMessage receivedMessage = (TextMessage) messageCaptor.getValue();
		assertThat( receivedMessage.getText(), is(messageText));
	}
}
