package co.com.pragma.bootcamp.application.model.loanapplication.gateways;

import co.com.pragma.bootcamp.application.model.loanapplication.LoanApplication;
import reactor.core.publisher.Mono;

public interface ILoanApplicationRepository {
    Mono<LoanApplication> save(LoanApplication loanApplication);
}
