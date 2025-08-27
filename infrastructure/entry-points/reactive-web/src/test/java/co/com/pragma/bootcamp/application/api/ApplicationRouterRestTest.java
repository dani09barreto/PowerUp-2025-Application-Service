package co.com.pragma.bootcamp.application.api;

import co.com.pragma.bootcamp.application.api.error.ErrorFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.mock;

class ApplicationRouterRestTest {

    @Mock
    private ApplicationHandler handler;

    @Mock
    private ErrorFilter errorFilter;

    private ApplicationRouterRest applicationRouterRest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        applicationRouterRest = new ApplicationRouterRest();
    }

    @Test
    @DisplayName("Should return a RouterFunction instance when valid handler and errorFilter are provided")
    void shouldReturnRouterFunctionInstance() {
        RouterFunction<ServerResponse> routerFunction = applicationRouterRest.routerFunction(handler, errorFilter);

        assertThat(routerFunction).isNotNull();
    }

    @Test
    @DisplayName("Should throw NullPointerException when handler is null")
    void shouldThrowExceptionWhenHandlerIsNull() {
        ErrorFilter mockErrorFilter = mock(ErrorFilter.class);

        assertThatThrownBy(() -> applicationRouterRest.routerFunction(null, mockErrorFilter))
                .isInstanceOf(NullPointerException.class);
    }

}