package co.com.pragma.bootcamp.application.usecase.registerapplication.error;

public class InvalidUserDataException extends RuntimeException {

    public InvalidUserDataException(String message) {
        super(message);
    }
}