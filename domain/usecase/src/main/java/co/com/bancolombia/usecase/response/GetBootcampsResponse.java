package co.com.bancolombia.usecase.response;

import java.util.List;

public class GetBootcampsResponse {
  private final List<BootcampResponse> bootcamps;
  private final FilterResponse filter;

  public GetBootcampsResponse(List<BootcampResponse> bootcamps, FilterResponse filter) {
    this.bootcamps = bootcamps;
    this.filter = filter;
  }

  public List<BootcampResponse> getBootcamps() {
    return bootcamps;
  }

  public FilterResponse getFilter() {
    return filter;
  }
}


