package co.com.pragma.bootcamp.application.api.error;

import co.com.pragma.bootcamp.application.usecase.registerapplication.error.LoanTypeNotFoundException;
import co.com.pragma.bootcamp.application.usecase.registerapplication.error.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

class ErrorFilterTest {

    private ErrorFilter errorFilter;

    @Mock
    private ServerRequest serverRequest;

    @Mock
    private HandlerFunction<ServerResponse> handlerFunction;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        errorFilter = new ErrorFilter();
    }

    @Test
    @DisplayName("Should handle UserNotFoundException and return NOT_FOUND response")
    void shouldHandleUserNotFoundException() {
        // Arrange
        when(handlerFunction.handle(serverRequest))
                .thenReturn(Mono.error(new UserNotFoundException("User not found")));
        when(serverRequest.path()).thenReturn("/test-path");

        // Act
        Mono<ServerResponse> result = errorFilter.filter(serverRequest, handlerFunction);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(response -> response.statusCode() == HttpStatus.NOT_FOUND)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should handle LoanTypeNotFoundException and return NOT_FOUND response")
    void shouldHandleLoanTypeNotFoundException() {
        // Arrange
        when(handlerFunction.handle(serverRequest))
                .thenReturn(Mono.error(new LoanTypeNotFoundException("Loan type not found")));
        when(serverRequest.path()).thenReturn("/test-path");

        // Act
        Mono<ServerResponse> result = errorFilter.filter(serverRequest, handlerFunction);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(response -> response.statusCode() == HttpStatus.NOT_FOUND)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should handle generic Exception and return INTERNAL_SERVER_ERROR response")
    void shouldHandleGenericException() {
        // Arrange
        when(handlerFunction.handle(serverRequest))
                .thenReturn(Mono.error(new RuntimeException("Generic error")));
        when(serverRequest.path()).thenReturn("/test-path");

        // Act
        Mono<ServerResponse> result = errorFilter.filter(serverRequest, handlerFunction);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(response -> response.statusCode() == HttpStatus.INTERNAL_SERVER_ERROR)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should pass through without errors when no exception occurs")
    void shouldPassThroughWithoutErrors() {
        // Arrange
        ServerResponse mockResponse = ServerResponse.ok().build().block();
        when(handlerFunction.handle(serverRequest)).thenReturn(Mono.just(mockResponse));

        // Act
        Mono<ServerResponse> result = errorFilter.filter(serverRequest, handlerFunction);

        // Assert
        StepVerifier.create(result)
                .expectNext(mockResponse)
                .verifyComplete();
    }
}