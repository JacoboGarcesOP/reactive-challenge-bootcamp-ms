package co.com.bancolombia.r2dbc;

import co.com.bancolombia.model.bootcamp.Bootcamp;
import co.com.bancolombia.r2dbc.entity.BootcampEntity;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class BootcampRepositoryAdapterTest {

    @Test
    void findAllPagedSorted_desc_sorts() {
        BootcampRepository repo = mock(BootcampRepository.class);
        when(repo.findAllOrderByNameAsc(anyInt(), anyInt()))
                .thenReturn(Flux.just(
                        new BootcampEntity(1L, "A", "d", LocalDate.now().plusDays(1), 5),
                        new BootcampEntity(2L, "B", "d", LocalDate.now().plusDays(2), 5)
                ));

        BootcampRepositoryAdapter adapter = new BootcampRepositoryAdapter(repo);

        StepVerifier.create(adapter.findAllPagedSorted(0, 10, "name", "desc").collectList())
                .expectNextMatches(list -> list.size() == 2)
                .verifyComplete();
    }

    @Test
    void existsById_and_deleteById_delegate() {
        BootcampRepository repo = mock(BootcampRepository.class);
        when(repo.existsById(1L)).thenReturn(Mono.just(true));
        when(repo.deleteById(1L)).thenReturn(Mono.empty());

        BootcampRepositoryAdapter adapter = new BootcampRepositoryAdapter(repo);

        StepVerifier.create(adapter.existsById(1L)).expectNext(true).verifyComplete();
        StepVerifier.create(adapter.deleteById(1L)).verifyComplete();
    }

    @Test
    void findAll_mapsEntities() {
        BootcampRepository repo = mock(BootcampRepository.class);
        when(repo.findAll()).thenReturn(Flux.just(new BootcampEntity(1L, "N", "D", LocalDate.now().plusDays(1), 1)));
        BootcampRepositoryAdapter adapter = new BootcampRepositoryAdapter(repo);

        StepVerifier.create(adapter.findAll().collectList())
                .expectNextMatches(list -> list.size() == 1 && list.get(0) instanceof Bootcamp)
                .verifyComplete();
    }
}
