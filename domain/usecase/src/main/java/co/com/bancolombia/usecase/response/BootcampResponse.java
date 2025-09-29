package co.com.bancolombia.usecase.response;

import java.time.LocalDate;
import java.util.List;

public class BootcampResponse {
  private final Long bootcampId;
  private final String name;
  private final String description;
  private final LocalDate launchDate;
  private final Integer duration;

  public BootcampResponse(Long bootcampId, String name, String description, LocalDate launchDate, Integer duration) {
    this.bootcampId = bootcampId;
    this.name = name;
    this.description = description;
    this.launchDate = launchDate;
    this.duration = duration;
  }

  public Long getBootcampId() {
    return bootcampId;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public LocalDate getLaunchDate() {
    return launchDate;
  }

  public Integer getDuration() {
    return duration;
  }
}
