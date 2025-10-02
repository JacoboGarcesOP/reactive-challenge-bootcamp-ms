package co.com.bancolombia.usecase;

import co.com.bancolombia.model.bootcamp.Bootcamp;
import co.com.bancolombia.model.bootcamp.Capacity;
import co.com.bancolombia.model.bootcamp.Technology;
import co.com.bancolombia.model.bootcamp.gateway.BootcampGateway;
import co.com.bancolombia.model.bootcamp.gateway.CapacityGateway;
import co.com.bancolombia.usecase.exception.BussinessException;
import co.com.bancolombia.usecase.response.BootcampsWithTechnologiesResponse;
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
  private CapacityGateway capacityGateway;
  private GetBootcampByIdUseCase useCase;

  @BeforeEach
  void setUp() {
    bootcampGateway = Mockito.mock(BootcampGateway.class);
    capacityGateway = Mockito.mock(CapacityGateway.class);
    useCase = new GetBootcampByIdUseCase(bootcampGateway, capacityGateway);
  }

  @Test
  void executeShouldReturnBootcampWithCapacitiesAndTechnologies() {
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
    Technology t1 = new Technology(10L, "Java", "Java 21 LTS");
    Technology t2 = new Technology(11L, "Spring Boot", "Spring Boot Framework");
    Capacity c1 = new Capacity(1L, "Payments Squad", "Handles all payment features", java.util.List.of(t1));
    Capacity c2 = new Capacity(2L, "User Management", "Handles user operations", java.util.List.of(t2));
    Mockito.when(capacityGateway.findByBootcampId(bootcampId))
      .thenReturn(reactor.core.publisher.Flux.just(c1, c2));

    // Act & Assert
    StepVerifier.create(useCase.execute(bootcampId))
      .assertNext(response -> {
        assert response.getBootcampId().equals(bootcampId);
        assert response.getName().equals("Bootcamp Java");
        assert response.getDescription().equals("Formación intensiva en Java");
        assert response.getDuration().equals(60);
        assert response.getCapacities().size() == 2;
        var cap1 = response.getCapacities().get(0);
        assert cap1.getCapacityId().equals(1L);
        assert cap1.getTechnologies().size() == 1;
        assert cap1.getTechnologies().get(0).getName().equals("Java");
        var cap2 = response.getCapacities().get(1);
        assert cap2.getCapacityId().equals(2L);
        assert cap2.getTechnologies().get(0).getName().equals("Spring Boot");
      })
      .verifyComplete();

    Mockito.verify(bootcampGateway).findById(bootcampId);
    Mockito.verify(capacityGateway).findByBootcampId(bootcampId);
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
