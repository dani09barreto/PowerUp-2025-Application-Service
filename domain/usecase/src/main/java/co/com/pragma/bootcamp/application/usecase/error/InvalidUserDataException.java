package co.com.pragma.bootcamp.application.usecase.error;

public class InvalidUserDataException extends RuntimeException {

    public InvalidUserDataException(String message) {
        super(message);
    }
}