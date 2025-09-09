package co.com.pragma.bootcamp.application.sqs.sender.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class SQSMessage {
    private String subject;
    private String email;
    private String message;
}
