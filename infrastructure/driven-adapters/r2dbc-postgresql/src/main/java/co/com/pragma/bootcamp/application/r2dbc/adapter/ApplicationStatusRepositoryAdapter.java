package co.com.pragma.bootcamp.application.r2dbc.adapter;

import co.com.pragma.bootcamp.application.model.applicationstatus.ApplicationStatus;
import co.com.pragma.bootcamp.application.model.applicationstatus.gateways.IApplicationStatusRepository;
import co.com.pragma.bootcamp.application.r2dbc.entity.ApplicationStatusEntity;
import co.com.pragma.bootcamp.application.r2dbc.repository.ApplicationStatusReactiveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class ApplicationStatusRepositoryAdapter implements IApplicationStatusRepository {

    private final ApplicationStatusReactiveRepository repository;

    @Override
    public Mono<ApplicationStatus> findByName(String name) {
        return repository.findByName(name)
                .map(ApplicationStatusEntity::toDomain)
                .switchIfEmpty(Mono.empty());
    }
}
