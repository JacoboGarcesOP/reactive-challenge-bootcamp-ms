package co.com.bancolombia.r2dbc;

import co.com.bancolombia.model.bootcamp.Bootcamp;
import co.com.bancolombia.model.bootcamp.values.Description;
import co.com.bancolombia.model.bootcamp.values.Duration;
import co.com.bancolombia.model.bootcamp.values.LaunchDate;
import co.com.bancolombia.model.bootcamp.values.Name;
import co.com.bancolombia.r2dbc.entity.BootcampEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

class BootcampRepositoryAdapterTest {

  private BootcampRepository repository;
  private BootcampRepositoryAdapter adapter;

  @BeforeEach
  void setUp() {
    repository = Mockito.mock(BootcampRepository.class);
    adapter = new BootcampRepositoryAdapter(repository);
  }

  @Test
  void existsByNameReturnsMono() {
    Mockito.when(repository.existsByName("X")).thenReturn(Mono.just(true));
    StepVerifier.create(adapter.existsByName("X")).expectNext(true).verifyComplete();
  }

  @Test
  void saveMapsDomainToEntityAndBack() {
    Bootcamp toSave = new Bootcamp(
      "BC Name",
      "BC Desc",
      LocalDate.now().plusDays(2),
      8
    );

    BootcampEntity saved = new BootcampEntity(1L, "BC Name", "BC Desc", toSave.getLaunchDate().getValue(), 8);
    Mockito.when(repository.save(any(BootcampEntity.class))).thenReturn(Mono.just(saved));

    StepVerifier.create(adapter.save(toSave))
      .assertNext(bc -> {
        org.junit.jupiter.api.Assertions.assertNotNull(bc.getId());
        org.junit.jupiter.api.Assertions.assertEquals(1L, bc.getId().getValue());
        org.junit.jupiter.api.Assertions.assertEquals("BC Name", bc.getName().getValue());
      })
      .verifyComplete();
  }

  @Test
  void findAllPagedSortedAsc() {
    BootcampEntity e1 = new BootcampEntity(2L, "A", "d", LocalDate.now().plusDays(2), 4);
    BootcampEntity e2 = new BootcampEntity(1L, "B", "d", LocalDate.now().plusDays(3), 6);
    Mockito.when(repository.findAllOrderByNameAsc(2, 0)).thenReturn(Flux.just(e1, e2));

    StepVerifier.create(adapter.findAllPagedSorted(0, 2, "name", "asc"))
      .expectNextCount(2)
      .verifyComplete();
  }

  @Test
  void findAllPagedSortedDesc() {
    BootcampEntity e1 = new BootcampEntity(2L, "A", "d", LocalDate.now().plusDays(2), 4);
    BootcampEntity e2 = new BootcampEntity(1L, "B", "d", LocalDate.now().plusDays(3), 6);
    Mockito.when(repository.findAllOrderByNameAsc(2, 0)).thenReturn(Flux.just(e1, e2));

    StepVerifier.create(adapter.findAllPagedSorted(0, 2, "name", "desc"))
      .expectNextMatches(bc -> "B".equals(bc.getName().getValue()))
      .expectNextMatches(bc -> "A".equals(bc.getName().getValue()))
      .verifyComplete();
  }
}


