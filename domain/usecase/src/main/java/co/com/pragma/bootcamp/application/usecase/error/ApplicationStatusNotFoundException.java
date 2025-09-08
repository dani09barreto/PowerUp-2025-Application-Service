package co.com.pragma.bootcamp.application.usecase.error;

public class ApplicationStatusNotFoundException extends RuntimeException {
    public ApplicationStatusNotFoundException(String message) {
        super(message);
    }
}
