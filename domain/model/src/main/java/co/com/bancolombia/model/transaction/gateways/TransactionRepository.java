package co.com.bancolombia.model.transaction.gateways;

import co.com.bancolombia.model.transaction.Transaction;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public interface TransactionRepository {
    Mono<Transaction> save(Transaction transaction);
    Mono<Transaction> findById(String id);
    Flux<Transaction> findAllByStatusAndCreatedAtBefore(String status, LocalDateTime before);
}
