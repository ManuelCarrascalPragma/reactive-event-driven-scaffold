package co.com.bancolombia.api;

import co.com.bancolombia.api.dto.TransactionRequest;
import co.com.bancolombia.usecase.transaction.TransactionUseCase;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class Handler {
    private final TransactionUseCase transactionUseCase;
    private final Validator validator;

    public Mono<ServerResponse> createTransaction(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(TransactionRequest.class)
                .flatMap(this::validate)
                .map(TransactionRequest::toDomain)
                .flatMap(transactionUseCase::processTransaction)
                .flatMap(savedTransaction -> ServerResponse.accepted().bodyValue(savedTransaction))
                .onErrorResume(IllegalArgumentException.class, 
                        e -> ServerResponse.badRequest().bodyValue(e.getMessage()))
                .onErrorResume(e -> ServerResponse.status(500).bodyValue("Internal error: " + e.getMessage()));
    }
    
    private Mono<TransactionRequest> validate(TransactionRequest request) {
        var violations = validator.validate(request);
        if (!violations.isEmpty()) {
            String errors = violations.stream()
                    .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                    .collect(Collectors.joining(", "));
            return Mono.error(new IllegalArgumentException("Validation failed: " + errors));
        }
        return Mono.just(request);
    }
}
