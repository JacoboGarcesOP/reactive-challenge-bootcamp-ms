package co.com.bancolombia.r2dbc;

import co.com.bancolombia.r2dbc.entity.BootcampEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BootcampRepository extends ReactiveCrudRepository<BootcampEntity, Long> {
  Mono<Boolean> existsByName(String name);

  @Query("SELECT bootcamp_id, name, description, launch_date, duration FROM bootcamp_schema.bootcamp ORDER BY name ASC LIMIT :limit OFFSET :offset")
  Flux<BootcampEntity> findAllOrderByNameAsc(int limit, int offset);
}
