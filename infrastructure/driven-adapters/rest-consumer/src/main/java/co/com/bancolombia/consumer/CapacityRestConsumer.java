package co.com.bancolombia.consumer;

import co.com.bancolombia.model.bootcamp.Capacity;
import co.com.bancolombia.model.bootcamp.CapacityBootcamp;
import co.com.bancolombia.model.bootcamp.Technology;
import co.com.bancolombia.model.bootcamp.gateway.CapacityGateway;
import co.com.bancolombia.model.bootcamp.values.Id;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CapacityRestConsumer implements CapacityGateway {
  private final WebClient client;

  @CircuitBreaker(name = "associateCapacity")
  @Override
  public Mono<Capacity> associateCapacity(CapacityBootcamp capacityBootcamp) {
    CapacityAssociateRequest request = CapacityAssociateRequest.builder()
      .bootcampId(capacityBootcamp.getBootcampId().getValue())
      .capacityId(capacityBootcamp.getCapacityId().getValue())
      .build();

    return client
      .post()
      .uri("/associate")
      .body(Mono.just(request), CapacityAssociateRequest.class)
      .retrieve()
      .onStatus(HttpStatusCode::is4xxClientError, this::map4xx)
      .onStatus(HttpStatusCode::is5xxServerError, this::map5xx)
      .bodyToMono(CapacityResponse.class)
      .map(resp -> new Capacity(
        resp.getCapacityId(),
        resp.getName(),
        resp.getDescription()));
  }

  @CircuitBreaker(name = "findByBootcampId")
  @Override
  public Flux<Capacity> findByBootcampId(Long bootcampId) {
    return client
      .get()
      .uri("/bootcamp/" + bootcampId)
      .retrieve()
      .onStatus(HttpStatusCode::is4xxClientError, this::map4xx)
      .onStatus(HttpStatusCode::is5xxServerError, this::map5xx)
      .bodyToFlux(CapacityResponse.class)
      .map(resp -> new Capacity(
        resp.getCapacityId(),
        resp.getName(),
        resp.getDescription(),
        resp.getTechnologies().stream().map(t-> new Technology(t.getTechnologyId(), t.getName(), t.getDescription())).toList()));
  }

  @CircuitBreaker(name = "findAllCapacities")
  @Override
  public Flux<Id> findAllCapacities() {
    return client
      .get()
      .uri("/ids")
      .retrieve()
      .onStatus(HttpStatusCode::is4xxClientError, this::map4xx)
      .onStatus(HttpStatusCode::is5xxServerError, this::map5xx)
      .bodyToFlux(Long.class)
      .map(Id::new);
  }

  private Mono<? extends Throwable> map4xx(ClientResponse response) {
    return response.bodyToMono(String.class)
      .map(msg -> new RuntimeException(msg != null && !msg.isBlank() ? msg : "Client error"));
  }

  private Mono<? extends Throwable> map5xx(ClientResponse response) {
    return response.bodyToMono(String.class)
      .map(msg -> new RuntimeException("External service error: " + msg));
  }
}


