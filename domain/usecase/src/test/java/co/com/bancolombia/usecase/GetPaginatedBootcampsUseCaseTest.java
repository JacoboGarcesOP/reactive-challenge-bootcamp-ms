package co.com.bancolombia.usecase;

import co.com.bancolombia.model.bootcamp.Bootcamp;
import co.com.bancolombia.model.bootcamp.Capacity;
import co.com.bancolombia.model.bootcamp.Technology;
import co.com.bancolombia.model.bootcamp.gateway.BootcampGateway;
import co.com.bancolombia.model.bootcamp.gateway.CapacityGateway;
import co.com.bancolombia.usecase.response.BootcampsWithTechnologiesResponse;
import co.com.bancolombia.usecase.response.GetBootcampsResponse;
import co.com.bancolombia.usecase.response.GetCapacityResponse;
import co.com.bancolombia.usecase.response.TechnologyResponse;
import co.com.bancolombia.usecase.response.FilterResponse;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetPaginatedBootcampsUseCaseTest {

  @Mock
  private BootcampGateway bootcampGateway;

  @Mock
  private CapacityGateway capacityGateway;

  @InjectMocks
  private GetPaginatedBootcampsUseCase useCase;

  private Bootcamp bootcamp1;
  private Bootcamp bootcamp2;
  private Capacity capacity1;
  private Capacity capacity2;
  private Technology technology1;
  private Technology technology2;

  @BeforeEach
  void setUp() {
    bootcamp1 = new Bootcamp(1L, "Java Bootcamp", "Java training", LocalDate.now().plusDays(30), 60);
    bootcamp2 = new Bootcamp(2L, "Python Bootcamp", "Python training", LocalDate.now().plusDays(45), 45);
    
    technology1 = new Technology(10L, "Java", "Java 21 LTS");
    technology2 = new Technology(11L, "Spring Boot", "Spring Boot Framework");
    
    capacity1 = new Capacity(1L, "Payments Squad", "Handles all payment features", List.of(technology1, technology2));
    capacity2 = new Capacity(2L, "User Management", "Handles user operations", List.of(technology1));
  }

  @Test
  void shouldGetBootcampsSortedByNameAsc() {
    // Given
    when(bootcampGateway.findAllPagedSorted(0, 10, "name", "asc"))
      .thenReturn(Flux.just(bootcamp1, bootcamp2));
    when(capacityGateway.findByBootcampId(1L))
      .thenReturn(Flux.just(capacity1));
    when(capacityGateway.findByBootcampId(2L))
      .thenReturn(Flux.just(capacity2));

    // When
    Mono<GetBootcampsResponse> result = useCase.execute(0, 10, "name", "asc");

    // Then
    StepVerifier.create(result)
      .assertNext(response -> {
        assertEquals(2, response.getBootcamps().size());
        assertEquals("Java Bootcamp", response.getBootcamps().get(0).getName());
        assertEquals("Python Bootcamp", response.getBootcamps().get(1).getName());
        assertEquals(0, response.getFilter().getPage());
        assertEquals(10, response.getFilter().getSize());
        assertEquals("name", response.getFilter().getSortBy());
        assertEquals("asc", response.getFilter().getOrder());
        
        // Verify first bootcamp has capacities with technologies
        BootcampsWithTechnologiesResponse firstBootcamp = response.getBootcamps().get(0);
        assertEquals(1, firstBootcamp.getCapacities().size());
        assertEquals("Payments Squad", firstBootcamp.getCapacities().get(0).getName());
        assertEquals(2, firstBootcamp.getCapacities().get(0).getTechnologies().size());
        assertEquals("Java", firstBootcamp.getCapacities().get(0).getTechnologies().get(0).getName());
      })
      .verifyComplete();

    verify(bootcampGateway).findAllPagedSorted(0, 10, "name", "asc");
    verify(capacityGateway).findByBootcampId(1L);
    verify(capacityGateway).findByBootcampId(2L);
  }

  @Test
  void shouldGetBootcampsSortedByNameDesc() {
    // Given
    when(bootcampGateway.findAllPagedSorted(0, 10, "name", "desc"))
      .thenReturn(Flux.just(bootcamp2, bootcamp1));
    when(capacityGateway.findByBootcampId(2L))
      .thenReturn(Flux.just(capacity2));
    when(capacityGateway.findByBootcampId(1L))
      .thenReturn(Flux.just(capacity1));

    // When
    Mono<GetBootcampsResponse> result = useCase.execute(0, 10, "name", "desc");

    // Then
    StepVerifier.create(result)
      .assertNext(response -> {
        assertEquals(2, response.getBootcamps().size());
        assertEquals("Python Bootcamp", response.getBootcamps().get(0).getName());
        assertEquals("Java Bootcamp", response.getBootcamps().get(1).getName());
        assertEquals("desc", response.getFilter().getOrder());
      })
      .verifyComplete();
  }

  @Test
  void shouldGetBootcampsSortedByCapacitiesAsc() {
    // Given
    when(bootcampGateway.findAll())
      .thenReturn(Flux.just(bootcamp1, bootcamp2));
    when(capacityGateway.findByBootcampId(1L))
      .thenReturn(Flux.just(capacity1, capacity2)); // 2 capacities
    when(capacityGateway.findByBootcampId(2L))
      .thenReturn(Flux.just(capacity1)); // 1 capacity

    // When
    Mono<GetBootcampsResponse> result = useCase.execute(0, 10, "capacities", "asc");

    // Then
    StepVerifier.create(result)
      .assertNext(response -> {
        assertEquals(2, response.getBootcamps().size());
        // Should be sorted by capacity count ascending (Python first with 1 capacity, Java second with 2)
        assertEquals("Python Bootcamp", response.getBootcamps().get(0).getName());
        assertEquals(1, response.getBootcamps().get(0).getCapacities().size());
        assertEquals("Java Bootcamp", response.getBootcamps().get(1).getName());
        assertEquals(2, response.getBootcamps().get(1).getCapacities().size());
        assertEquals("capacities", response.getFilter().getSortBy());
        assertEquals("asc", response.getFilter().getOrder());
      })
      .verifyComplete();

    verify(bootcampGateway).findAll();
    verify(capacityGateway).findByBootcampId(1L);
    verify(capacityGateway).findByBootcampId(2L);
  }

  @Test
  void shouldGetBootcampsSortedByCapacitiesDesc() {
    // Given
    when(bootcampGateway.findAll())
      .thenReturn(Flux.just(bootcamp1, bootcamp2));
    when(capacityGateway.findByBootcampId(1L))
      .thenReturn(Flux.just(capacity1, capacity2)); // 2 capacities
    when(capacityGateway.findByBootcampId(2L))
      .thenReturn(Flux.just(capacity1)); // 1 capacity

    // When
    Mono<GetBootcampsResponse> result = useCase.execute(0, 10, "capacities", "desc");

    // Then
    StepVerifier.create(result)
      .assertNext(response -> {
        assertEquals(2, response.getBootcamps().size());
        // Should be sorted by capacity count descending (Java first with 2 capacities, Python second with 1)
        assertEquals("Java Bootcamp", response.getBootcamps().get(0).getName());
        assertEquals(2, response.getBootcamps().get(0).getCapacities().size());
        assertEquals("Python Bootcamp", response.getBootcamps().get(1).getName());
        assertEquals(1, response.getBootcamps().get(1).getCapacities().size());
        assertEquals("desc", response.getFilter().getOrder());
      })
      .verifyComplete();
  }

  @Test
  void shouldHandlePaginationCorrectly() {
    // Given
    when(bootcampGateway.findAllPagedSorted(1, 1, "name", "asc"))
      .thenReturn(Flux.just(bootcamp2));
    when(capacityGateway.findByBootcampId(2L))
      .thenReturn(Flux.just(capacity2));

    // When
    Mono<GetBootcampsResponse> result = useCase.execute(1, 1, "name", "asc");

    // Then
    StepVerifier.create(result)
      .assertNext(response -> {
        assertEquals(1, response.getBootcamps().size());
        assertEquals("Python Bootcamp", response.getBootcamps().get(0).getName());
        assertEquals(1, response.getFilter().getPage());
        assertEquals(1, response.getFilter().getSize());
      })
      .verifyComplete();
  }

  @Test
  void shouldHandleCapacitiesPaginationCorrectly() {
    // Given
    when(bootcampGateway.findAll())
      .thenReturn(Flux.just(bootcamp1, bootcamp2));
    when(capacityGateway.findByBootcampId(1L))
      .thenReturn(Flux.just(capacity1, capacity2)); // 2 capacities
    when(capacityGateway.findByBootcampId(2L))
      .thenReturn(Flux.just(capacity1)); // 1 capacity

    // When - page 0, size 1 should return first bootcamp (Python with 1 capacity) in ascending order
    Mono<GetBootcampsResponse> result = useCase.execute(0, 1, "capacities", "asc");

    // Then
    StepVerifier.create(result)
      .assertNext(response -> {
        assertEquals(1, response.getBootcamps().size());
        assertEquals("Python Bootcamp", response.getBootcamps().get(0).getName());
        assertEquals(1, response.getBootcamps().get(0).getCapacities().size());
        assertEquals(0, response.getFilter().getPage());
        assertEquals(1, response.getFilter().getSize());
      })
      .verifyComplete();
  }

  @Test
  void shouldHandleEmptyCapacitiesList() {
    // Given
    Capacity capacityWithoutTechnologies = new Capacity(3L, "Empty Squad", "No technologies", List.of());
    when(bootcampGateway.findAllPagedSorted(0, 10, "name", "asc"))
      .thenReturn(Flux.just(bootcamp1));
    when(capacityGateway.findByBootcampId(1L))
      .thenReturn(Flux.just(capacityWithoutTechnologies));

    // When
    Mono<GetBootcampsResponse> result = useCase.execute(0, 10, "name", "asc");

    // Then
    StepVerifier.create(result)
      .assertNext(response -> {
        assertEquals(1, response.getBootcamps().size());
        assertEquals(1, response.getBootcamps().get(0).getCapacities().size());
        assertEquals("Empty Squad", response.getBootcamps().get(0).getCapacities().get(0).getName());
        assertEquals(0, response.getBootcamps().get(0).getCapacities().get(0).getTechnologies().size());
      })
      .verifyComplete();
  }

  @Test
  void shouldHandleNullTechnologiesInCapacity() {
    // Given
    Capacity capacityWithNullTechnologies = new Capacity(4L, "Null Tech Squad", "Null technologies");
    capacityWithNullTechnologies.setTechnologies(null);
    when(bootcampGateway.findAllPagedSorted(0, 10, "name", "asc"))
      .thenReturn(Flux.just(bootcamp1));
    when(capacityGateway.findByBootcampId(1L))
      .thenReturn(Flux.just(capacityWithNullTechnologies));

    // When
    Mono<GetBootcampsResponse> result = useCase.execute(0, 10, "name", "asc");

    // Then
    StepVerifier.create(result)
      .assertNext(response -> {
        assertEquals(1, response.getBootcamps().size());
        assertEquals(1, response.getBootcamps().get(0).getCapacities().size());
        assertEquals("Null Tech Squad", response.getBootcamps().get(0).getCapacities().get(0).getName());
        assertEquals(0, response.getBootcamps().get(0).getCapacities().get(0).getTechnologies().size());
      })
      .verifyComplete();
  }

  @Test
  void shouldHandleEmptyBootcampsList() {
    // Given
    when(bootcampGateway.findAllPagedSorted(0, 10, "name", "asc"))
      .thenReturn(Flux.empty());

    // When
    Mono<GetBootcampsResponse> result = useCase.execute(0, 10, "name", "asc");

    // Then
    StepVerifier.create(result)
      .assertNext(response -> {
        assertEquals(0, response.getBootcamps().size());
        assertEquals(0, response.getFilter().getPage());
        assertEquals(10, response.getFilter().getSize());
      })
      .verifyComplete();
  }

  @Test
  void shouldHandleNegativePageAndSize() {
    // Given - The use case passes negative values directly to the gateway
    when(bootcampGateway.findAllPagedSorted(-1, -5, "name", "asc"))
      .thenReturn(Flux.empty());

    // When
    Mono<GetBootcampsResponse> result = useCase.execute(-1, -5, "name", "asc");

    // Then
    StepVerifier.create(result)
      .assertNext(response -> {
        assertEquals(0, response.getBootcamps().size());
        assertEquals(-1, response.getFilter().getPage());
        assertEquals(-5, response.getFilter().getSize());
      })
      .verifyComplete();
  }

  @Test
  void shouldHandleCapacitiesPaginationWithEmptySlice() {
    // Given
    when(bootcampGateway.findAll())
      .thenReturn(Flux.just(bootcamp1));
    when(capacityGateway.findByBootcampId(1L))
      .thenReturn(Flux.just(capacity1));

    // When - page 5, size 10 should return empty slice
    Mono<GetBootcampsResponse> result = useCase.execute(5, 10, "capacities", "asc");

    // Then
    StepVerifier.create(result)
      .assertNext(response -> {
        assertEquals(0, response.getBootcamps().size());
        assertEquals(5, response.getFilter().getPage());
        assertEquals(10, response.getFilter().getSize());
      })
      .verifyComplete();
  }
}
