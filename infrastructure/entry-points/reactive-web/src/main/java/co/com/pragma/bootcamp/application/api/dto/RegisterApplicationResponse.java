package co.com.pragma.bootcamp.application.api.dto;

import java.math.BigDecimal;

public record RegisterApplicationResponse(
        Long id,
        Long userId,
        Long loanTypeId,
        Long applicationStatusId,
        BigDecimal annualRate,
        Integer termMonths,
        BigDecimal amount
) {
}
