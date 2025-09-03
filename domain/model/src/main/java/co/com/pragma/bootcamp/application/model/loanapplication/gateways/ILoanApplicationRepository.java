package co.com.pragma.bootcamp.application.model.loanapplication.gateways;

import co.com.pragma.bootcamp.application.model.loanapplication.LoanApplication;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ILoanApplicationRepository {
    Mono<LoanApplication> save(LoanApplication loanApplication);
    Flux<LoanApplication> findLoanApplicationsByApplicationStatusNameIn(List<String> applicationStatusNames, Integer page, Integer size);
}
