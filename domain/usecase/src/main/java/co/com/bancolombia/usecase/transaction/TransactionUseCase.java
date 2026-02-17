package co.com.bancolombia.usecase.transaction;

import co.com.bancolombia.model.events.config.EventTopics;
import co.com.bancolombia.model.events.gateways.EventGateway;
import co.com.bancolombia.model.transaction.Transaction;
import co.com.bancolombia.model.transaction.gateways.TransactionRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@RequiredArgsConstructor
public class TransactionUseCase {

    private final EventGateway<Transaction> eventGateway;
    private final EventTopics eventTopics;
    private final TransactionRepository transactionRepository;

    public Mono<Transaction> processTransaction(Transaction transaction) {
        String txId = transaction.getId() != null ? transaction.getId() : java.util.UUID.randomUUID().toString();
        
        return transactionRepository.findById(txId)
                .flatMap(existing -> validateExistingTransaction(existing, transaction))
                .switchIfEmpty(
                    Mono.just(transaction.toBuilder().id(txId).status("PENDING").build())
                        .flatMap(transactionRepository::save)
                        .flatMap(savedTx -> eventGateway.emit(
                                eventTopics.getTransactionReceived(), 
                                savedTx.getId(), 
                                savedTx)
                                .thenReturn(savedTx))
                );
    }
    
    private Mono<Transaction> validateExistingTransaction(Transaction existing, Transaction incoming) {
        if (existing.getAccountId().equals(incoming.getAccountId()) 
                && existing.getAmount().equals(incoming.getAmount())
                && existing.getCurrency().equals(incoming.getCurrency())) {
            return Mono.just(existing);
        }
        return Mono.error(new IllegalArgumentException(
                "Transaction ID " + existing.getId() + " already exists with different data. " +
                "Possible fraud attempt or data inconsistency detected."));
    }
    
    public Mono<Transaction> updateTransactionStatus(Transaction transaction) {
        return transactionRepository.findById(transaction.getId())
                .flatMap(existing -> {
                    Transaction updated = existing.toBuilder()
                            .status(transaction.getStatus())
                            .build();
                    return transactionRepository.save(updated);
                })
                .switchIfEmpty(Mono.error(new RuntimeException("Transaction not found: " + transaction.getId())));
    }
    
    public Mono<Long> processZombieTransactions(int timeoutMinutes) {
        LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(timeoutMinutes);
        
        return transactionRepository.findAllByStatusAndCreatedAtBefore("PENDING", cutoffTime)
                .flatMap(zombie -> {
                    Transaction failed = zombie.toBuilder()
                            .status("TIMEOUT")
                            .build();
                    return transactionRepository.save(failed);
                })
                .count();
    }
}
