package co.com.pragma.bootcamp.application.api.dto;

import java.math.BigDecimal;

public record ApplicationResponse(
        BigDecimal amount,
        Integer termMonths,
        String email,
        String firstName,
        String loadTypeName,
        BigDecimal interestRate,
        String applicationStatusName,
        BigDecimal baseSalary
) {
}
