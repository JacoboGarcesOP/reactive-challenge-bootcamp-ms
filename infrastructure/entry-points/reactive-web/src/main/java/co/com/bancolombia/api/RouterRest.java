package co.com.bancolombia.api;

import co.com.bancolombia.api.request.CreateBootcampRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRest {
  private final String BASE_URL = "/v1/api";

  @Bean
  @RouterOperation(
    path = "/v1/api/bootcamp",
    produces = {MediaType.APPLICATION_JSON_VALUE},
    method = RequestMethod.POST,
    beanClass = Handler.class,
    beanMethod = "createBootcamp",
    operation = @Operation(
      operationId = "createBootcamp",
      summary = "Crear nuevo bootcamp",
      description = "Crea un bootcamp con sus capacidades asociadas. Valida los datos de entrada y maneja errores.",
      tags = {"Bootcamp Management"},
      requestBody = @RequestBody(
        required = true,
        description = "Datos del bootcamp a crear. Requiere nombre (máx 50), descripción (máx 90), fecha de lanzamiento futura y duración, además de lista de IDs de capacidades (1 a 4).",
        content = @Content(
          mediaType = MediaType.APPLICATION_JSON_VALUE,
          schema = @Schema(implementation = CreateBootcampRequest.class),
          examples = @ExampleObject(
            name = "Ejemplo de bootcamp",
            summary = "Request para crear bootcamp",
            value = "{\n" +
              "  \"name\": \"Bootcamp Java\",\n" +
              "  \"description\": \"Formación intensiva en Java\",\n" +
              "  \"launchDate\": \"2030-01-10\",\n" +
              "  \"duration\": 60,\n" +
              "  \"capacityIds\": [1, 2, 3]\n" +
              "}"
          )
        )
      ),
      responses = {
        @ApiResponse(responseCode = "200", description = "Bootcamp creado exitosamente",
          content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            examples = @ExampleObject(
              name = "Success Response",
              summary = "Bootcamp creado",
              value = "{\n" +
                "  \"bootcampId\": 100,\n" +
                "  \"name\": \"Bootcamp Java\",\n" +
                "  \"description\": \"Formación intensiva en Java\",\n" +
                "  \"launchDate\": \"2030-01-10\",\n" +
                "  \"duration\": 60,\n" +
                "  \"capacities\": [\n" +
                "    { \"capacityId\": 1, \"name\": \"Payments Squad\", \"description\": \"Handles all payment features\" }\n" +
                "  ]\n" +
                "}"
            )
          )
        ),
        @ApiResponse(responseCode = "400", description = "Error de validación, dominio o negocio",
          content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            examples = @ExampleObject(
              name = "Validation Error",
              summary = "Error de validación",
              value = "{\n" +
                "  \"error\": \"VALIDATION_ERROR\",\n" +
                "  \"message\": \"name: must not be blank\"\n" +
                "}"
            )
          )
        ),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor",
          content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            examples = @ExampleObject(
              name = "Internal Error",
              summary = "Error interno",
              value = "{\n" +
                "  \"error\": \"INTERNAL_ERROR\",\n" +
                "  \"message\": \"An unexpected error occurred\"\n" +
                "}"
            )
          )
        )
      }
    )
  )
  public RouterFunction<ServerResponse> createBootcampRoute(Handler handler) {
    return route(POST(BASE_URL + "/bootcamp"), handler::createBootcamp);
  }
}
