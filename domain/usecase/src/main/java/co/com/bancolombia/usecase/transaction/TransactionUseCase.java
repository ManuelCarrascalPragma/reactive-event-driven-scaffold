package co.com.bancolombia.usecase.transaction;

import co.com.bancolombia.model.events.config.EventTopics;
import co.com.bancolombia.model.events.gateways.EventGateway;
import co.com.bancolombia.model.transaction.Transaction;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class TransactionUseCase {

    private final EventGateway<Transaction> eventGateway;
    private final EventTopics eventTopics;

    public Mono<Void> processTransaction(Transaction transaction) {
        return Mono.just(transaction)
                .map(tx -> tx.toBuilder().status("PENDING").build())
                .flatMap(tx -> eventGateway.emit(
                        eventTopics.getTransactionReceived(), 
                        tx.getId(), 
                        tx));
    }
}
