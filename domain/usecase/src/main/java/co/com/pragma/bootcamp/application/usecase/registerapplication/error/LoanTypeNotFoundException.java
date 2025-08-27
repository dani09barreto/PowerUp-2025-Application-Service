package co.com.pragma.bootcamp.application.usecase.registerapplication.error;

public class LoanTypeNotFoundException extends RuntimeException {
    public LoanTypeNotFoundException(String message) {
        super(message);
    }
}
