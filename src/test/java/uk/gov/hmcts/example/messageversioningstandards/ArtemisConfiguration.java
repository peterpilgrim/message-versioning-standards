package uk.gov.hmcts.example.messageversioningstandards;

import org.springframework.boot.autoconfigure.jms.artemis.ArtemisConfigurationCustomizer;

public class ArtemisConfiguration implements ArtemisConfigurationCustomizer {
    @Override
    public void customize(org.apache.activemq.artemis.core.config.Configuration configuration) {
        try {
            configuration.addAcceptorConfiguration("remote", "tcp://localhost:61616");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
