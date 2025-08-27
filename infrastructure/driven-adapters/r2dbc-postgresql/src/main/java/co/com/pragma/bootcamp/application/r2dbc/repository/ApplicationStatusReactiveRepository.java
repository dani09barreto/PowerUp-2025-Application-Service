package co.com.pragma.bootcamp.application.r2dbc.repository;

import co.com.pragma.bootcamp.application.r2dbc.entity.ApplicationStatusEntity;
import co.com.pragma.bootcamp.application.r2dbc.entity.LoanTypeEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface ApplicationStatusReactiveRepository extends ReactiveCrudRepository<ApplicationStatusEntity, Long>, ReactiveQueryByExampleExecutor<ApplicationStatusEntity> {
    Mono<ApplicationStatusEntity> findByName(String name);
}
