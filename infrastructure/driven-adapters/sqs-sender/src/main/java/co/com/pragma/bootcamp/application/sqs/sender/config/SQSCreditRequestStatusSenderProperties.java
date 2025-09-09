package co.com.pragma.bootcamp.application.sqs.sender.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "adapter.sqs")
public record SQSCreditRequestStatusSenderProperties(
     String region,
     String queueUrl,
     String accessKey,
     String secretAccessKey){
}
