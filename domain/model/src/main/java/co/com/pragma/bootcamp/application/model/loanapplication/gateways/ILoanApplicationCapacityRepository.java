package co.com.pragma.bootcamp.application.model.loanapplication.gateways;

import co.com.pragma.bootcamp.application.model.loanapplication.LoanApplication;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ILoanApplicationCapacityRepository {
    Mono<String> sendAutoLoanApplicationCapacity(LoanApplication loanApplication, List<LoanApplication> approvedApplications);
}
