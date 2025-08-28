package co.com.pragma.bootcamp.application.r2dbc.adapter;

import co.com.pragma.bootcamp.application.model.loantype.LoanType;
import co.com.pragma.bootcamp.application.model.loantype.gateways.ILoanTypeRepository;
import co.com.pragma.bootcamp.application.r2dbc.entity.LoanTypeEntity;
import co.com.pragma.bootcamp.application.r2dbc.repository.LoanTypeReactiveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Repository
public class LoanTypeRepositoryAdapter implements ILoanTypeRepository {

    private final LoanTypeReactiveRepository repository;

    @Override
    public Mono<LoanType> findById(Long id) {
        return repository.findById(id)
                .map(LoanTypeEntity::toDomain)
                .switchIfEmpty(Mono.empty());
    }
}
