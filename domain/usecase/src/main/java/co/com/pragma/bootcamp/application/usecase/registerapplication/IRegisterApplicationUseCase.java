package co.com.pragma.bootcamp.application.usecase.registerapplication;

import co.com.pragma.bootcamp.application.model.loanapplication.LoanApplication;
import reactor.core.publisher.Mono;

public interface IRegisterApplicationUseCase {
    Mono<LoanApplication> registerApplication(LoanApplication loanApplication);
    void validate(LoanApplication loanApplication);
}
