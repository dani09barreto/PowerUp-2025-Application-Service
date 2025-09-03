package co.com.pragma.bootcamp.application.usecase.listapplications;

import co.com.pragma.bootcamp.application.usecase.listapplications.dto.ApplicationListDto;
import reactor.core.publisher.Flux;

import java.util.List;

public interface IListApplicationsUseCase {
    Flux<ApplicationListDto> listApplicationsByApplicationStatus(List<String> applicationStatus, Integer page, Integer size);
}
