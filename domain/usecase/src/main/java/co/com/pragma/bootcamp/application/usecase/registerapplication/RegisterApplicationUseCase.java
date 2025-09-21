package co.com.pragma.bootcamp.application.usecase.registerapplication;

import co.com.pragma.bootcamp.application.model.applicationstatus.enums.ApplicationStatusEnum;
import co.com.pragma.bootcamp.application.model.applicationstatus.gateways.IApplicationStatusRepository;
import co.com.pragma.bootcamp.application.model.loanapplication.LoanApplication;
import co.com.pragma.bootcamp.application.model.loanapplication.gateways.ILoanApplicationCapacityRepository;
import co.com.pragma.bootcamp.application.model.loanapplication.gateways.ILoanApplicationRepository;
import co.com.pragma.bootcamp.application.model.loantype.gateways.ILoanTypeRepository;
import co.com.pragma.bootcamp.application.model.transaccion.IReactiveTxPort;
import co.com.pragma.bootcamp.application.model.user.gateways.IUserRepository;
import co.com.pragma.bootcamp.application.usecase.error.InvalidUserDataException;
import co.com.pragma.bootcamp.application.usecase.error.LoanTypeNotFoundException;
import co.com.pragma.bootcamp.application.usecase.error.UserNotFoundException;
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
    private final ILoanApplicationCapacityRepository loanApplicationCapacityRepository;

    @Override
    public Mono<LoanApplication> registerApplication(LoanApplication loanApplication, Mono<String> currentUser) {
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
                                                        .flatMap(user ->
                                                                currentUser.flatMap(current -> {
                                                                    if (!current.equals(user.getEmail())) {
                                                                        return Mono.error(new InvalidUserDataException(
                                                                                "El usuario autenticado no coincide con el usuario de la solicitud"
                                                                        ));
                                                                    }

                                                                    LoanApplication loanApplicationToSave = LoanApplication.builder()
                                                                            .user(user)
                                                                            .amount(loanApplication.getAmount())
                                                                            .termMonths(loanApplication.getTermMonths())
                                                                            .applicationStatus(applicationStatus)
                                                                            .annualRate(loanApplication.getAnnualRate())
                                                                            .loanType(loanType)
                                                                            .build();

                                                                    return loanApplicationRepository.save(loanApplicationToSave);
                                                                })
                                                        )
                                        )
                        )
        )
                .doOnSuccess(loanApplicationSaved -> {

                    loanTypeRepository.findById(loanApplicationSaved.getLoanType().getId())
                            .flatMap(loanType -> {
                                if (Boolean.TRUE.equals(loanType.getAutoValidation())) {
                                    return userRepository.findById(loanApplicationSaved.getUser().getId())
                                            .flatMap(user -> {
                                                loanApplicationSaved.setUser(user);
                                                return applicationStatusRepository.findByName(ApplicationStatusEnum.APPROVED.name());
                                            }
                                            )
                                            .flatMap(applicationStatus ->
                                                    loanApplicationRepository.findAllByApplicationStatusId(applicationStatus.getId())
                                                            .collectList()
                                                            .flatMap(approvedApplications ->
                                                                    loanApplicationCapacityRepository.sendAutoLoanApplicationCapacity(loanApplicationSaved, approvedApplications)
                                                                            .flatMap(responseStatusName ->
                                                                                    applicationStatusRepository.findByName(responseStatusName)
                                                                                            .flatMap(newStatus -> {
                                                                                                loanApplicationSaved.setApplicationStatus(newStatus);
                                                                                                return loanApplicationRepository.save(loanApplicationSaved);
                                                                                            })
                                                                            )
                                                            )
                                            );
                                } else {
                                    // Si autoValidation es false, no hace nada y retorna vacío
                                    return Mono.empty();
                                }
                            })
                            .subscribe(
                                    updated -> System.out.println("LoanApplication actualizado con status externo"),
                                    err -> System.err.println("Error en proceso asincrónico: " + err.getMessage())
                            );
                });

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
