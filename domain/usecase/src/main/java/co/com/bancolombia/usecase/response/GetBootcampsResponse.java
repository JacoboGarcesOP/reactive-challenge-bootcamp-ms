package co.com.bancolombia.usecase.response;

import java.util.List;

public class GetBootcampsResponse {
  private final List<BootcampsWithTechnologiesResponse> bootcamps;
  private final FilterResponse filter;

  public GetBootcampsResponse(List<BootcampsWithTechnologiesResponse> bootcamps, FilterResponse filter) {
    this.bootcamps = bootcamps;
    this.filter = filter;
  }

  public List<BootcampsWithTechnologiesResponse> getBootcamps() {
    return bootcamps;
  }

  public FilterResponse getFilter() {
    return filter;
  }
}


