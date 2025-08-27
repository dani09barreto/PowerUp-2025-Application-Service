package co.com.pragma.bootcamp.application.consumer.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UserRegistrationResponse(
    Long id,
    String firstName,
    String lastName,
    LocalDate birthDate,
    String address,
    String phone,
    String identificationNumber,
    String email,
    BigDecimal baseSalary
) {
}
