package co.com.pragma.bootcamp.application.model.creditrequeststatus;
import lombok.*;

import java.math.BigDecimal;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class CreditRequestStatus {
    private String clientEmail;
    private Integer termMonths;
    private BigDecimal amount;
    private BigDecimal annualRate;
    private String applicationStatus;
}
