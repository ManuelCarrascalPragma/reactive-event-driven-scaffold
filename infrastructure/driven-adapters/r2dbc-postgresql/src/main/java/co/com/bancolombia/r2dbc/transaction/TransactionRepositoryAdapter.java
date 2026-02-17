package co.com.bancolombia.r2dbc.transaction;

import co.com.bancolombia.model.transaction.Transaction;
import co.com.bancolombia.model.transaction.gateways.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Repository
@RequiredArgsConstructor
public class TransactionRepositoryAdapter implements TransactionRepository {
    private final TransactionR2dbcRepository r2dbcRepository;

    @Override
    public Mono<Transaction> save(Transaction transaction) {
        return r2dbcRepository.findById(transaction.getId())
                .flatMap(existing -> {
                    TransactionEntity updated = TransactionEntity.builder()
                            .id(transaction.getId())
                            .accountId(transaction.getAccountId())
                            .amount(transaction.getAmount())
                            .currency(transaction.getCurrency())
                            .status(transaction.getStatus())
                            .createdAt(existing.getCreatedAt())
                            .updatedAt(LocalDateTime.now())
                            .isNew(false)
                            .build();
                    return r2dbcRepository.save(updated);
                })
                .switchIfEmpty(
                    r2dbcRepository.save(TransactionEntity.fromDomain(transaction))
                )
                .map(TransactionEntity::toDomain);
    }

    @Override
    public Mono<Transaction> findById(String id) {
        return r2dbcRepository.findById(id)
                .map(TransactionEntity::toDomain);
    }
    
    @Override
    public Flux<Transaction> findAllByStatusAndCreatedAtBefore(String status, LocalDateTime before) {
        return r2dbcRepository.findAllByStatusAndCreatedAtBefore(status, before)
                .map(TransactionEntity::toDomain);
    }
}
