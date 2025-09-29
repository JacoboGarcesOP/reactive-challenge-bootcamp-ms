package co.com.bancolombia.usecase.response;

import java.util.List;

public class GetCapacityResponse extends CapacityResponse {
  private final List<TechnologyResponse> technologies;

  public GetCapacityResponse(Long capacityId, String name, String description, List<TechnologyResponse> technologies) {
    super(capacityId, name, description);
    this.technologies = technologies;
  }

  public List<TechnologyResponse> getTechnologies() {
    return technologies;
  }
}
