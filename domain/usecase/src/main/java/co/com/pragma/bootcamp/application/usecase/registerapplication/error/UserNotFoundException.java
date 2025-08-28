package co.com.pragma.bootcamp.application.usecase.registerapplication.error;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
