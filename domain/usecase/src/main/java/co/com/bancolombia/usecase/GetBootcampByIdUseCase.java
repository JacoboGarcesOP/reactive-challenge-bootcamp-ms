package co.com.bancolombia.usecase;

import co.com.bancolombia.model.bootcamp.gateway.BootcampGateway;
import co.com.bancolombia.usecase.exception.BussinessException;
import co.com.bancolombia.usecase.response.BootcampResponse;
import reactor.core.publisher.Mono;

public class GetBootcampByIdUseCase {
  private static final String BOOTCAMP_NOT_FOUND = "bootcamp not found";
  
  private final BootcampGateway bootcampGateway;

  public GetBootcampByIdUseCase(BootcampGateway bootcampGateway) {
    this.bootcampGateway = bootcampGateway;
  }

  public Mono<BootcampResponse> execute(Long bootcampId) {
    return bootcampGateway.findById(bootcampId)
      .switchIfEmpty(Mono.error(new BussinessException(BOOTCAMP_NOT_FOUND)))
      .map(bootcamp -> new BootcampResponse(
          bootcamp.getId().getValue(),
          bootcamp.getName().getValue(),
          bootcamp.getDescription().getValue(),
          bootcamp.getLaunchDate().getValue(),
          bootcamp.getDuration().getValue()
        )
      );
  }
}
