package co.com.pragma.bootcamp.application.r2dbc.repository;

import co.com.pragma.bootcamp.application.r2dbc.entity.LoanApplicationEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.List;

public interface LoanApplicationReactiveRepository extends ReactiveCrudRepository<LoanApplicationEntity, Long>, ReactiveQueryByExampleExecutor<LoanApplicationEntity> {

    @Query("select * from loan_applications la inner join application_status aps on aps.id = la.application_status_id where aps.name in (:applicationStatusNames)")
    Flux<LoanApplicationEntity> findByApplicationStatusName(List<String> applicationStatusNames);
}
