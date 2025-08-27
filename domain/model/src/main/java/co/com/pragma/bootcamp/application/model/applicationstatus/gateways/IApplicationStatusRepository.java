package co.com.pragma.bootcamp.application.model.applicationstatus.gateways;

import co.com.pragma.bootcamp.application.model.applicationstatus.ApplicationStatus;
import reactor.core.publisher.Mono;

public interface IApplicationStatusRepository {
    Mono<ApplicationStatus> findByName(String name);
}
