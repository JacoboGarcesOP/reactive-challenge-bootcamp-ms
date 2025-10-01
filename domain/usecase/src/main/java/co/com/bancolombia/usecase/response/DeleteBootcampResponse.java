package co.com.bancolombia.usecase.response;

public class DeleteBootcampResponse {
  private final Long bootcampId;
  private final java.util.List<Long> deletedCapacityIds;

  public DeleteBootcampResponse(Long bootcampId, java.util.List<Long> deletedCapacityIds) {
    this.bootcampId = bootcampId;
    this.deletedCapacityIds = deletedCapacityIds;
  }

  public Long getBootcampId() {
    return bootcampId;
  }

  public java.util.List<Long> getDeletedCapacityIds() {
    return deletedCapacityIds;
  }
}

 