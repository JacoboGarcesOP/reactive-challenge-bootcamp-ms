package co.com.bancolombia.usecase;

import co.com.bancolombia.model.bootcamp.Bootcamp;
import co.com.bancolombia.model.bootcamp.gateway.BootcampGateway;
import co.com.bancolombia.usecase.exception.BussinessException;
import co.com.bancolombia.usecase.response.BootcampResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

class GetBootcampByIdUseCaseTest {

  private BootcampGateway bootcampGateway;
  private GetBootcampByIdUseCase useCase;

  @BeforeEach
  void setUp() {
    bootcampGateway = Mockito.mock(BootcampGateway.class);
    useCase = new GetBootcampByIdUseCase(bootcampGateway);
  }

  @Test
  void executeShouldReturnBootcamp() {
    // Arrange
    Long bootcampId = 1L;
    Bootcamp bootcamp = new Bootcamp(
      bootcampId,
      "Bootcamp Java",
      "Formación intensiva en Java",
      LocalDate.now().plusDays(30),
      60
    );

    Mockito.when(bootcampGateway.findById(bootcampId)).thenReturn(Mono.just(bootcamp));

    // Act & Assert
    StepVerifier.create(useCase.execute(bootcampId))
      .assertNext(response -> {
        assert response.getBootcampId().equals(bootcampId);
        assert response.getName().equals("Bootcamp Java");
        assert response.getDescription().equals("Formación intensiva en Java");
        assert response.getDuration().equals(60);
      })
      .verifyComplete();

    Mockito.verify(bootcampGateway).findById(bootcampId);
  }

  @Test
  void executeShouldThrowExceptionWhenBootcampNotFound() {
    // Arrange
    Long bootcampId = 999L;
    Mockito.when(bootcampGateway.findById(bootcampId)).thenReturn(Mono.empty());

    // Act & Assert
    StepVerifier.create(useCase.execute(bootcampId))
      .expectError(BussinessException.class)
      .verify();

    Mockito.verify(bootcampGateway).findById(bootcampId);
  }
}
