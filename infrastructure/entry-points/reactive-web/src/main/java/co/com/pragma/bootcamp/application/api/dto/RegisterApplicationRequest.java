package co.com.pragma.bootcamp.application.api.dto;

import java.math.BigDecimal;

public record RegisterApplicationRequest(
        String documentNumber,
        Long loanTypeId,
        BigDecimal amount,
        Integer termMonths
) {
}
