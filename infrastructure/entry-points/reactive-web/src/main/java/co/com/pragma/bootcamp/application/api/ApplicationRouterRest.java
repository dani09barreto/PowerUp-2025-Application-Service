package co.com.pragma.bootcamp.application.api;

import co.com.pragma.bootcamp.application.api.error.ErrorFilter;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class ApplicationRouterRest {

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/api/v1/solicitud",
                    beanClass = ApplicationHandler.class,
                    method = RequestMethod.POST,
                    beanMethod = "registerApplication"
            ),
            @RouterOperation(
                    path = "/api/v1/solicitud",
                    beanClass = ApplicationHandler.class,
                    method = RequestMethod.GET,
                    beanMethod = "getApplicationsByFilters"
            ),
            @RouterOperation(
                    path = "/api/v1/solicitud",
                    beanClass = ApplicationHandler.class,
                    method = RequestMethod.PUT,
                    beanMethod = "updateApplication"
            )
    })
    public RouterFunction<ServerResponse> routerFunction(ApplicationHandler handler, ErrorFilter errorFilter) {
        return route(POST("/api/v1/solicitud"), handler::registerApplication)
                .andRoute(GET("/api/v1/solicitud") , handler::getApplicationsByFilters)
                .andRoute(PUT("/api/v1/solicitud"), handler::updateApplication)
                .filter(errorFilter);
    }
}
