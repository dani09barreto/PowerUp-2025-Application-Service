package co.com.pragma.bootcamp.application.r2dbc.entity;

import co.com.pragma.bootcamp.application.model.applicationstatus.ApplicationStatus;
import co.com.pragma.bootcamp.application.model.loanapplication.LoanApplication;
import co.com.pragma.bootcamp.application.model.loantype.LoanType;
import co.com.pragma.bootcamp.application.model.user.User;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.mapping.Column;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("loan_applications")
public class LoanApplicationEntity {

    @Id
    private Long id;

    @Column("user_id")
    private Long userId;

    private BigDecimal amount;

    @Column("term_months")
    private Integer termMonths;

    @Column("loan_type_id")
    private Long loanTypeId;

    @Column("application_status_id")
    private Long applicationStatusId;

    public static LoanApplicationEntity fromDomain(LoanApplication loanApplication) {
        return LoanApplicationEntity.builder()
                .id(loanApplication.getId())
                .userId(loanApplication.getUser() != null ? loanApplication.getUser().getId() : null)
                .amount(loanApplication.getAmount())
                .termMonths(loanApplication.getTermMonths())
                .loanTypeId(loanApplication.getLoanType() != null ? loanApplication.getLoanType().getId() : null)
                .applicationStatusId(loanApplication.getApplicationStatus() != null ? loanApplication.getApplicationStatus().getId() : null)
                .build();
    }

    public LoanApplication toDomain() {
        return LoanApplication.builder()
                .id(this.id)
                .user(this.userId != null ? User.builder().id(this.userId).build() : null)
                .amount(this.amount)
                .termMonths(this.termMonths)
                .loanType(this.loanTypeId != null ? LoanType.builder().id(this.loanTypeId).build() : null)
                .applicationStatus(this.applicationStatusId != null ? ApplicationStatus.builder().id(this.applicationStatusId).build() : null)
                .build();
    }
}
