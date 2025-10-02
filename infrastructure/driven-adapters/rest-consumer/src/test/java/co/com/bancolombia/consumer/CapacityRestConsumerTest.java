package co.com.bancolombia.consumer;

import co.com.bancolombia.model.bootcamp.Capacity;
import co.com.bancolombia.model.bootcamp.CapacityBootcamp;
import co.com.bancolombia.model.bootcamp.values.Id;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.util.List;

public class CapacityRestConsumerTest {

    private static MockWebServer server;

    @BeforeAll
    static void setup() throws IOException {
        server = new MockWebServer();
        server.start();
    }

    @AfterAll
    static void tearDown() throws IOException {
        server.shutdown();
    }

    private CapacityRestConsumer buildClient() {
        String baseUrl = server.url("/").toString();
        WebClient client = WebClient.builder().baseUrl(baseUrl).build();
        return new CapacityRestConsumer(client);
    }

    @Test
    void findByBootcampId_mapsTechnologyResponse() {
        String body = "[\n" +
                " {\"capacityId\":1,\"name\":\"cap\",\"description\":\"d\",\"technologies\":[{\"technologyId\":10,\"name\":\"Java\",\"description\":\"J\"}]}\n" +
                "]";
        server.enqueue(new MockResponse().setBody(body).setResponseCode(200).addHeader("Content-Type", "application/json"));

        CapacityRestConsumer consumer = buildClient();

        StepVerifier.create(consumer.findByBootcampId(1L))
                .expectNextMatches(c -> c instanceof Capacity && c.getTechnologies() != null && !c.getTechnologies().isEmpty())
                .verifyComplete();
    }

    @Test
    void associateCapacity_mapsResponse() {
        String body = "{\n" +
                " \"capacityId\":1,\"name\":\"cap\",\"description\":\"d\",\"technologies\":[]\n" +
                "}";
        server.enqueue(new MockResponse().setBody(body).setResponseCode(200).addHeader("Content-Type", "application/json"));

        CapacityRestConsumer consumer = buildClient();
        CapacityBootcamp cb = new CapacityBootcamp(1L, 1L);

        StepVerifier.create(consumer.associateCapacity(cb))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void findAllCapacities_mapsIds() {
        String body = "[1,2,3]";
        server.enqueue(new MockResponse().setBody(body).setResponseCode(200).addHeader("Content-Type", "application/json"));
        CapacityRestConsumer consumer = buildClient();
        StepVerifier.create(consumer.findAllCapacities())
                .expectNextMatches(id -> id instanceof Id && id.getValue().equals(1L))
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void deleteByBootcampId_mapsIds() {
        String body = "[10,20]";
        server.enqueue(new MockResponse().setBody(body).setResponseCode(200).addHeader("Content-Type", "application/json"));
        CapacityRestConsumer consumer = buildClient();
        StepVerifier.create(consumer.deleteByBootcampId(1L))
                .expectNextCount(2)
                .verifyComplete();
    }
}
