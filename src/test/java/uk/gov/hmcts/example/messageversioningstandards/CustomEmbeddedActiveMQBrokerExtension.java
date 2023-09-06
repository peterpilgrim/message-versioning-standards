package uk.gov.hmcts.example.messageversioningstandards;

import org.apache.activemq.broker.BrokerService;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class CustomEmbeddedActiveMQBrokerExtension implements AfterEachCallback, BeforeEachCallback {

    BrokerService brokerService = new BrokerService();
    @Override
    public void afterEach(ExtensionContext extensionContext) throws Exception {
        brokerService.setBrokerName("peter");
        brokerService.addConnector("tcp://localhost:61616");
        brokerService.start();
    }

    @Override
    public void beforeEach(ExtensionContext extensionContext) throws Exception {
        brokerService.stop();
    }
}
