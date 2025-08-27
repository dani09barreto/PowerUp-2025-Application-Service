package co.com.pragma.bootcamp.application.r2dbc.adapter;

import co.com.pragma.bootcamp.application.model.loantype.LoanType;
import co.com.pragma.bootcamp.application.r2dbc.entity.LoanTypeEntity;
import co.com.pragma.bootcamp.application.r2dbc.repository.LoanTypeReactiveRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class LoanTypeRepositoryAdapterTest {

    @Mock
    private LoanTypeReactiveRepository reactiveRepository;

    @InjectMocks
    private LoanTypeRepositoryAdapter adapter;

    private LoanTypeEntity entity;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        entity = LoanTypeEntity.builder()
                .id(100L)
                .name("Personal Loan")
                .build();
    }

    @Test
    void findById_ShouldReturnDomain_WhenEntityExists() {
        // Arrange
        when(reactiveRepository.findById(100L)).thenReturn(Mono.just(entity));

        // Act
        Mono<LoanType> result = adapter.findById(100L);

        // Assert
        StepVerifier.create(result)
                .assertNext(loanType -> {
                    assertThat(loanType.getId()).isEqualTo(100L);
                    assertThat(loanType.getName()).isEqualTo("Personal Loan");
                })
                .verifyComplete();

        verify(reactiveRepository, times(1)).findById(100L);
    }

    @Test
    void findById_ShouldReturnEmpty_WhenEntityDoesNotExist() {
        // Arrange
        when(reactiveRepository.findById(200L)).thenReturn(Mono.empty());

        // Act
        Mono<LoanType> result = adapter.findById(200L);

        // Assert
        StepVerifier.create(result)
                .verifyComplete(); // No emite ningún valor

        verify(reactiveRepository, times(1)).findById(200L);
    }
}
