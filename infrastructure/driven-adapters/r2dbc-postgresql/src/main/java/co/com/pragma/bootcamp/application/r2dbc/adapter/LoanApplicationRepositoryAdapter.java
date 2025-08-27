package co.com.pragma.bootcamp.application.r2dbc.adapter;

import co.com.pragma.bootcamp.application.model.loanapplication.LoanApplication;
import co.com.pragma.bootcamp.application.model.loanapplication.gateways.ILoanApplicationRepository;
import co.com.pragma.bootcamp.application.r2dbc.entity.LoanApplicationEntity;
import co.com.pragma.bootcamp.application.r2dbc.repository.LoanApplicationReactiveRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Log4j2
@RequiredArgsConstructor
@Repository
public class LoanApplicationRepositoryAdapter implements ILoanApplicationRepository {

    private final LoanApplicationReactiveRepository repository;

    @Transactional
    @Override
    public Mono<LoanApplication> save(LoanApplication loanApplication) {
        log.info("Saving LoanApplication: {}", loanApplication);
        return repository.save(LoanApplicationEntity.fromDomain(loanApplication))
                .doOnNext(entity -> log.info("Saved LoanApplication: {}", entity))
                .map(LoanApplicationEntity::toDomain);
    }
}
