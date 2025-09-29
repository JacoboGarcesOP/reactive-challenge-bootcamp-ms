package co.com.bancolombia.api;

import co.com.bancolombia.api.request.CreateBootcampRequest;
import co.com.bancolombia.api.response.ErrorResponse;
import co.com.bancolombia.model.bootcamp.exception.DomainException;
import co.com.bancolombia.usecase.CreateBootcampUseCase;
import co.com.bancolombia.usecase.GetPaginatedBootcampsUseCase;
import co.com.bancolombia.usecase.command.CreateBootcampCommand;
import co.com.bancolombia.usecase.exception.BussinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class Handler {
  private static final String VALIDATION_ERROR_TEXT = "VALIDATION_ERROR";
  private static final String DOMAIN_ERROR_TEXT = "DOMAIN_ERROR";
  private static final String BUSINESS_ERROR_TEXT = "BUSINESS_ERROR";
  private static final String INTERNAL_ERROR_TEXT = "INTERNAL_ERROR";
  private static final String GENERIC_ERROR_MESSAGE = "An unexpected error occurred";

  private final CreateBootcampUseCase createBootcampUseCase;
  private final GetPaginatedBootcampsUseCase getPaginatedBootcampsUseCase;
  private final Validator validator;

  public Mono<ServerResponse> createBootcamp(ServerRequest serverRequest) {
    return serverRequest.bodyToMono(CreateBootcampRequest.class)
      .doOnNext(this::validateCreateBootcampRequest)
      .map(this::mapToCreateBootcampCommand)
      .flatMap(createBootcampUseCase::execute)
      .flatMap(this::buildSuccessResponse)
      .onErrorResume(ConstraintViolationException.class, this::handleValidationException)
      .onErrorResume(DomainException.class, this::handleDomainException)
      .onErrorResume(BussinessException.class, this::handleBusinessException)
      .onErrorResume(Exception.class, this::handleGenericException)
      .doOnError(error -> log.error(GENERIC_ERROR_MESSAGE, error));
  }

  public Mono<ServerResponse> getAllBootcamps(ServerRequest serverRequest) {
    int page = serverRequest.queryParam("page").map(Integer::parseInt).orElse(0);
    int size = serverRequest.queryParam("size").map(Integer::parseInt).orElse(10);
    String sortBy = serverRequest.queryParam("sortBy").orElse("name");
    String order = serverRequest.queryParam("order").orElse("asc");

    return getPaginatedBootcampsUseCase.execute(page, size, sortBy, order)
      .flatMap(this::buildSuccessResponse)
      .onErrorResume(DomainException.class, this::handleDomainException)
      .onErrorResume(BussinessException.class, this::handleBusinessException)
      .onErrorResume(Exception.class, this::handleGenericException)
      .doOnError(error -> log.error("Error retrieving bootcamps", error));
  }

  private void validateCreateBootcampRequest(CreateBootcampRequest request) {
    Set<ConstraintViolation<CreateBootcampRequest>> violations = validator.validate(request);
    if (!violations.isEmpty()) {
      throw new ConstraintViolationException(violations);
    }
  }

  private CreateBootcampCommand mapToCreateBootcampCommand(CreateBootcampRequest request) {
    return new CreateBootcampCommand(
      request.getName(),
      request.getDescription(),
      request.getLaunchDate(),
      request.getDuration(),
      request.getCapacityIds()
    );
  }

  private Mono<ServerResponse> buildSuccessResponse(Object response) {
    return ServerResponse.ok()
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(response);
  }

  private Mono<ServerResponse> handleValidationException(ConstraintViolationException ex) {
    String errorMessage = ex.getConstraintViolations().stream()
      .map(ConstraintViolation::getMessage)
      .collect(Collectors.joining(", "));

    log.warn("Validation error: {}", errorMessage);

    return ServerResponse.badRequest()
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(createErrorResponse(VALIDATION_ERROR_TEXT, errorMessage));
  }

  private Mono<ServerResponse> handleDomainException(DomainException ex) {
    log.warn("Domain error: {}", ex.getMessage());

    return ServerResponse.badRequest()
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(createErrorResponse(DOMAIN_ERROR_TEXT, ex.getMessage()));
  }

  private Mono<ServerResponse> handleBusinessException(BussinessException ex) {
    log.warn("Business error: {}", ex.getMessage());

    return ServerResponse.badRequest()
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(createErrorResponse(BUSINESS_ERROR_TEXT, ex.getMessage()));
  }

  private Mono<ServerResponse> handleGenericException(Exception ex) {
    log.error("Unexpected error", ex);

    return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(createErrorResponse(INTERNAL_ERROR_TEXT, GENERIC_ERROR_MESSAGE));
  }

  private ErrorResponse createErrorResponse(String error, String message) {
    return new ErrorResponse(error, message);
  }
}
