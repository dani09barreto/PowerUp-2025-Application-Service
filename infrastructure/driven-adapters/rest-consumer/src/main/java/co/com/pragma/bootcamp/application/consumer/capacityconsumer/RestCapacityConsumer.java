package co.com.pragma.bootcamp.application.consumer.capacityconsumer;

import co.com.pragma.bootcamp.application.consumer.capacityconsumer.dto.AutoApplicationValidationRequest;
import co.com.pragma.bootcamp.application.consumer.capacityconsumer.dto.AutoApplicationValidationResponse;
import co.com.pragma.bootcamp.application.model.loanapplication.LoanApplication;
import co.com.pragma.bootcamp.application.model.loanapplication.gateways.ILoanApplicationCapacityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Log4j2
@Service
public class RestCapacityConsumer implements ILoanApplicationCapacityRepository {

    private final WebClient client;

    public RestCapacityConsumer(@Qualifier("capacityWebClient") WebClient client) {
        this.client = client;
    }

    @Override
    public Mono<String> sendAutoLoanApplicationCapacity(LoanApplication loanApplication, List<LoanApplication> approvedApplications) {

        AutoApplicationValidationRequest autoApplicationValidationRequest = AutoApplicationValidationRequest.builder()
                .email(loanApplication.getUser().getEmail())
                .firstName(loanApplication.getUser().getFirstName())
                .baseSalary(loanApplication.getUser().getBaseSalary())
                .amount(loanApplication.getAmount())
                .termMonths(loanApplication.getTermMonths())
                .annualRate(loanApplication.getAnnualRate())
                .approvedApplications(
                        approvedApplications.stream().map(app -> AutoApplicationValidationRequest.ApprovedApplication.builder()
                                .amount(app.getAmount())
                                .termMonths(app.getTermMonths())
                                .annualRate(app.getAnnualRate())
                                .build()).toList()
                )
                .build();

        return client
                .post()
                .uri("/api/v1/calcular-capacidad")
                .bodyValue(autoApplicationValidationRequest)
                .retrieve()
                .onStatus(
                        HttpStatusCode::isError,
                        response -> response.bodyToMono(String.class)
                                .flatMap(errorMessage -> {
                                    log.error("Error response from lambda service: {}", errorMessage);
                                    return Mono.empty();
                                })
                )
                .bodyToMono(AutoApplicationValidationResponse.class)
                .map(AutoApplicationValidationResponse::getResponse);
    }
}
