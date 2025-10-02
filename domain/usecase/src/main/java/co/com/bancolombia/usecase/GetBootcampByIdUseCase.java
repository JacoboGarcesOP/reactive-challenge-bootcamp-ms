package co.com.bancolombia.usecase;

import co.com.bancolombia.model.bootcamp.gateway.BootcampGateway;
import co.com.bancolombia.model.bootcamp.gateway.CapacityGateway;
import co.com.bancolombia.usecase.exception.BussinessException;
import co.com.bancolombia.usecase.response.BootcampsWithTechnologiesResponse;
import co.com.bancolombia.usecase.response.GetCapacityResponse;
import co.com.bancolombia.usecase.response.TechnologyResponse;
import reactor.core.publisher.Mono;

import java.util.List;

public class GetBootcampByIdUseCase {
  private static final String BOOTCAMP_NOT_FOUND = "bootcamp not found";

  private final BootcampGateway bootcampGateway;
  private final CapacityGateway capacityGateway;

  public GetBootcampByIdUseCase(BootcampGateway bootcampGateway, CapacityGateway capacityGateway) {
    this.bootcampGateway = bootcampGateway;
    this.capacityGateway = capacityGateway;
  }

  public Mono<BootcampsWithTechnologiesResponse> execute(Long bootcampId) {
    return bootcampGateway.findById(bootcampId)
      .switchIfEmpty(Mono.error(new BussinessException(BOOTCAMP_NOT_FOUND)))
      .flatMap(bootcamp -> capacityGateway
        .findByBootcampId(bootcamp.getId().getValue())
        .collectList()
        .map(capacities -> new BootcampsWithTechnologiesResponse(
            bootcamp.getId().getValue(),
            bootcamp.getName().getValue(),
            bootcamp.getDescription().getValue(),
            bootcamp.getLaunchDate().getValue(),
            bootcamp.getDuration().getValue(),
            capacities
              .stream()
              .map(capacity -> new GetCapacityResponse(
                capacity.getId().getValue(),
                capacity.getName().getValue(),
                capacity.getDescription().getValue(),
                capacity.getTechnologies() != null ?
                  capacity.getTechnologies()
                    .stream()
                    .map(technology -> new TechnologyResponse(
                      technology.getId().getValue(),
                      technology.getName().getValue(),
                      technology.getDescription().getValue()
                    ))
                    .toList() : List.of()
              ))
              .toList()
          )
        )
      );
  }
}
