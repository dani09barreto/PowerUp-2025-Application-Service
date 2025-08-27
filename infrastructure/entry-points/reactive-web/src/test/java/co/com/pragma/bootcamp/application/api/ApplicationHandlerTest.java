package co.com.pragma.bootcamp.application.api;

import co.com.pragma.bootcamp.application.api.dto.RegisterApplicationRequest;
import co.com.pragma.bootcamp.application.api.dto.RegisterApplicationResponse;
import co.com.pragma.bootcamp.application.api.mapper.ApplicationDtoMapper;
import co.com.pragma.bootcamp.application.usecase.registerapplication.IRegisterApplicationUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ApplicationHandlerTest {

    @Mock
    private IRegisterApplicationUseCase registerApplicationUseCase;

    private ApplicationHandler applicationHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        applicationHandler = new ApplicationHandler(registerApplicationUseCase);
    }

    @Test
    @DisplayName("Should return ServerResponse.ok when application is registered successfully")
    void shouldReturnOkWhenApplicationRegisteredSuccessfully() {
        // Arrange
        RegisterApplicationRequest request = generateRegisterApplicationRequestData();
        RegisterApplicationResponse response = generateRegisterApplicationResponseData();
        ServerRequest serverRequest = mock(ServerRequest.class);

        when(serverRequest.bodyToMono(RegisterApplicationRequest.class))
                .thenReturn(Mono.just(request));
        when(registerApplicationUseCase.registerApplication(any()))
                .thenReturn(Mono.just(ApplicationDtoMapper.toLoanApplication(request)));
        when(registerApplicationUseCase.registerApplication(any()))
                .thenReturn(Mono.just(ApplicationDtoMapper.toLoanApplication(request)));

        // Act
        Mono<ServerResponse> result = applicationHandler.registerApplication(serverRequest);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();

        verify(registerApplicationUseCase, times(1)).registerApplication(any());
    }

    @Test
    @DisplayName("Should return error response when request body is invalid")
    void shouldReturnErrorWhenRequestBodyInvalid() {
        // Arrange
        ServerRequest serverRequest = mock(ServerRequest.class);

        when(serverRequest.bodyToMono(RegisterApplicationRequest.class))
                .thenReturn(Mono.error(new IllegalArgumentException("Invalid request")));

        // Act
        Mono<ServerResponse> result = applicationHandler.registerApplication(serverRequest);

        // Assert
        StepVerifier.create(result)
                .expectError(IllegalArgumentException.class)
                .verify();

        verify(registerApplicationUseCase, never()).registerApplication(any());
    }

    RegisterApplicationResponse generateRegisterApplicationResponseData(){

        return new RegisterApplicationResponse(
                1L,
                1L,
                1L,
                1L,
                12,
                new BigDecimal("5000")
        );
    }

    RegisterApplicationRequest generateRegisterApplicationRequestData(){
        return new RegisterApplicationRequest(
                "1234",
                1L,
                new BigDecimal("5000"),
                12
        );
    }
}