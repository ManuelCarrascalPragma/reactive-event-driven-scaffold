package co.com.bancolombia.config;

import co.com.bancolombia.model.events.config.EventTopics;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class EventTopicsConfig implements EventTopics {

    @Value("${events.topics.transaction-received}")
    private String transactionReceived;
}
