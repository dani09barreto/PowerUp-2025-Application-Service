package co.com.pragma.bootcamp.application.usecase.listapplications;

import co.com.pragma.bootcamp.application.model.applicationstatus.ApplicationStatus;
import co.com.pragma.bootcamp.application.model.applicationstatus.enums.ApplicationStatusEnum;
import co.com.pragma.bootcamp.application.model.applicationstatus.gateways.IApplicationStatusRepository;
import co.com.pragma.bootcamp.application.model.loanapplication.LoanApplication;
import co.com.pragma.bootcamp.application.model.loanapplication.gateways.ILoanApplicationRepository;
import co.com.pragma.bootcamp.application.model.loantype.LoanType;
import co.com.pragma.bootcamp.application.model.loantype.gateways.ILoanTypeRepository;
import co.com.pragma.bootcamp.application.model.user.User;
import co.com.pragma.bootcamp.application.model.user.gateways.IUserRepository;
import co.com.pragma.bootcamp.application.usecase.listapplications.dto.ApplicationListDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class ListApplicationsUseCaseTest {

    @Mock
    private ILoanApplicationRepository loanApplicationRepository;

    @Mock
    private IApplicationStatusRepository applicationStatusRepository;

    @Mock
    private ILoanTypeRepository loanTypeRepository;

    @Mock
    private IUserRepository userRepository;

    @InjectMocks
    private ListApplicationsUseCase listApplicationsUseCase;

    private LoanApplication mockLoanApplicationApproved;
    private LoanApplication mockLoanApplicationRejected;
    private LoanType mockLoanType;
    private ApplicationStatus approvedStatus;
    private ApplicationStatus rejectedStatus;
    private User mockUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mockLoanType = LoanType.builder()
                .id(1L)
                .name("Personal Loan")
                .build();

        approvedStatus = ApplicationStatus.builder()
                .id(1L)
                .name(ApplicationStatusEnum.APPROVED.name())
                .build();

        rejectedStatus = ApplicationStatus.builder()
                .id(2L)
                .name(ApplicationStatusEnum.REJECTED.name())
                .build();

        mockUser = User.builder()
                .id(1L)
                .firstName("Daniel")
                .email("test@example.com")
                .baseSalary(BigDecimal.valueOf(5000))
                .birthDate(LocalDate.of(1995, 1, 1))
                .build();

        mockLoanApplicationApproved = LoanApplication.builder()
                .id(1L)
                .amount(BigDecimal.valueOf(12000))
                .annualRate(BigDecimal.valueOf(0.12))
                .termMonths(12)
                .loanType(mockLoanType)
                .applicationStatus(approvedStatus)
                .user(mockUser)
                .build();

        mockLoanApplicationRejected = mockLoanApplicationApproved.toBuilder()
                .id(2L)
                .applicationStatus(rejectedStatus)
                .build();
    }

    @Test
    void listApplicationsApprovedStatus_ShouldCalculateMonthlyPayment() {
        when(loanApplicationRepository.findLoanApplicationsByApplicationStatusNameIn(any(), any(), any()))
                .thenReturn(Flux.just(mockLoanApplicationApproved));
        when(loanTypeRepository.findById(1L)).thenReturn(Mono.just(mockLoanType));
        when(applicationStatusRepository.findById(1L)).thenReturn(Mono.just(approvedStatus));
        when(userRepository.findById(1L)).thenReturn(Mono.just(mockUser));

        StepVerifier.create(listApplicationsUseCase.listApplicationsByApplicationStatus(
                        List.of(ApplicationStatusEnum.APPROVED.name()), 0, 10))
                .expectNextMatches(dto ->
                        dto.getFirstName().equals("Daniel") &&
                        dto.getLoadTypeName().equals("Personal Loan") &&
                        dto.getApplicationStatusName().equals(ApplicationStatusEnum.APPROVED.name()) &&
                        dto.getMonthlyPayment().compareTo(BigDecimal.ZERO) > 0 // Debe calcular cuota
                )
                .verifyComplete();
    }

    @Test
    void listApplicationsRejectedStatus_ShouldReturnZeroMonthlyPayment() {
        when(loanApplicationRepository.findLoanApplicationsByApplicationStatusNameIn(any(), any(), any()))
                .thenReturn(Flux.just(mockLoanApplicationRejected));
        when(loanTypeRepository.findById(1L)).thenReturn(Mono.just(mockLoanType));
        when(applicationStatusRepository.findById(2L)).thenReturn(Mono.just(rejectedStatus));
        when(userRepository.findById(1L)).thenReturn(Mono.just(mockUser));

        StepVerifier.create(listApplicationsUseCase.listApplicationsByApplicationStatus(
                        List.of(ApplicationStatusEnum.REJECTED.name()), 0, 10))
                .expectNextMatches(dto ->
                        dto.getApplicationStatusName().equals(ApplicationStatusEnum.REJECTED.name()) &&
                        dto.getMonthlyPayment().compareTo(BigDecimal.ZERO) == 0 // Debe ser cero
                )
                .verifyComplete();
    }

    @Test
    void listApplicationsEmpty_ShouldReturnEmptyFlux() {
        when(loanApplicationRepository.findLoanApplicationsByApplicationStatusNameIn(any(), any(), any()))
                .thenReturn(Flux.empty());

        StepVerifier.create(listApplicationsUseCase.listApplicationsByApplicationStatus(
                        List.of("ANY"), 0, 10))
                .verifyComplete();
    }
}
