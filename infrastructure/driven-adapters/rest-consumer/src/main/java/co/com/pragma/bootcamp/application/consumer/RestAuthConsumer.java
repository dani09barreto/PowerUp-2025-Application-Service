package co.com.pragma.bootcamp.application.consumer;

import co.com.pragma.bootcamp.application.consumer.dto.UserRegistrationResponse;
import co.com.pragma.bootcamp.application.consumer.error.ApiError;
import co.com.pragma.bootcamp.application.consumer.mapper.UserDtoMapper;
import co.com.pragma.bootcamp.application.model.user.User;
import co.com.pragma.bootcamp.application.model.user.gateways.IUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Log4j2
@Service
@RequiredArgsConstructor
public class RestAuthConsumer implements IUserRepository {

    private final WebClient client;

    @Override
    public Mono<User> findByNumberIdentification(String numberIdentification) {
        return client
                .get()
                .uri("/api/v1/usuarios/document/{identification}", numberIdentification)
                .retrieve()
                .onStatus(
                        HttpStatusCode::isError,
                        response -> response.bodyToMono(String.class)
                                .flatMap(errorMessage -> {
                                    log.error("Error response from auth service: {}", errorMessage);
                                    return Mono.empty();
                                })
                )
                .bodyToMono(UserRegistrationResponse.class)
                .map(UserDtoMapper::toUser);
    }
}
