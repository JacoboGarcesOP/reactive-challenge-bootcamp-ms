package co.com.bancolombia.usecase.response;

public class CapacityResponse {
  private final Long capacityId;
  private final String name;
  private final String description;

  public CapacityResponse(Long capacityId, String name, String description) {
    this.capacityId = capacityId;
    this.name = name;
    this.description = description;
  }

  public Long getCapacityId() {
    return capacityId;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }
}
