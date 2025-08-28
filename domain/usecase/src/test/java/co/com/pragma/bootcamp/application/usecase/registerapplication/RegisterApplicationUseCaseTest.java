package co.com.pragma.bootcamp.application.usecase.registerapplication;

import co.com.pragma.bootcamp.application.model.applicationstatus.ApplicationStatus;
import co.com.pragma.bootcamp.application.model.applicationstatus.enums.ApplicationStatusEnum;
import co.com.pragma.bootcamp.application.model.applicationstatus.gateways.IApplicationStatusRepository;
import co.com.pragma.bootcamp.application.model.loanapplication.LoanApplication;
import co.com.pragma.bootcamp.application.model.loanapplication.gateways.ILoanApplicationRepository;
import co.com.pragma.bootcamp.application.model.loantype.LoanType;
import co.com.pragma.bootcamp.application.model.loantype.gateways.ILoanTypeRepository;
import co.com.pragma.bootcamp.application.model.transaccion.IReactiveTxPort;
import co.com.pragma.bootcamp.application.model.user.User;
import co.com.pragma.bootcamp.application.model.user.gateways.IUserRepository;
import co.com.pragma.bootcamp.application.usecase.registerapplication.error.InvalidUserDataException;
import co.com.pragma.bootcamp.application.usecase.registerapplication.error.LoanTypeNotFoundException;
import co.com.pragma.bootcamp.application.usecase.registerapplication.error.UserNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RegisterApplicationUseCaseTest {

    private ILoanTypeRepository loanTypeRepository;
    private IApplicationStatusRepository applicationStatusRepository;
    private ILoanApplicationRepository loanApplicationRepository;
    private IUserRepository userRepository;
    private IReactiveTxPort reactiveTxPort;
    private RegisterApplicationUseCase useCase;

    @BeforeEach
    void setUp() {
        loanTypeRepository = mock(ILoanTypeRepository.class);
        applicationStatusRepository = mock(IApplicationStatusRepository.class);
        loanApplicationRepository = mock(ILoanApplicationRepository.class);
        userRepository = mock(IUserRepository.class);
        reactiveTxPort = mock(IReactiveTxPort.class);

        useCase = new RegisterApplicationUseCase(
                loanTypeRepository,
                applicationStatusRepository,
                loanApplicationRepository,
                userRepository,
                reactiveTxPort
        );
    }

    @Test
    void testRegisterApplicationSuccess() {
        LoanApplication loanApplication = createValidLoanApplication();

        when(loanTypeRepository.findById(any()))
                .thenReturn(Mono.just(LoanType.builder().id(1L).name("state").build()));
        when(applicationStatusRepository.findByName(ApplicationStatusEnum.PENDING.name()))
                .thenReturn(Mono.just(ApplicationStatus.builder().id(1L).description(ApplicationStatusEnum.PENDING.name()).build()));
        when(userRepository.findByNumberIdentification(any()))
                .thenReturn(Mono.just(User.builder().id(1L).identificationNumber("123").build()));
        when(loanApplicationRepository.save(any()))
                .thenReturn(Mono.just(loanApplication));
        when(reactiveTxPort.executeInTransaction(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        StepVerifier.create(useCase.registerApplication(loanApplication))
                .expectNext(loanApplication)
                .verifyComplete();

        verify(loanTypeRepository).findById(any());
        verify(applicationStatusRepository).findByName(any());
        verify(userRepository).findByNumberIdentification(any());
        verify(loanApplicationRepository).save(any());
    }

    @Test
    void testRegisterApplicationLoanTypeNotFound() {
        LoanApplication loanApplication = createValidLoanApplication();

        when(loanTypeRepository.findById(any()))
                .thenReturn(Mono.empty());

        when(reactiveTxPort.executeInTransaction(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        StepVerifier.create(useCase.registerApplication(loanApplication))
                .expectError(LoanTypeNotFoundException.class)
                .verify();

        verify(loanTypeRepository).findById(any());
        verifyNoInteractions(applicationStatusRepository, userRepository, loanApplicationRepository);
    }

    @Test
    void testRegisterApplicationUserNotFound() {
        LoanApplication loanApplication = createValidLoanApplication();

        when(loanTypeRepository.findById(any()))
                .thenReturn(Mono.just(LoanType.builder().id(1L).name("state").build()));
        when(applicationStatusRepository.findByName(ApplicationStatusEnum.PENDING.name()))
                .thenReturn(Mono.just(ApplicationStatus.builder().id(1L).description(ApplicationStatusEnum.PENDING.name()).build()));
        when(userRepository.findByNumberIdentification(any()))
                .thenReturn(Mono.empty());

        when(reactiveTxPort.executeInTransaction(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        StepVerifier.create(useCase.registerApplication(loanApplication))
                .expectError(UserNotFoundException.class)
                .verify();

        verify(userRepository).findByNumberIdentification(any());
    }

    @Test
    void testValidateInvalidUserIdentification() {
        LoanApplication loanApplication = createValidLoanApplication();
        loanApplication.getUser().setIdentificationNumber(null);

        Assertions.assertThrows(InvalidUserDataException.class, () -> {
            useCase.validate(loanApplication);
        });
    }

    @Test
    void testValidateInvalidTermsMonths() {
        LoanApplication loanApplication = createValidLoanApplication();
        loanApplication.setTermMonths(null);

        Assertions.assertThrows(InvalidUserDataException.class, () -> {
            useCase.validate(loanApplication);
        });
    }

    @Test
    void testValidateInvalidAmount() {
        LoanApplication loanApplication = createValidLoanApplication();
        loanApplication.setAmount(null);

        Assertions.assertThrows(InvalidUserDataException.class, () -> {
            useCase.validate(loanApplication);
        });
    }

    private LoanApplication createValidLoanApplication() {
        return LoanApplication.builder()
                .user(User.builder().identificationNumber("123").build())
                .amount(BigDecimal.valueOf(1000))
                .termMonths(12)
                .loanType(LoanType.builder().id(1L).build())
                .build();
    }
}