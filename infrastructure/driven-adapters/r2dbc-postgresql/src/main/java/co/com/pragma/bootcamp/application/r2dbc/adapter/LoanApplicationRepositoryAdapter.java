package co.com.pragma.bootcamp.application.r2dbc.adapter;

import co.com.pragma.bootcamp.application.model.loanapplication.LoanApplication;
import co.com.pragma.bootcamp.application.model.loanapplication.gateways.ILoanApplicationRepository;
import co.com.pragma.bootcamp.application.r2dbc.entity.LoanApplicationEntity;
import co.com.pragma.bootcamp.application.r2dbc.repository.LoanApplicationReactiveRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

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

    @Override
    public Flux<LoanApplication> findLoanApplicationsByApplicationStatusNameIn(List<String> applicationStatusNames, Integer page, Integer size) {
        log.info("Finding LoanApplications with status names: {} | page: {} | size: {}", applicationStatusNames, page, size);

        return repository.findByApplicationStatusName(applicationStatusNames)
                .doOnNext(entity -> log.info("Found LoanApplication"))
                .skip((long) page * size)
                .take(size)
                .map(LoanApplicationEntity::toDomain);
    }

    @Override
    public Mono<LoanApplication> findById(Long id) {
        log.info("Finding LoanApplication by ID: {}", id);
        return repository.findById(id)
                .map(LoanApplicationEntity::toDomain);
    }

    @Override
    public Mono<LoanApplication> update(LoanApplication loanApplication) {
        log.info("Updating LoanApplication: {}", loanApplication);
        return repository.save(LoanApplicationEntity.fromDomain(loanApplication))
                .map(LoanApplicationEntity::toDomain);
    }
}
