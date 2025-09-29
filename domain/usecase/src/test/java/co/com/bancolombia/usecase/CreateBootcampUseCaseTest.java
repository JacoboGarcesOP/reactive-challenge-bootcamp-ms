package co.com.bancolombia.usecase;

import co.com.bancolombia.model.bootcamp.Bootcamp;
import co.com.bancolombia.model.bootcamp.Capacity;
import co.com.bancolombia.model.bootcamp.CapacityBootcamp;
import co.com.bancolombia.model.bootcamp.gateway.BootcampGateway;
import co.com.bancolombia.model.bootcamp.gateway.CapacityGateway;
import co.com.bancolombia.model.bootcamp.values.Id;
import co.com.bancolombia.usecase.command.CreateBootcampCommand;
import co.com.bancolombia.usecase.exception.BussinessException;
import co.com.bancolombia.usecase.response.CreateBootcampResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateBootcampUseCaseTest {

  @Mock
  private BootcampGateway bootcampGateway;

  @Mock
  private CapacityGateway capacityGateway;

  @InjectMocks
  private CreateBootcampUseCase useCase;

  private CreateBootcampCommand validCommand;

  @BeforeEach
  void setUp() {
    validCommand = new CreateBootcampCommand(
      "Java Bootcamp",
      "Desc",
      LocalDate.now().plusDays(10),
      60,
      List.of(1L, 2L, 3L)
    );
  }

  @Test
  void shouldCreateBootcampAndAssociateCapacities() {
    when(bootcampGateway.existsByName("Java Bootcamp")).thenReturn(Mono.just(false));
    when(capacityGateway.findAllCapacities()).thenReturn(Flux.just(new Id(1L), new Id(2L), new Id(3L), new Id(4L)));

    Bootcamp saved = new Bootcamp(10L, "Java Bootcamp", "Desc", validCommand.getLaunchDate(), validCommand.getDuration());
    when(bootcampGateway.save(any(Bootcamp.class))).thenReturn(Mono.just(saved));

    when(capacityGateway.associateCapacity(any(CapacityBootcamp.class)))
      .thenAnswer(inv -> {
        CapacityBootcamp cb = inv.getArgument(0);
        return Mono.just(new Capacity(cb.getCapacityId().getValue(), "cap-" + cb.getCapacityId().getValue(), "desc"));
      });

    Mono<CreateBootcampResponse> result = useCase.execute(validCommand);

    StepVerifier.create(result)
      .assertNext(resp -> {
        assertEquals(10L, resp.getBootcampId());
        assertEquals("Java Bootcamp", resp.getName());
        assertEquals(3, resp.getCapacities().size());
      })
      .verifyComplete();

    verify(bootcampGateway).existsByName("Java Bootcamp");
    verify(bootcampGateway).save(any(Bootcamp.class));
    verify(capacityGateway, times(3)).associateCapacity(any(CapacityBootcamp.class));
  }

  @Test
  void shouldFailWhenBootcampNameDuplicated() {
    when(bootcampGateway.existsByName("Java Bootcamp")).thenReturn(Mono.just(true));

    StepVerifier.create(useCase.execute(validCommand))
      .expectError(BussinessException.class)
      .verify();
  }

  @Test
  void shouldFailWhenNoCapacities() {
    CreateBootcampCommand cmd = new CreateBootcampCommand(
      "Java Bootcamp", "Desc", LocalDate.now().plusDays(10), 60, List.of()
    );

    StepVerifier.create(useCase.execute(cmd))
      .expectError(BussinessException.class)
      .verify();
  }

  @Test
  void shouldFailWhenTooManyCapacities() {
    CreateBootcampCommand cmd = new CreateBootcampCommand(
      "Java Bootcamp", "Desc", LocalDate.now().plusDays(10), 60, List.of(1L,2L,3L,4L,5L)
    );

    StepVerifier.create(useCase.execute(cmd))
      .expectError(BussinessException.class)
      .verify();
  }

  @Test
  void shouldFailWhenDuplicatedCapacities() {
    CreateBootcampCommand cmd = new CreateBootcampCommand(
      "Java Bootcamp", "Desc", LocalDate.now().plusDays(10), 60, List.of(1L,2L,2L)
    );

    StepVerifier.create(useCase.execute(cmd))
      .expectError(BussinessException.class)
      .verify();
  }

  @Test
  void shouldFailWhenSomeCapacitiesNotFound() {
    when(bootcampGateway.existsByName("Java Bootcamp")).thenReturn(Mono.just(false));
    when(capacityGateway.findAllCapacities()).thenReturn(Flux.just(new Id(1L)));

    StepVerifier.create(useCase.execute(validCommand))
      .expectError(BussinessException.class)
      .verify();
  }
}


