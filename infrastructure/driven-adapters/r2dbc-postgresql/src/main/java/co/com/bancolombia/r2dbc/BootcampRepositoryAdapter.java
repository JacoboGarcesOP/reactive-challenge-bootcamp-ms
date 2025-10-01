package co.com.bancolombia.r2dbc;

import co.com.bancolombia.model.bootcamp.Bootcamp;
import co.com.bancolombia.model.bootcamp.gateway.BootcampGateway;
import co.com.bancolombia.r2dbc.entity.BootcampEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Comparator;

@Repository
public class BootcampRepositoryAdapter implements BootcampGateway {
  private final BootcampRepository bootcampRepository;

  public BootcampRepositoryAdapter(BootcampRepository bootcampRepository) {
    this.bootcampRepository = bootcampRepository;
  }

  @Override
  public Mono<Boolean> existsByName(String name) {
    return bootcampRepository.existsByName(name);
  }

  @Override
  public Mono<Bootcamp> save(Bootcamp bootcamp) {
    BootcampEntity entity = new BootcampEntity(
      null,
      bootcamp.getName().getValue(),
      bootcamp.getDescription().getValue(),
      bootcamp.getLaunchDate().getValue(),
      bootcamp.getDuration().getValue()
    );

    return bootcampRepository.save(entity)
      .map(saved -> {
        bootcamp.setId(new co.com.bancolombia.model.bootcamp.values.Id(saved.getId()));
        return bootcamp;
      });
  }

  @Override
  public Flux<Bootcamp> findAllPagedSorted(int page, int size, String sortBy, String order) {
    int limit = Math.max(size, 0);
    int offset = Math.max(page, 0) * limit;

    Flux<BootcampEntity> source = bootcampRepository.findAllOrderByNameAsc(limit, offset);
    if ("desc".equalsIgnoreCase(order)) {
      return source
        .sort(Comparator.comparing(BootcampEntity::getName).reversed())
        .map(this::toDomain);
    }
    return source.map(this::toDomain);
  }

  @Override
  public Flux<Bootcamp> findAll() {
    return bootcampRepository.findAll().map(bootcampEntity -> new Bootcamp(bootcampEntity.getId(), bootcampEntity.getName(), bootcampEntity.getDescription(), bootcampEntity.getLaunchDate(), bootcampEntity.getDuration()));
  }

  @Override
  public Mono<Boolean> existsById(Long id) {
    return bootcampRepository.existsById(id);
  }

  @Transactional
  @Override
  public Mono<Void> deleteById(Long id) {
    return bootcampRepository.deleteById(id);
  }

  private Bootcamp toDomain(BootcampEntity entity) {
    return new Bootcamp(
      entity.getId(),
      entity.getName(),
      entity.getDescription(),
      entity.getLaunchDate(),
      entity.getDuration()
    );
  }
}
