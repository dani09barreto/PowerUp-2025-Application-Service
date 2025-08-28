package co.com.pragma.bootcamp.application.api;

import co.com.pragma.bootcamp.application.api.dto.RegisterApplicationRequest;
import co.com.pragma.bootcamp.application.api.dto.RegisterApplicationResponse;
import co.com.pragma.bootcamp.application.api.error.ApiError;
import co.com.pragma.bootcamp.application.api.mapper.ApplicationDtoMapper;
import co.com.pragma.bootcamp.application.usecase.registerapplication.IRegisterApplicationUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Log4j2
@Component
@RequiredArgsConstructor
public class ApplicationHandler {

    private final IRegisterApplicationUseCase registerApplicationUseCase;

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
    public Mono<ServerResponse> registerApplication(ServerRequest serverRequest) {
        log.info("Received request to register application");
        return serverRequest.bodyToMono(RegisterApplicationRequest.class)
                .map(ApplicationDtoMapper::toLoanApplication)
                .flatMap(registerApplicationUseCase::registerApplication)
                .map(ApplicationDtoMapper::toRegisterApplicationResponse)
                .flatMap(savedUser -> ServerResponse.ok().bodyValue(savedUser));
    }
}
