package co.com.pragma.bootcamp.application.model.loantype.gateways;

import co.com.pragma.bootcamp.application.model.loantype.LoanType;
import reactor.core.publisher.Mono;

public interface ILoanTypeRepository {
    Mono<LoanType> findById(Long id);
}
