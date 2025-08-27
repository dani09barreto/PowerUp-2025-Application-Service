package co.com.pragma.bootcamp.application.usecase.registerapplication;

import co.com.pragma.bootcamp.application.model.applicationstatus.enums.ApplicationStatusEnum;
import co.com.pragma.bootcamp.application.model.applicationstatus.gateways.IApplicationStatusRepository;
import co.com.pragma.bootcamp.application.model.loanapplication.LoanApplication;
import co.com.pragma.bootcamp.application.model.loanapplication.gateways.ILoanApplicationRepository;
import co.com.pragma.bootcamp.application.model.loantype.gateways.ILoanTypeRepository;
import co.com.pragma.bootcamp.application.model.transaccion.IReactiveTxPort;
import co.com.pragma.bootcamp.application.model.user.gateways.IUserRepository;
import co.com.pragma.bootcamp.application.usecase.registerapplication.error.InvalidUserDataException;
import co.com.pragma.bootcamp.application.usecase.registerapplication.error.LoanTypeNotFoundException;
import co.com.pragma.bootcamp.application.usecase.registerapplication.error.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class RegisterApplicationUseCase implements IRegisterApplicationUseCase{

    private final ILoanTypeRepository loanTypeRepository;
    private final IApplicationStatusRepository applicationStatusRepository;
    private final ILoanApplicationRepository loanApplicationRepository;
    private final IUserRepository userRepository;
    private final IReactiveTxPort reactiveTxPort;

    @Override
    public Mono<LoanApplication> registerApplication(LoanApplication loanApplication) {
        validate(loanApplication);

        return reactiveTxPort.executeInTransaction(
                loanTypeRepository.findById(loanApplication.getLoanType().getId())
                        .switchIfEmpty(Mono.error(new LoanTypeNotFoundException("Invalid Loan Type ID: " + loanApplication.getLoanType().getId())))
                        .flatMap(loanType ->
                                applicationStatusRepository.findByName(ApplicationStatusEnum.PENDING.name())
                                        .switchIfEmpty(Mono.error(new InvalidUserDataException("Application Status 'PENDING' not found")))
                                        .flatMap(applicationStatus ->
                                                userRepository.findByNumberIdentification(loanApplication.getUser().getIdentificationNumber())
                                                        .switchIfEmpty(Mono.error(new UserNotFoundException("User not found with identification number: " + loanApplication.getUser().getIdentificationNumber())))
                                                        .flatMap(user -> {

                                                            LoanApplication loanApplicationToSave = LoanApplication.builder()
                                                                    .user(user)
                                                                    .amount(loanApplication.getAmount())
                                                                    .termMonths(loanApplication.getTermMonths())
                                                                    .applicationStatus(applicationStatus)
                                                                    .loanType(loanType)
                                                                    .build();

                                                            return loanApplicationRepository.save(loanApplicationToSave);
                                                        })
                                        )
                        )
        );
    }

    @Override
    public void validate(LoanApplication loanApplication) {

        if (loanApplication.getUser().getIdentificationNumber() == null || loanApplication.getUser().getIdentificationNumber().isEmpty()) {
            throw new InvalidUserDataException("User identification number is required");
        }

        if (loanApplication.getAmount() == null || loanApplication.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidUserDataException("Amount must be greater than zero");
        }

        if (loanApplication.getTermMonths() == null || loanApplication.getTermMonths() <= 0) {
            throw new InvalidUserDataException("Term in months must be greater than zero");
        }
    }
}
