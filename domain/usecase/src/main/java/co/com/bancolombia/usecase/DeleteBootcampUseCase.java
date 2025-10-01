package co.com.bancolombia.usecase;

import co.com.bancolombia.model.bootcamp.gateway.BootcampGateway;
import co.com.bancolombia.model.bootcamp.gateway.CapacityGateway;
import co.com.bancolombia.usecase.exception.BussinessException;
import co.com.bancolombia.usecase.response.DeleteBootcampResponse;
import reactor.core.publisher.Mono;

public class DeleteBootcampUseCase {
  private static final String BOOTCAMP_ID_REQUIRED = "bootcampId is required";
  private static final String BOOTCAMP_NOT_FOUND = "bootcamp not found";

  private final BootcampGateway bootcampGateway;
  private final CapacityGateway capacityGateway;

  public DeleteBootcampUseCase(BootcampGateway bootcampGateway, CapacityGateway capacityGateway) {
    this.bootcampGateway = bootcampGateway;
    this.capacityGateway = capacityGateway;
  }

  public Mono<DeleteBootcampResponse> execute(Long bootcampId) {
    if (bootcampId == null) {
      return Mono.error(new BussinessException(BOOTCAMP_ID_REQUIRED));
    }

    return bootcampGateway.existsById(bootcampId)
      .flatMap(exists -> Boolean.TRUE.equals(exists)
        ? capacityGateway.deleteByBootcampId(bootcampId)
          .map(id -> id.getValue())
          .collectList()
          .flatMap(deletedIds -> bootcampGateway.deleteById(bootcampId)
            .thenReturn(new DeleteBootcampResponse(bootcampId, deletedIds)))
        : Mono.error(new BussinessException(BOOTCAMP_NOT_FOUND))
      );
  }
}

 