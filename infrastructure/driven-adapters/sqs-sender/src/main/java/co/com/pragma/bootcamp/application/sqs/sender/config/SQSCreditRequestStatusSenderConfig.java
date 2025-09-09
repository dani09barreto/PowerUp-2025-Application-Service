package co.com.pragma.bootcamp.application.sqs.sender.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.metrics.MetricPublisher;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

@Configuration
@EnableConfigurationProperties(SQSCreditRequestStatusSenderProperties.class)
@ConditionalOnMissingBean(SqsAsyncClient.class)
public class SQSCreditRequestStatusSenderConfig {

    @Bean
    public SqsAsyncClient configSqs(SQSCreditRequestStatusSenderProperties properties,
                                    MetricPublisher publisher) {

        return SqsAsyncClient.builder()
                .region(Region.of(properties.region()))
                .overrideConfiguration(o -> o.addMetricPublisher(publisher))
                .credentialsProvider(buildStaticProvider(properties))
                .build();
    }

    private AwsCredentialsProvider buildStaticProvider(SQSCreditRequestStatusSenderProperties properties) {
        return StaticCredentialsProvider.create(
                AwsBasicCredentials.create(
                        properties.accessKey(),
                        properties.secretAccessKey()
                )
        );
    }
}