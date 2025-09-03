package co.com.pragma.bootcamp.application.usecase.listapplications.dto;

import lombok.*;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class ApplicationListDto {
    private BigDecimal amount;
    private Integer termMonths;
    private String email;
    private String firstName;
    private String loadTypeName;
    private BigDecimal interestRate;
    private String applicationStatusName;
    private BigDecimal baseSalary;
    private BigDecimal monthlyPayment;
}
