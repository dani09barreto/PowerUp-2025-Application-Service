package co.com.pragma.bootcamp.application.r2dbc.adapter;

import co.com.pragma.bootcamp.application.model.applicationstatus.ApplicationStatus;
import co.com.pragma.bootcamp.application.r2dbc.entity.ApplicationStatusEntity;
import co.com.pragma.bootcamp.application.r2dbc.repository.ApplicationStatusReactiveRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ApplicationStatusRepositoryAdapterTest {

    @Mock
    private ApplicationStatusReactiveRepository reactiveRepository;

    @InjectMocks
    private ApplicationStatusRepositoryAdapter adapter;

    private ApplicationStatusEntity entity;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        entity = ApplicationStatusEntity.builder()
                .id(1L)
                .name("APPROVED")
                .description("Application approved")
                .build();
    }

    @Test
    void findByName_ShouldReturnDomainObject_WhenEntityExists() {
        // Arrange
        when(reactiveRepository.findByName("APPROVED")).thenReturn(Mono.just(entity));

        // Act
        Mono<ApplicationStatus> result = adapter.findByName("APPROVED");

        // Assert
        StepVerifier.create(result)
                .assertNext(status -> {
                    assertThat(status.getId()).isEqualTo(1L);
                    assertThat(status.getName()).isEqualTo("APPROVED");
                    assertThat(status.getDescription()).isEqualTo("Application approved");
                })
                .verifyComplete();

        verify(reactiveRepository, times(1)).findByName("APPROVED");
    }

    @Test
    void findByName_ShouldReturnEmpty_WhenEntityDoesNotExist() {
        // Arrange
        when(reactiveRepository.findByName("REJECTED")).thenReturn(Mono.empty());

        // Act
        Mono<ApplicationStatus> result = adapter.findByName("REJECTED");

        // Assert
        StepVerifier.create(result)
                .verifyComplete();

        verify(reactiveRepository, times(1)).findByName("REJECTED");
    }
}
