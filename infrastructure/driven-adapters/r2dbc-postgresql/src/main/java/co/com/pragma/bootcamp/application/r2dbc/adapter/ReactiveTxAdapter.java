package co.com.pragma.bootcamp.application.r2dbc.adapter;

import co.com.pragma.bootcamp.application.model.transaccion.IReactiveTxPort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

@Component
public class ReactiveTxAdapter implements IReactiveTxPort {

    private final TransactionalOperator txOperator;

    public ReactiveTxAdapter(ReactiveTransactionManager txManager) {
        this.txOperator = TransactionalOperator.create(txManager);
    }

    @Override
    public <T> Mono<T> executeInTransaction(Mono<T> publisher) {
        return publisher.as(txOperator::transactional);
    }
}
