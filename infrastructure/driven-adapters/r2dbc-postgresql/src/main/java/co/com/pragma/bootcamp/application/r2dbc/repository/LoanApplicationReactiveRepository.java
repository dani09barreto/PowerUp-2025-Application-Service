package co.com.pragma.bootcamp.application.r2dbc.repository;

import co.com.pragma.bootcamp.application.r2dbc.entity.LoanApplicationEntity;
import co.com.pragma.bootcamp.application.r2dbc.entity.LoanTypeEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface LoanApplicationReactiveRepository extends ReactiveCrudRepository<LoanApplicationEntity, Long>, ReactiveQueryByExampleExecutor<LoanApplicationEntity> {

}
