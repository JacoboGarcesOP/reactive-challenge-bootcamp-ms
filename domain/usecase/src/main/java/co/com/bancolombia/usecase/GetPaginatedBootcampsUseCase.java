package co.com.bancolombia.usecase;

import co.com.bancolombia.model.bootcamp.gateway.BootcampGateway;
import co.com.bancolombia.model.bootcamp.gateway.CapacityGateway;
import co.com.bancolombia.usecase.response.GetBootcampsResponse;
import co.com.bancolombia.usecase.response.BootcampsWithTechnologiesResponse;
import co.com.bancolombia.usecase.response.GetCapacityResponse;
import co.com.bancolombia.usecase.response.TechnologyResponse;
import co.com.bancolombia.usecase.response.FilterResponse;
import reactor.core.publisher.Mono;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class GetPaginatedBootcampsUseCase {
  private final BootcampGateway bootcampGateway;
  private final CapacityGateway capacityGateway;

  public GetPaginatedBootcampsUseCase(BootcampGateway bootcampGateway, CapacityGateway capacityGateway) {
    this.bootcampGateway = bootcampGateway;
    this.capacityGateway = capacityGateway;
  }

  public Mono<GetBootcampsResponse> execute(Integer page, Integer size, String sortBy, String order) {
    if ("capacities".equalsIgnoreCase(sortBy)) {
      return bootcampGateway.findAll()
        .concatMap(bootcamp -> capacityGateway
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
                        technology.getDescription().getValue())
                      ).toList() : List.of()
                )).toList()
            )
          )
        )
        .collectList()
        .map(list -> {
          Comparator<BootcampsWithTechnologiesResponse> comparator = Comparator.comparingInt(b -> b.getCapacities().size());
          if ("desc".equalsIgnoreCase(order)) {
            comparator = comparator.reversed();
          }
          List<BootcampsWithTechnologiesResponse> sorted = list.stream().sorted(comparator).collect(Collectors.toList());
          int from = Math.max(page, 0) * Math.max(size, 0);
          int to = Math.min(sorted.size(), from + Math.max(size, 0));
          List<BootcampsWithTechnologiesResponse> slice = from < to ? sorted.subList(from, to) : List.of();
          return new GetBootcampsResponse(slice, new FilterResponse(page, size, sortBy, order));
        });
    }

    // Default: sort by name handled by the repository (DB level) with stable pagination
    return bootcampGateway.findAllPagedSorted(page, size, sortBy, order)
      .concatMap(bootcamp -> capacityGateway
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
                      technology.getDescription().getValue())
                    ).toList() : List.of()
              )).toList()
          )
        )
      )
      .collectList()
      .map(bootcamps -> new GetBootcampsResponse(bootcamps, new FilterResponse(page, size, sortBy, order)));
  }
}
