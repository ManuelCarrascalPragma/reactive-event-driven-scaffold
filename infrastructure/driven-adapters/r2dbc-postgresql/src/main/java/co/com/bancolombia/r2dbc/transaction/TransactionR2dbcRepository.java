package co.com.bancolombia.r2dbc.transaction;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;

public interface TransactionR2dbcRepository extends ReactiveCrudRepository<TransactionEntity, String> {
    Flux<TransactionEntity> findAllByStatusAndCreatedAtBefore(String status, LocalDateTime before);
}
