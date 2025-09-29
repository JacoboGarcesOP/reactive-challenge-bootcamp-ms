package co.com.bancolombia.usecase.response;

import java.time.LocalDate;
import java.util.List;

public class BootcampsWithTechnologiesResponse extends BootcampResponse {
  private final List<GetCapacityResponse> capacities;

  public BootcampsWithTechnologiesResponse(Long bootcampId, String name, String description, LocalDate launchDate, Integer duration, List<GetCapacityResponse> capacities) {
    super(bootcampId, name, description, launchDate, duration);
    this.capacities = capacities;
  }

  public List<GetCapacityResponse> getCapacities() {
    return capacities;
  }
}
