package co.com.bancolombia.usecase;

import co.com.bancolombia.model.bootcamp.gateway.BootcampGateway;
import co.com.bancolombia.model.bootcamp.gateway.CapacityGateway;
import co.com.bancolombia.usecase.exception.BussinessException;
import co.com.bancolombia.usecase.response.DeleteBootcampResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteBootcampUseCaseTest {

  @Mock
  private BootcampGateway bootcampGateway;

  @Mock
  private CapacityGateway capacityGateway;

  @InjectMocks
  private DeleteBootcampUseCase useCase;

  @Test
  void shouldDeleteBootcampWhenExists() {
    when(bootcampGateway.existsById(10L)).thenReturn(Mono.just(true));
    when(capacityGateway.deleteByBootcampId(10L)).thenReturn(reactor.core.publisher.Flux.just(new co.com.bancolombia.model.bootcamp.values.Id(1L), new co.com.bancolombia.model.bootcamp.values.Id(2L)));
    when(bootcampGateway.deleteById(10L)).thenReturn(Mono.empty());

    StepVerifier.create(useCase.execute(10L))
      .expectNextMatches(r -> r.getBootcampId().equals(10L) && r.getDeletedCapacityIds().size() == 2)
      .verifyComplete();

    verify(capacityGateway).deleteByBootcampId(10L);
    verify(bootcampGateway).deleteById(10L);
  }

  @Test
  void shouldFailWhenBootcampIdNull() {
    StepVerifier.create(useCase.execute(null))
      .expectError(BussinessException.class)
      .verify();
  }

  @Test
  void shouldReturnNotFoundWhenBootcampNotExists() {
    when(bootcampGateway.existsById(10L)).thenReturn(Mono.just(false));

    StepVerifier.create(useCase.execute(10L))
      .expectError(BussinessException.class)
      .verify();
  }

  @Test
  void shouldNotDeleteBootcampWhenCapacityDeletionFails() {
    when(bootcampGateway.existsById(10L)).thenReturn(Mono.just(true));
    when(capacityGateway.deleteByBootcampId(10L)).thenReturn(reactor.core.publisher.Flux.error(new RuntimeException("ext error")));

    StepVerifier.create(useCase.execute(10L))
      .expectError()
      .verify();

    verify(bootcampGateway, never()).deleteById(anyLong());
  }
}

 