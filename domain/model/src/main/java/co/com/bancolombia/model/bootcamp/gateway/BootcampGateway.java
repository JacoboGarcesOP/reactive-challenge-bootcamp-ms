package co.com.bancolombia.model.bootcamp.gateway;

import co.com.bancolombia.model.bootcamp.Bootcamp;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BootcampGateway {
  Mono<Boolean> existsByName(String name);
  Mono<Bootcamp> save(Bootcamp bootcamp);
  Flux<Bootcamp> findAllPagedSorted(int page, int size, String sortBy, String order);
  Flux<Bootcamp> findAll();
}
