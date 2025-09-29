package co.com.bancolombia.consumer;

import co.com.bancolombia.model.bootcamp.CapacityBootcamp;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

import java.io.IOException;

class CapacityRestConsumerTest {

  private MockWebServer server;
  private CapacityRestConsumer consumer;

  @BeforeEach
  void setUp() throws IOException {
    server = new MockWebServer();
    server.start();
    WebClient client = WebClient.builder().baseUrl(server.url("/").toString()).build();
    consumer = new CapacityRestConsumer(client);
  }

  @AfterEach
  void tearDown() throws IOException {
    server.shutdown();
  }

  @Test
  void associateCapacity_ok() {
    String body = "{\"capacityId\":1,\"name\":\"Cap\",\"description\":\"Desc\"}";
    server.enqueue(new MockResponse().setResponseCode(200).setBody(body).addHeader("Content-Type", "application/json"));

    CapacityBootcamp input = new CapacityBootcamp(10L, 1L);
    StepVerifier.create(consumer.associateCapacity(input))
      .expectNextMatches(c -> c.getId().getValue().equals(1L) && c.getName().getValue().equals("Cap"))
      .verifyComplete();
  }

  @Test
  void associateCapacity_4xx_mapsError() {
    server.enqueue(new MockResponse().setResponseCode(400).setBody("Bad request"));
    StepVerifier.create(consumer.associateCapacity(new CapacityBootcamp(1L, 2L)))
      .expectError()
      .verify();
  }

  @Test
  void findByBootcampId_ok() {
    String body = "[{\"capacityId\":1,\"name\":\"A\",\"description\":\"d\"}]";
    server.enqueue(new MockResponse().setResponseCode(200).setBody(body).addHeader("Content-Type", "application/json"));
    StepVerifier.create(consumer.findByBootcampId(5L))
      .expectNextMatches(c -> c.getId().getValue().equals(1L) && c.getName().getValue().equals("A"))
      .verifyComplete();
  }

  @Test
  void findAllCapacities_5xx_mapsError() {
    server.enqueue(new MockResponse().setResponseCode(500).setBody("oops"));
    StepVerifier.create(consumer.findAllCapacities())
      .expectError()
      .verify();
  }
}


