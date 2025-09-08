package co.com.pragma.bootcamp.application.usecase.updateapplication;

import co.com.pragma.bootcamp.application.model.loanapplication.LoanApplication;
import reactor.core.publisher.Mono;

public interface IUpdateApplicationUseCase {
    Mono<LoanApplication> updateApplication(Long applicationId, String status);
}
