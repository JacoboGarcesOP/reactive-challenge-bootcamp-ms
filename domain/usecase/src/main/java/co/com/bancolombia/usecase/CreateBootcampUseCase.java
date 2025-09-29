package co.com.bancolombia.usecase;

import co.com.bancolombia.model.bootcamp.Bootcamp;
import co.com.bancolombia.model.bootcamp.Capacity;
import co.com.bancolombia.model.bootcamp.CapacityBootcamp;
import co.com.bancolombia.model.bootcamp.gateway.BootcampGateway;
import co.com.bancolombia.model.bootcamp.gateway.CapacityGateway;
import co.com.bancolombia.model.bootcamp.values.Id;
import co.com.bancolombia.usecase.command.CreateBootcampCommand;
import co.com.bancolombia.usecase.exception.BussinessException;
import co.com.bancolombia.usecase.response.BootcampResponse;
import co.com.bancolombia.usecase.response.CapacityResponse;
import co.com.bancolombia.usecase.response.CreateBootcampResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.List;

public class CreateBootcampUseCase {
  private final String CAPACITIES_DUPLICATED_MESSAGE = "The bootcamp should not have duplicated capacities.";
  private final String CAPACITY_LOWER_BOUND_MESSAGE = "The bootcamp should have 1 capacity minimum.";
  private final String CAPACITY_UPPER_BOUND_MESSAGE = "The bootcamp should have 4 capacities maximum.";
  private final String CAPACITY_NOT_FOUND_MESSAGE = "Some capacities have not been found.";
  private final String BOOTCAMP_DUPLICATED_MESSAGE = "The bootcamp name cannot be duplicated.";
  private final Integer MAX_CAPACITIES = 4;

  private final BootcampGateway bootcampGateway;
  private final CapacityGateway capacityGateway;

  public CreateBootcampUseCase(BootcampGateway bootcampGateway, CapacityGateway capacityGateway) {
    this.bootcampGateway = bootcampGateway;
    this.capacityGateway = capacityGateway;
  }

  public Mono<CreateBootcampResponse> execute(CreateBootcampCommand command) {
    if (isWrongMinQuantityCapacities(command.getCapacityIds())) {
      return Mono.error(new BussinessException(CAPACITY_LOWER_BOUND_MESSAGE));
    }

    if (isWrongMaxQuantityCapacities(command.getCapacityIds())) {
      return Mono.error(new BussinessException(CAPACITY_UPPER_BOUND_MESSAGE));
    }

    if (existsDuplicatedCapacities(command.getCapacityIds())) {
      return Mono.error(new BussinessException(CAPACITIES_DUPLICATED_MESSAGE));
    }

    return bootcampGateway.existsByName(command.getName())
      .flatMap(exists -> {
        if (Boolean.TRUE.equals(exists)) {
          return Mono.error(new BussinessException(BOOTCAMP_DUPLICATED_MESSAGE));
        }

        return validateCapacitiesExisting(command.getCapacityIds())
          .flatMap(valid -> bootcampGateway.save(new Bootcamp(
              command.getName(),
              command.getDescription(),
              command.getLaunchDate(),
              command.getDuration()
            ))
            .flatMap(bootcamp -> Flux
              .fromIterable(command.getCapacityIds())
              .flatMap(capacityId -> capacityGateway.associateCapacity(new CapacityBootcamp(bootcamp.getId().getValue(), capacityId)), MAX_CAPACITIES)
              .collectList()
              .map(capacities -> new CreateBootcampResponse(
                  bootcamp.getId().getValue(),
                  bootcamp.getName().getValue(),
                  bootcamp.getDescription().getValue(),
                  bootcamp.getLaunchDate().getValue(),
                  bootcamp.getDuration().getValue(),
                  capacities
                    .stream()
                    .map(this::toCapacityResponse)
                    .toList()
                )
              )
            )
          );
      });
  }

  private CapacityResponse toCapacityResponse(Capacity capacity) {
    return new CapacityResponse(
      capacity.getId().getValue(),
      capacity.getName().getValue(),
      capacity.getDescription().getValue()
    );
  }

  private Boolean isWrongMinQuantityCapacities(List<Long> capacityIds) {
    return capacityIds.isEmpty();
  }

  private Boolean isWrongMaxQuantityCapacities(List<Long> capacityIds) {
    return capacityIds.size() > MAX_CAPACITIES;
  }

  private Boolean existsDuplicatedCapacities(List<Long> capacityIds) {
    return capacityIds.stream().distinct().count() != capacityIds.size();
  }

  private Mono<Boolean> validateCapacitiesExisting(List<Long> capacityIds) {
    return capacityGateway.findAllCapacities()
      .map(Id::getValue)
      .collectList()
      .map(existingIds -> new HashSet<>(existingIds).containsAll(capacityIds))
      .flatMap(allExist -> Boolean.TRUE.equals(allExist) ? Mono.just(true) : Mono.error(new BussinessException(CAPACITY_NOT_FOUND_MESSAGE)));
  }
}
