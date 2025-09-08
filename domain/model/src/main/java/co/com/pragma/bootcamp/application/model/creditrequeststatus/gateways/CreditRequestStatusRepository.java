package co.com.pragma.bootcamp.application.model.creditrequeststatus.gateways;

import co.com.pragma.bootcamp.application.model.creditrequeststatus.CreditRequestStatus;
import reactor.core.publisher.Mono;

public interface CreditRequestStatusRepository {
    Mono<String> sendCreditRequestStatus(CreditRequestStatus creditRequestStatus);
}
