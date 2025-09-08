package co.com.pragma.bootcamp.application.sqs.sender;

import co.com.pragma.bootcamp.application.model.creditrequeststatus.CreditRequestStatus;
import co.com.pragma.bootcamp.application.model.creditrequeststatus.gateways.CreditRequestStatusRepository;
import co.com.pragma.bootcamp.application.sqs.sender.config.SQSCreditRequestStatusSenderProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

@Service
@Log4j2
@RequiredArgsConstructor
public class SQSCreditRequestStatusSender implements CreditRequestStatusRepository {
    private final SQSCreditRequestStatusSenderProperties properties;
    private final SqsAsyncClient client;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Mono<String> sendCreditRequestStatus(CreditRequestStatus creditRequestStatus) {
        return Mono.fromCallable(() -> buildRequest(objectMapper.writeValueAsString(creditRequestStatus)))
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
}
