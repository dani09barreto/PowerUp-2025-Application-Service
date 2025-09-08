package co.com.pragma.bootcamp.application.usecase.error;

public class LoanApplicationNotFound extends RuntimeException {
    public LoanApplicationNotFound(String message) {
        super(message);
    }
}
