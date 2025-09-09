package co.com.pragma.bootcamp.application.sqs.sender;

import co.com.pragma.bootcamp.application.model.creditrequeststatus.CreditRequestStatus;
import co.com.pragma.bootcamp.application.model.creditrequeststatus.gateways.CreditRequestStatusRepository;
import co.com.pragma.bootcamp.application.sqs.sender.config.SQSCreditRequestStatusSenderProperties;
import co.com.pragma.bootcamp.application.sqs.sender.dto.SQSMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

import java.math.BigDecimal;

@Service
@Log4j2
@RequiredArgsConstructor
public class SQSCreditRequestStatusSender implements CreditRequestStatusRepository {
    private final SQSCreditRequestStatusSenderProperties properties;
    private final SqsAsyncClient client;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Mono<String> sendCreditRequestStatus(CreditRequestStatus creditRequestStatus) {
        return Mono.fromCallable(() -> buildRequest(objectMapper.writeValueAsString(creditRequestStatusMessage(creditRequestStatus))))
                .flatMap(request -> Mono.fromFuture(client.sendMessage(request)))
                .doOnNext(response -> log.debug("Message sent {}", response.messageId()))
                .map(SendMessageResponse::messageId)
                .doOnError(e -> log.error("Error sending message to SQS: {}", e.getMessage(), e));
    }


    private SendMessageRequest buildRequest(String message) {
        return SendMessageRequest.builder()
                .queueUrl(properties.queueUrl())
                .messageBody(message)
                .build();
    }

    private SQSMessage creditRequestStatusMessage(CreditRequestStatus creditRequestStatus){
        return SQSMessage.builder()
                .subject(String.format("Solicitud de credito %s", creditRequestStatus.getApplicationStatus()))
                .email(creditRequestStatus.getClientEmail())
                .message(String.format(
                        "Hola,\n\n" +
                                "Tu Crédito ha sido %s.\n\n" +
                                "Details:\n" +
                                "Valor: %.2f\n" +
                                "Plazo: %d months\n" +
                                "Tasa: %.2f%%\n",
                        creditRequestStatus.getApplicationStatus(),
                        creditRequestStatus.getAmount(),
                        creditRequestStatus.getTermMonths(),
                        creditRequestStatus.getAnnualRate().multiply(BigDecimal.valueOf(100))
                ))
                .build();
    }
}
