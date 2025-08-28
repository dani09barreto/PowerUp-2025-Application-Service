package co.com.pragma.bootcamp.application.model.transaccion;

import reactor.core.publisher.Mono;

public interface IReactiveTxPort {
    <T> Mono<T> executeInTransaction(Mono<T> publisher);
}
