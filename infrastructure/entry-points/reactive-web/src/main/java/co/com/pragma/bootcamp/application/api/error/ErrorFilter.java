package co.com.pragma.bootcamp.application.api.error;

import co.com.pragma.bootcamp.application.usecase.registerapplication.error.LoanTypeNotFoundException;
import co.com.pragma.bootcamp.application.usecase.registerapplication.error.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.HandlerFilterFunction;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Slf4j
@Component
public class ErrorFilter implements HandlerFilterFunction<ServerResponse, ServerResponse> {

    @Override
    public Mono<ServerResponse> filter(ServerRequest request, HandlerFunction<ServerResponse> next) {
        return next.handle(request)
                .onErrorResume(UserNotFoundException.class,
                        ex -> buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request))
                .onErrorResume(LoanTypeNotFoundException.class,
                        ex -> buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request))
                .onErrorResume(Exception.class,
                        ex -> buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), request));

    }

    private Mono<ServerResponse> buildErrorResponse(HttpStatus status, String message, ServerRequest request) {
        log.error("Error occurred: {}", message);
        ApiError apiError = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(message)
                .path(request.path())
                .build();

        return ServerResponse
                .status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(apiError);
    }
}
