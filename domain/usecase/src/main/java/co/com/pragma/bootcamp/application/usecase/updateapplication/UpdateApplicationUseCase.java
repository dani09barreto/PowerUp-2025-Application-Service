package co.com.pragma.bootcamp.application.usecase.updateapplication;

import co.com.pragma.bootcamp.application.model.applicationstatus.gateways.IApplicationStatusRepository;
import co.com.pragma.bootcamp.application.model.creditrequeststatus.CreditRequestStatus;
import co.com.pragma.bootcamp.application.model.creditrequeststatus.gateways.CreditRequestStatusRepository;
import co.com.pragma.bootcamp.application.model.loanapplication.LoanApplication;
import co.com.pragma.bootcamp.application.model.loanapplication.gateways.ILoanApplicationRepository;
import co.com.pragma.bootcamp.application.model.user.gateways.IUserRepository;
import co.com.pragma.bootcamp.application.usecase.error.ApplicationStatusNotFoundException;
import co.com.pragma.bootcamp.application.usecase.error.LoanApplicationNotFound;
import co.com.pragma.bootcamp.application.usecase.error.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UpdateApplicationUseCase implements IUpdateApplicationUseCase{

    private final IApplicationStatusRepository applicationStatusRepository;
    private final ILoanApplicationRepository loanApplicationRepository;
    private final IUserRepository userRepository;
    private final CreditRequestStatusRepository creditRequestStatusRepository;

    @Override
    public Mono<LoanApplication> updateApplication(Long applicationId, String status) {

        return applicationStatusRepository.findByName(status)
                .switchIfEmpty(Mono.error(new ApplicationStatusNotFoundException("Application Status not found: " + status)))
                .flatMap(applicationStatus ->
                        loanApplicationRepository.findById(applicationId)
                                .switchIfEmpty(Mono.error(new LoanApplicationNotFound("Loan Application not found with ID: " + applicationId)))
                                .flatMap(loanApplication ->
                                        userRepository.findById(loanApplication.getUser().getId())
                                                .switchIfEmpty(Mono.error(new UserNotFoundException("User not found with ID: " + loanApplication.getUser().getId())))
                                                .flatMap(user -> {

                                                    loanApplication.setApplicationStatus(applicationStatus);

                                                    Mono<LoanApplication> updatedLoanApplication = loanApplicationRepository.update(loanApplication);

                                                    creditRequestStatusRepository.sendCreditRequestStatus(
                                                            CreditRequestStatus.builder()
                                                                    .clientEmail(user.getEmail())
                                                                    .termMonths(loanApplication.getTermMonths())
                                                                    .amount(loanApplication.getAmount())
                                                                    .annualRate(loanApplication.getAnnualRate())
                                                                    .applicationStatus(applicationStatus.getName())
                                                                    .build()
                                                    )
                                                            .log()
                                                            .subscribe();

                                                    return updatedLoanApplication;
                                                })
                                )
                );
    }
}
