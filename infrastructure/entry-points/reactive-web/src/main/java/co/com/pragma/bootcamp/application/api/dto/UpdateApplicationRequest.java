package co.com.pragma.bootcamp.application.api.dto;

public record UpdateApplicationRequest(
        Long applicationId,
        String status
) {
}
