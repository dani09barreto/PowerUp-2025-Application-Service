package co.com.pragma.bootcamp.application.r2dbc.adapter;

import co.com.pragma.bootcamp.application.model.applicationstatus.ApplicationStatus;
import co.com.pragma.bootcamp.application.model.loanapplication.LoanApplication;
import co.com.pragma.bootcamp.application.model.loantype.LoanType;
import co.com.pragma.bootcamp.application.model.user.User;
import co.com.pragma.bootcamp.application.r2dbc.entity.LoanApplicationEntity;
import co.com.pragma.bootcamp.application.r2dbc.repository.LoanApplicationReactiveRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class LoanApplicationRepositoryAdapterTest {

    @Mock
    private LoanApplicationReactiveRepository reactiveRepository;

    @InjectMocks
    private LoanApplicationRepositoryAdapter adapter;

    private LoanApplication domainLoanApplication;
    private LoanApplicationEntity entity;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        domainLoanApplication = LoanApplication.builder()
                .id(1L)
                .user(User.builder().id(10L).build())
                .amount(BigDecimal.valueOf(5000))
                .termMonths(12)
                .loanType(LoanType.builder().id(20L).build())
                .applicationStatus(ApplicationStatus.builder().id(30L).build())
                .build();

        entity = LoanApplicationEntity.builder()
                .id(1L)
                .userId(10L)
                .amount(BigDecimal.valueOf(5000))
                .termMonths(12)
                .loanTypeId(20L)
                .applicationStatusId(30L)
                .build();
    }

    @Test
    void save_ShouldConvertDomainToEntity_AndReturnDomain() {
        // Arrange
        when(reactiveRepository.save(any(LoanApplicationEntity.class))).thenReturn(Mono.just(entity));

        // Act
        Mono<LoanApplication> result = adapter.save(domainLoanApplication);

        // Assert
        StepVerifier.create(result)
                .assertNext(saved -> {
                    assertThat(saved.getId()).isEqualTo(1L);
                    assertThat(saved.getUser().getId()).isEqualTo(10L);
                    assertThat(saved.getAmount()).isEqualTo(BigDecimal.valueOf(5000));
                    assertThat(saved.getTermMonths()).isEqualTo(12);
                    assertThat(saved.getLoanType().getId()).isEqualTo(20L);
                    assertThat(saved.getApplicationStatus().getId()).isEqualTo(30L);
                })
                .verifyComplete();

        // Verifica que realmente se llamó al repo
        ArgumentCaptor<LoanApplicationEntity> captor = ArgumentCaptor.forClass(LoanApplicationEntity.class);
        verify(reactiveRepository, times(1)).save(captor.capture());

        LoanApplicationEntity captured = captor.getValue();
        assertThat(captured.getUserId()).isEqualTo(10L);
        assertThat(captured.getAmount()).isEqualTo(BigDecimal.valueOf(5000));
        assertThat(captured.getLoanTypeId()).isEqualTo(20L);
        assertThat(captured.getApplicationStatusId()).isEqualTo(30L);
    }
}
