package co.com.bancolombia.usecase.response;

import java.time.LocalDate;
import java.util.List;

public class CreateBootcampResponse extends BootcampResponse {
  private final List<CapacityResponse> capacities;

  public CreateBootcampResponse(Long bootcampId, String name, String description, LocalDate launchDate, Integer duration, List<CapacityResponse> capacities) {
    super(bootcampId, name, description, launchDate, duration);
    this.capacities = capacities;
  }

  public List<CapacityResponse> getCapacities() {
    return capacities;
  }
}
