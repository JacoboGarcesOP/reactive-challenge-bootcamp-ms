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
  private WebTestClient getAllBootcampsWebTestClient;

  @BeforeEach
  void setUp() {
    handler = Mockito.mock(Handler.class);
    RouterRest routerRest = new RouterRest();
    
    // Setup for create bootcamp route
    RouterFunction<ServerResponse> createBootcampRoutes = routerRest.createBootcampRoute(handler);
    webTestClient = WebTestClient.bindToRouterFunction(createBootcampRoutes).build();
    
    // Setup for get all bootcamps route
    RouterFunction<ServerResponse> getAllBootcampsRoutes = routerRest.getAllBootcampsRoute(handler);
    getAllBootcampsWebTestClient = WebTestClient.bindToRouterFunction(getAllBootcampsRoutes).build();
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

  // Tests for getAllBootcamps endpoint

  @Test
  void shouldRouteGetAllBootcampsSuccess() {
    String expectedResponse = "{\n" +
      "  \"bootcamps\": [\n" +
      "    {\n" +
      "      \"bootcampId\": 1,\n" +
      "      \"name\": \"Java Bootcamp\",\n" +
      "      \"description\": \"Java training\",\n" +
      "      \"launchDate\": \"2030-01-10\",\n" +
      "      \"duration\": 60,\n" +
      "      \"capacities\": [\n" +
      "        {\n" +
      "          \"capacityId\": 1,\n" +
      "          \"name\": \"Payments Squad\",\n" +
      "          \"description\": \"Handles all payment features\",\n" +
      "          \"technologies\": [\n" +
      "            { \"technologyId\": 10, \"name\": \"Java\", \"description\": \"Java 21 LTS\" }\n" +
      "          ]\n" +
      "        }\n" +
      "      ]\n" +
      "    }\n" +
      "  ],\n" +
      "  \"filter\": {\n" +
      "    \"page\": 0,\n" +
      "    \"size\": 10,\n" +
      "    \"sortBy\": \"name\",\n" +
      "    \"order\": \"asc\"\n" +
      "  }\n" +
      "}";

    when(handler.getAllBootcamps(any())).thenReturn(ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(expectedResponse));

    getAllBootcampsWebTestClient.get()
      .uri("/v1/api/bootcamp")
      .exchange()
      .expectStatus().isOk()
      .expectHeader().contentType(MediaType.APPLICATION_JSON)
      .expectBody()
      .jsonPath("$.bootcamps").isArray()
      .jsonPath("$.bootcamps[0].bootcampId").isEqualTo(1)
      .jsonPath("$.bootcamps[0].name").isEqualTo("Java Bootcamp")
      .jsonPath("$.filter.page").isEqualTo(0)
      .jsonPath("$.filter.size").isEqualTo(10)
      .jsonPath("$.filter.sortBy").isEqualTo("name")
      .jsonPath("$.filter.order").isEqualTo("asc");
  }

  @Test
  void shouldRouteGetAllBootcampsWithQueryParameters() {
    String expectedResponse = "{\n" +
      "  \"bootcamps\": [],\n" +
      "  \"filter\": {\n" +
      "    \"page\": 1,\n" +
      "    \"size\": 5,\n" +
      "    \"sortBy\": \"capacities\",\n" +
      "    \"order\": \"desc\"\n" +
      "  }\n" +
      "}";

    when(handler.getAllBootcamps(any())).thenReturn(ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(expectedResponse));

    getAllBootcampsWebTestClient.get()
      .uri("/v1/api/bootcamp?page=1&size=5&sortBy=capacities&order=desc")
      .exchange()
      .expectStatus().isOk()
      .expectHeader().contentType(MediaType.APPLICATION_JSON)
      .expectBody()
      .jsonPath("$.filter.page").isEqualTo(1)
      .jsonPath("$.filter.size").isEqualTo(5)
      .jsonPath("$.filter.sortBy").isEqualTo("capacities")
      .jsonPath("$.filter.order").isEqualTo("desc");
  }

  @Test
  void shouldRouteGetAllBootcampsWithDefaultParameters() {
    String expectedResponse = "{\n" +
      "  \"bootcamps\": [],\n" +
      "  \"filter\": {\n" +
      "    \"page\": 0,\n" +
      "    \"size\": 10,\n" +
      "    \"sortBy\": \"name\",\n" +
      "    \"order\": \"asc\"\n" +
      "  }\n" +
      "}";

    when(handler.getAllBootcamps(any())).thenReturn(ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(expectedResponse));

    getAllBootcampsWebTestClient.get()
      .uri("/v1/api/bootcamp")
      .exchange()
      .expectStatus().isOk()
      .expectHeader().contentType(MediaType.APPLICATION_JSON)
      .expectBody()
      .jsonPath("$.filter.page").isEqualTo(0)
      .jsonPath("$.filter.size").isEqualTo(10)
      .jsonPath("$.filter.sortBy").isEqualTo("name")
      .jsonPath("$.filter.order").isEqualTo("asc");
  }

  @Test
  void shouldRouteGetAllBootcampsBusinessError() {
    when(handler.getAllBootcamps(any())).thenReturn(ServerResponse.badRequest().contentType(MediaType.APPLICATION_JSON).bodyValue("{\"error\":\"BUSINESS_ERROR\",\"message\":\"No bootcamps found\"}"));

    getAllBootcampsWebTestClient.get()
      .uri("/v1/api/bootcamp")
      .exchange()
      .expectStatus().isBadRequest()
      .expectHeader().contentType(MediaType.APPLICATION_JSON)
      .expectBody()
      .jsonPath("$.error").isEqualTo("BUSINESS_ERROR")
      .jsonPath("$.message").isEqualTo("No bootcamps found");
  }

  @Test
  void shouldRouteGetAllBootcampsDomainError() {
    when(handler.getAllBootcamps(any())).thenReturn(ServerResponse.badRequest().contentType(MediaType.APPLICATION_JSON).bodyValue("{\"error\":\"DOMAIN_ERROR\",\"message\":\"Invalid sort field\"}"));

    getAllBootcampsWebTestClient.get()
      .uri("/v1/api/bootcamp?sortBy=invalid")
      .exchange()
      .expectStatus().isBadRequest()
      .expectHeader().contentType(MediaType.APPLICATION_JSON)
      .expectBody()
      .jsonPath("$.error").isEqualTo("DOMAIN_ERROR")
      .jsonPath("$.message").isEqualTo("Invalid sort field");
  }

  @Test
  void shouldRouteGetAllBootcampsInternalError() {
    when(handler.getAllBootcamps(any())).thenReturn(ServerResponse.status(500).contentType(MediaType.APPLICATION_JSON).bodyValue("{\"error\":\"INTERNAL_ERROR\",\"message\":\"An unexpected error occurred\"}"));

    getAllBootcampsWebTestClient.get()
      .uri("/v1/api/bootcamp")
      .exchange()
      .expectStatus().is5xxServerError()
      .expectHeader().contentType(MediaType.APPLICATION_JSON)
      .expectBody()
      .jsonPath("$.error").isEqualTo("INTERNAL_ERROR")
      .jsonPath("$.message").isEqualTo("An unexpected error occurred");
  }

  @Test
  void shouldRouteGetAllBootcampsWithPartialQueryParameters() {
    String expectedResponse = "{\n" +
      "  \"bootcamps\": [],\n" +
      "  \"filter\": {\n" +
      "    \"page\": 2,\n" +
      "    \"size\": 10,\n" +
      "    \"sortBy\": \"name\",\n" +
      "    \"order\": \"asc\"\n" +
      "  }\n" +
      "}";

    when(handler.getAllBootcamps(any())).thenReturn(ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(expectedResponse));

    getAllBootcampsWebTestClient.get()
      .uri("/v1/api/bootcamp?page=2")
      .exchange()
      .expectStatus().isOk()
      .expectHeader().contentType(MediaType.APPLICATION_JSON)
      .expectBody()
      .jsonPath("$.filter.page").isEqualTo(2)
      .jsonPath("$.filter.size").isEqualTo(10)
      .jsonPath("$.filter.sortBy").isEqualTo("name")
      .jsonPath("$.filter.order").isEqualTo("asc");
  }
}


