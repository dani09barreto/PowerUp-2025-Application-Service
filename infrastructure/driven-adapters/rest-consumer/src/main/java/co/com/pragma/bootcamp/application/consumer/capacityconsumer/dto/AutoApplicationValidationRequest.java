package co.com.pragma.bootcamp.application.consumer.capacityconsumer.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class AutoApplicationValidationRequest {
    private String email;
    private String firstName;
    private BigDecimal baseSalary;
    private List<ApprovedApplication> approvedApplications;
    private BigDecimal amount;
    private Integer termMonths;
    private BigDecimal annualRate;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder(toBuilder = true)
    public static class ApprovedApplication{
        private BigDecimal amount;
        private Integer termMonths;
        private BigDecimal annualRate;
    }
}
