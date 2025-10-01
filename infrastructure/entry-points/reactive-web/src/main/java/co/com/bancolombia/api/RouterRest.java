package co.com.bancolombia.api;

import co.com.bancolombia.api.request.CreateBootcampRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
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

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.DELETE;
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

  @Bean
  @RouterOperation(
    path = "/v1/api/bootcamp/{bootcampId}",
    produces = {MediaType.APPLICATION_JSON_VALUE},
    method = RequestMethod.DELETE,
    beanClass = Handler.class,
    beanMethod = "deleteBootcamp",
    operation = @Operation(
      operationId = "deleteBootcamp",
      summary = "Eliminar bootcamp por ID",
      description = "Elimina un bootcamp local y sus capacidades asociadas en el microservicio externo.",
      tags = {"Bootcamp Management"},
      parameters = {
        @Parameter(name = "bootcampId", in = ParameterIn.PATH, required = true, description = "ID del bootcamp", example = "10", schema = @Schema(type = "integer"))
      },
      responses = {
        @ApiResponse(responseCode = "200", description = "Eliminación exitosa",
          content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            examples = @ExampleObject(
              name = "Success Response",
              summary = "Bootcamp eliminado",
              value = "{\n" +
                "  \"bootcampId\": 10,\n" +
                "  \"deletedCapacityIds\": [1, 2, 3]\n" +
                "}"
            )
          )
        ),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida",
          content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            examples = @ExampleObject(
              name = "Validation Error",
              summary = "Error de validación",
              value = "{\n" +
                "  \"error\": \"VALIDATION_ERROR\",\n" +
                "  \"message\": \"bootcampId must be a number\"\n" +
                "}"
            )
          )
        ),
        @ApiResponse(responseCode = "404", description = "Bootcamp no encontrado",
          content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            examples = @ExampleObject(
              name = "Business Error",
              summary = "No encontrado",
              value = "{\n" +
                "  \"error\": \"BUSINESS_ERROR\",\n" +
                "  \"message\": \"bootcamp not found\"\n" +
                "}"
            )
          )
        ),
        @ApiResponse(responseCode = "500", description = "Error interno",
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
  public RouterFunction<ServerResponse> deleteBootcamp(Handler handler) {
    return route(DELETE(BASE_URL + "/bootcamp/{bootcampId}"), handler::deleteBootcamp);
  }

  @Bean
  @RouterOperation(
    path = "/v1/api/bootcamp",
    produces = {MediaType.APPLICATION_JSON_VALUE},
    method = RequestMethod.GET,
    beanClass = Handler.class,
    beanMethod = "getAllBootcamps",
    operation = @Operation(
      operationId = "getAllBootcamps",
      summary = "Obtener bootcamps paginados y ordenados",
      description = "Retorna bootcamps con sus capacidades y tecnologías asociadas, soportando paginación y ordenamiento. " +
        "Maneja errores de dominio, negocio e internos.",
      tags = {"Bootcamp Management"},
      parameters = {
        @Parameter(name = "page", description = "Número de página (base 0)", example = "0", schema = @Schema(type = "integer", defaultValue = "0")),
        @Parameter(name = "size", description = "Tamaño de página", example = "10", schema = @Schema(type = "integer", defaultValue = "10")),
        @Parameter(name = "sortBy", description = "Campo por el cual ordenar", example = "name", schema = @Schema(type = "string", allowableValues = {"name", "capacities"}, defaultValue = "name")),
        @Parameter(name = "order", description = "Dirección del ordenamiento", example = "asc", schema = @Schema(type = "string", allowableValues = {"asc", "desc"}, defaultValue = "asc"))
      },
      responses = {
        @ApiResponse(responseCode = "200", description = "Bootcamps obtenidos exitosamente",
          content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            examples = @ExampleObject(
              name = "Success Response",
              summary = "Lista de bootcamps paginados y ordenados",
              value = "{\n" +
                "  \"bootcamps\": [\n" +
                "    {\n" +
                "      \"bootcampId\": 1,\n" +
                "      \"name\": \"Bootcamp Java\",\n" +
                "      \"description\": \"Formación intensiva en Java\",\n" +
                "      \"launchDate\": \"2030-01-10\",\n" +
                "      \"duration\": 60,\n" +
                "      \"capacities\": [\n" +
                "        {\n" +
                "          \"capacityId\": 1,\n" +
                "          \"name\": \"Payments Squad\",\n" +
                "          \"description\": \"Handles all payment features\",\n" +
                "          \"technologies\": [\n" +
                "            { \"technologyId\": 10, \"name\": \"Java\", \"description\": \"Java 21 LTS\" },\n" +
                "            { \"technologyId\": 11, \"name\": \"Spring Boot\", \"description\": \"Spring Boot Framework\" }\n" +
                "          ]\n" +
                "        },\n" +
                "        {\n" +
                "          \"capacityId\": 2,\n" +
                "          \"name\": \"User Management\",\n" +
                "          \"description\": \"Handles user operations\",\n" +
                "          \"technologies\": [\n" +
                "            { \"technologyId\": 12, \"name\": \"React\", \"description\": \"React Framework\" }\n" +
                "          ]\n" +
                "        }\n" +
                "      ]\n" +
                "    },\n" +
                "    {\n" +
                "      \"bootcampId\": 2,\n" +
                "      \"name\": \"Bootcamp Python\",\n" +
                "      \"description\": \"Formación intensiva en Python\",\n" +
                "      \"launchDate\": \"2030-02-15\",\n" +
                "      \"duration\": 45,\n" +
                "      \"capacities\": [\n" +
                "        {\n" +
                "          \"capacityId\": 3,\n" +
                "          \"name\": \"Data Analytics\",\n" +
                "          \"description\": \"Data analysis and visualization\",\n" +
                "          \"technologies\": [\n" +
                "            { \"technologyId\": 13, \"name\": \"Python\", \"description\": \"Python Programming Language\" },\n" +
                "            { \"technologyId\": 14, \"name\": \"Pandas\", \"description\": \"Data Analysis Library\" }\n" +
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
                "}"
            )
          )
        ),
        @ApiResponse(responseCode = "400", description = "Error de dominio o negocio",
          content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            examples = @ExampleObject(
              name = "Business Error",
              summary = "Error de negocio",
              value = "{\n" +
                "  \"error\": \"BUSINESS_ERROR\",\n" +
                "  \"message\": \"No bootcamps found\"\n" +
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
  public RouterFunction<ServerResponse> getAllBootcampsRoute(Handler handler) {
    return route(GET(BASE_URL + "/bootcamp"), handler::getAllBootcamps);
  }
}
