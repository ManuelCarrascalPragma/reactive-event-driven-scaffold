package co.com.bancolombia.model.events.gateways;

import reactor.core.publisher.Mono;

public interface EventGateway<T> {
    Mono<Void> emit(String eventName, String eventId, T payload);
}
