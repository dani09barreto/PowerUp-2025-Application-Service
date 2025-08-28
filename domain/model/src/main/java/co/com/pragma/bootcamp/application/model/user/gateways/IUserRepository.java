package co.com.pragma.bootcamp.application.model.user.gateways;

import co.com.pragma.bootcamp.application.model.user.User;
import reactor.core.publisher.Mono;

public interface IUserRepository {
    Mono<User> findByNumberIdentification(String numberIdentification);
}
