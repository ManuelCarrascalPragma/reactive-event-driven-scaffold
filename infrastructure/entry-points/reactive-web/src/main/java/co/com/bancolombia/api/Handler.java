package co.com.bancolombia.api;

import co.com.bancolombia.model.transaction.Transaction;
import co.com.bancolombia.usecase.transaction.TransactionUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class Handler {
private final TransactionUseCase transactionUseCase;

    public Mono<ServerResponse> createTransaction(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(Transaction.class)
                .flatMap(transactionUseCase::processTransaction)
                .flatMap(unused -> ServerResponse.accepted().build())
                .onErrorResume(e -> ServerResponse.badRequest().bodyValue(e.getMessage()));
    }
}
