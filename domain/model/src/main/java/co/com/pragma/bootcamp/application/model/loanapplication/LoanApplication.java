package co.com.pragma.bootcamp.application.model.loanapplication;
import co.com.pragma.bootcamp.application.model.applicationstatus.ApplicationStatus;
import co.com.pragma.bootcamp.application.model.loantype.LoanType;
import co.com.pragma.bootcamp.application.model.user.User;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class LoanApplication {
    private Long id;
    private User user;
    private BigDecimal amount;
    private Integer termMonths;
    private ApplicationStatus applicationStatus;
    private LoanType loanType;
}
