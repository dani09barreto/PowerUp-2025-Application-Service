package co.com.pragma.bootcamp.application.consumer;


import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;
import java.io.IOException;


class RestConsumerTest {

    private static RestAuthConsumer restConsumer;

    private static MockWebServer mockBackEnd;


    @BeforeAll
    static void setUp() throws IOException {
        mockBackEnd = new MockWebServer();
        mockBackEnd.start();
        var webClient = WebClient.builder().baseUrl(mockBackEnd.url("/").toString()).build();
        restConsumer = new RestAuthConsumer(webClient);
    }

    @AfterAll
    static void tearDown() throws IOException {

        mockBackEnd.shutdown();
    }

    @Test
    @DisplayName("Validate the function findByNumberIdentification.")
    void shouldReturnUserWhenResponseIsSuccessful() {
        String numberIdentification = "12345";
        String responseBody = """
            {
              "id": 1,
              "firstName": "John",
              "lastName": "Doe",
              "birthDate": "2025-08-27",
              "address": "Call3 1",
              "phone": "322123",
              "identificationNumber": "12345",
              "email": "dan@gmail.com",
              "baseSalary": 1000
            }
    """;

        mockBackEnd.enqueue(new MockResponse()
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setResponseCode(HttpStatus.OK.value())
                .setBody(responseBody));

        StepVerifier.create(restConsumer.findByNumberIdentification(numberIdentification))
                .expectNextMatches(user -> user.getFirstName().equals("John") &&
                        user.getLastName().equals("Doe") &&
                        user.getIdentificationNumber().equals("12345"))
                .verifyComplete();
    }
}