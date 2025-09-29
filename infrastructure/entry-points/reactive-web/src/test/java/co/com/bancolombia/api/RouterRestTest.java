package co.com.bancolombia.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

class RouterRestTest {

  private Handler handler;
  private WebTestClient webTestClient;

  @BeforeEach
  void setUp() {
    handler = Mockito.mock(Handler.class);
    RouterRest routerRest = new RouterRest();
    RouterFunction<ServerResponse> routes = routerRest.createBootcampRoute(handler);
    webTestClient = WebTestClient.bindToRouterFunction(routes).build();
  }

  @Test
  void shouldRouteCreateBootcampSuccess() {
    when(handler.createBootcamp(any())).thenReturn(ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue("{\"ok\":true}"));

    String body = "{\n" +
      "  \"name\": \"Bootcamp Java\",\n" +
      "  \"description\": \"Formación intensiva en Java\",\n" +
      "  \"launchDate\": \"2030-01-10\",\n" +
      "  \"duration\": 60,\n" +
      "  \"capacityIds\": [1,2,3]\n" +
      "}";

    webTestClient.post()
      .uri("/v1/api/bootcamp")
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(body)
      .exchange()
      .expectStatus().isOk()
      .expectHeader().contentType(MediaType.APPLICATION_JSON)
      .expectBody().json("{\"ok\":true}");
  }

  @Test
  void shouldRouteCreateBootcampBusinessError() {
    when(handler.createBootcamp(any())).thenReturn(ServerResponse.badRequest().contentType(MediaType.APPLICATION_JSON).bodyValue("{\"error\":\"BUSINESS_ERROR\",\"message\":\"msg\"}"));

    webTestClient.post()
      .uri("/v1/api/bootcamp")
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue("{}")
      .exchange()
      .expectStatus().isBadRequest()
      .expectHeader().contentType(MediaType.APPLICATION_JSON)
      .expectBody().jsonPath("$.error").isEqualTo("BUSINESS_ERROR");
  }

  @Test
  void shouldRouteCreateBootcampInternalError() {
    when(handler.createBootcamp(any())).thenReturn(ServerResponse.status(500).contentType(MediaType.APPLICATION_JSON).bodyValue("{\"error\":\"INTERNAL_ERROR\"}"));

    webTestClient.post()
      .uri("/v1/api/bootcamp")
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue("{}")
      .exchange()
      .expectStatus().is5xxServerError()
      .expectHeader().contentType(MediaType.APPLICATION_JSON)
      .expectBody().jsonPath("$.error").isEqualTo("INTERNAL_ERROR");
  }
}


