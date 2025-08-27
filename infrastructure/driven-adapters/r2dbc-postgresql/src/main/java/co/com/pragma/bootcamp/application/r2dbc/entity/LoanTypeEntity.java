package co.com.pragma.bootcamp.application.r2dbc.entity;

import co.com.pragma.bootcamp.application.model.loantype.LoanType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("loan_type")
public class LoanTypeEntity {
    @Id
    private Long id;
    private String name;


    public static LoanTypeEntity fromDomain(LoanType loanType) {
        return LoanTypeEntity.builder()
                .id(loanType.getId())
                .name(loanType.getName())
                .build();
    }

    public LoanType toDomain() {
        return LoanType.builder()
                .id(this.id)
                .name(this.name)
                .build();
    }
}
