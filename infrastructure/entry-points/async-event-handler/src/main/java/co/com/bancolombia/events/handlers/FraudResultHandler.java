package co.com.bancolombia.events.handlers;

import co.com.bancolombia.model.transaction.Transaction;
import co.com.bancolombia.usecase.transaction.TransactionUseCase;
import lombok.RequiredArgsConstructor;
import org.reactivecommons.async.api.HandlerRegistry;
import org.reactivecommons.async.impl.config.annotations.EnableMessageListeners;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@RequiredArgsConstructor
@EnableMessageListeners
public class FraudResultHandler {
    private final TransactionUseCase transactionUseCase;

    @Bean
    @Primary
    public HandlerRegistry fraudResultHandlers() {
        return HandlerRegistry.register()
                .listenEvent(
                        "fraudanalyzer.result",
                        event -> transactionUseCase.updateTransactionStatus(event.getData()).then(),
                        Transaction.class
                );
    }
}
