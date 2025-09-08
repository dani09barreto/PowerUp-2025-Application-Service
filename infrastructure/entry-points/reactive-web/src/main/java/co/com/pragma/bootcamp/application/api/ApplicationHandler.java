package co.com.pragma.bootcamp.application.api;

import co.com.pragma.bootcamp.application.api.dto.PageableResponse;
import co.com.pragma.bootcamp.application.api.dto.RegisterApplicationRequest;
import co.com.pragma.bootcamp.application.api.dto.RegisterApplicationResponse;
import co.com.pragma.bootcamp.application.api.dto.UpdateApplicationRequest;
import co.com.pragma.bootcamp.application.api.error.ApiError;
import co.com.pragma.bootcamp.application.api.mapper.ApplicationDtoMapper;
import co.com.pragma.bootcamp.application.usecase.listapplications.IListApplicationsUseCase;
import co.com.pragma.bootcamp.application.usecase.listapplications.dto.ApplicationListDto;
import co.com.pragma.bootcamp.application.usecase.registerapplication.IRegisterApplicationUseCase;
import co.com.pragma.bootcamp.application.usecase.updateapplication.IUpdateApplicationUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.security.Principal;
import java.util.List;

@Log4j2
@Component
@RequiredArgsConstructor
public class ApplicationHandler {

    private final IRegisterApplicationUseCase registerApplicationUseCase;
    private final IListApplicationsUseCase listApplicationsUseCase;
    private final IUpdateApplicationUseCase updateApplicationUseCase;

    @Operation(
            summary = "Registrar una nueva solicitud de préstamo",
            description = "Este endpoint permite registrar una solicitud de préstamo en el sistema.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Datos del usuario a registrar",
                    content = @Content(schema = @Schema(implementation = RegisterApplicationRequest.class))
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Usuario registrado exitosamente",
                            content = @Content(schema = @Schema(implementation = RegisterApplicationResponse.class))
                    ),
                    @ApiResponse(responseCode = "400", description = "Error de validación en los datos",
                            content = @Content(schema = @Schema(implementation = ApiError.class))
                    ),
                    @ApiResponse(responseCode = "409", description = "Correo ya registrado",
                            content = @Content(schema = @Schema(implementation = ApiError.class))
                    )
            }
    )
    @PreAuthorize("hasAnyRole('CLIENT')")
    public Mono<ServerResponse> registerApplication(ServerRequest serverRequest) {
        log.info("Received request to register application");

        return serverRequest.bodyToMono(RegisterApplicationRequest.class)
                .map(ApplicationDtoMapper::toLoanApplication)
                .flatMap(loanApplication -> registerApplicationUseCase.registerApplication(loanApplication, getCurrentUserName()))
                .map(ApplicationDtoMapper::toRegisterApplicationResponse)
                .flatMap(savedUser -> ServerResponse.ok().bodyValue(savedUser));
    }

    @Operation(
            summary = "Obtener solicitudes de préstamos por filtros",
            description = "Este endpoint permite obtener las solicitudes de préstamo filtradas por estado, con paginación.",
            parameters = {
                    @io.swagger.v3.oas.annotations.Parameter(
                            name = "status",
                            description = "Status de las solicitudes a filtrar (puede repetirse para múltiples estados)",
                            required = true,
                            in = ParameterIn.QUERY,
                            example = "PENDING"
                    ),
                    @io.swagger.v3.oas.annotations.Parameter(
                            name = "page",
                            description = "Number of the page to retrieve",
                            required = true,
                            in = ParameterIn.QUERY,
                            example = "0"
                    ),
                    @io.swagger.v3.oas.annotations.Parameter(
                            name = "size",
                            description = "Size of the page to retrieve",
                            required = true,
                            in = ParameterIn.QUERY,
                            example = "10"
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Lista de solicitudes de préstamo obtenida exitosamente",
                            content = @Content(schema = @Schema(implementation = PageableResponse.class))
                    )
            }
    )
    @PreAuthorize("hasAnyRole('ADVISOR')")
    public Mono<ServerResponse> getApplicationsByFilters(ServerRequest serverRequest){
        log.info("Received request to get applications by filters");
        List<String> status = serverRequest.queryParam("status").map(List::of).orElse(List.of());
        Integer page = serverRequest.queryParam("page").map(Integer::valueOf).orElse(0);
        Integer size = serverRequest.queryParam("size").map(Integer::valueOf).orElse(10);

        log.info("Filters - status: {}, page: {}, size: {}", status, page, size);

        return listApplicationsUseCase.listApplicationsByApplicationStatus(status, page, size)
                .log()
                .collectList()
                .map(applications -> PageableResponse.<ApplicationListDto>builder()
                        .data(applications)
                        .currentPage(page + 1)
                        .totalItems(applications.size())
                        .totalPages((int) Math.ceil((double) applications.size() / size))
                        .build())
                .flatMap(response -> ServerResponse.ok().bodyValue(response));
    }

    public Mono<String> getCurrentUserName() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(Principal::getName);
    }

    @Operation(
            summary = "Actualizar una solicitud de préstamo",
            description = "Este endpoint permite actualizar una solicitud de préstamo en el sistema.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Solicitud de préstamo a actualizar",
                    content = @Content(schema = @Schema(implementation = UpdateApplicationRequest.class))
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Solicitud de préstamo actualizada exitosamente",
                            content = @Content(schema = @Schema(implementation = RegisterApplicationResponse.class))
                    ),
                    @ApiResponse(responseCode = "400", description = "Error de validación en los datos",
                            content = @Content(schema = @Schema(implementation = ApiError.class))
                    ),
                    @ApiResponse(responseCode = "409", description = "Correo ya registrado",
                            content = @Content(schema = @Schema(implementation = ApiError.class))
                    )
            }
    )
    @PreAuthorize("hasAnyRole('ADVISOR')")
    public Mono<ServerResponse> updateApplication(ServerRequest serverRequest){
        return serverRequest.bodyToMono(UpdateApplicationRequest.class)
                .flatMap(updateApplicationRequest -> updateApplicationUseCase.updateApplication(updateApplicationRequest.applicationId(), updateApplicationRequest.status()))
                .map(ApplicationDtoMapper::toRegisterApplicationResponse)
                .flatMap(updatedApplication -> ServerResponse.ok().bodyValue(updatedApplication));
    }
}
