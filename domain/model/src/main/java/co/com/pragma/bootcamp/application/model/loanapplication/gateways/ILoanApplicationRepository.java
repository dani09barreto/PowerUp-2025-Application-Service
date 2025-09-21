package co.com.pragma.bootcamp.application.model.loanapplication.gateways;

import co.com.pragma.bootcamp.application.model.loanapplication.LoanApplication;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ILoanApplicationRepository {
    Mono<LoanApplication> save(LoanApplication loanApplication);
    Flux<LoanApplication> findLoanApplicationsByApplicationStatusNameIn(List<String> applicationStatusNames, Integer page, Integer size);
    Mono<LoanApplication> findById(Long id);
    Mono<LoanApplication> update(LoanApplication loanApplication);
    Flux<LoanApplication> findAllByApplicationStatusId(Long applicationTypeId);
}
