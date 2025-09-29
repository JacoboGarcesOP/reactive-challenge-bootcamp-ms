package co.com.bancolombia.model.bootcamp.gateway;

import co.com.bancolombia.model.bootcamp.Capacity;
import co.com.bancolombia.model.bootcamp.CapacityBootcamp;
import co.com.bancolombia.model.bootcamp.values.Id;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CapacityGateway {
  Flux<Capacity> findByBootcampId(Long bootcampId);
  Mono<Capacity> associateCapacity(CapacityBootcamp capacityBootcamp);
  Flux<Id> findAllCapacities();
}
