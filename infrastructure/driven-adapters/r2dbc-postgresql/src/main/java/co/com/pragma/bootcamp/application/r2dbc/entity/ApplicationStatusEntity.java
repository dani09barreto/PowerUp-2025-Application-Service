package co.com.pragma.bootcamp.application.r2dbc.entity;

import co.com.pragma.bootcamp.application.model.applicationstatus.ApplicationStatus;
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
@Table("application_status")
public class ApplicationStatusEntity {
    @Id
    private Long id;
    private String name;
    private String description;

    public static ApplicationStatusEntity fromDomain(ApplicationStatus status) {
        return ApplicationStatusEntity.builder()
                .id(status.getId())
                .name(status.getName())
                .description(status.getDescription())
                .build();
    }

    public ApplicationStatus toDomain() {
        return ApplicationStatus.builder()
                .id(this.id)
                .name(this.name)
                .description(this.description)
                .build();
    }
}
