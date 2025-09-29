package co.com.bancolombia.api.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public class CreateBootcampRequest {
  @NotBlank
  private String name;

  @NotBlank
  private String description;

  @NotNull
  @Future
  private LocalDate launchDate;

  @NotNull
  private Integer duration;

  @NotEmpty
  private List<Long> capacityIds;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public LocalDate getLaunchDate() {
    return launchDate;
  }

  public void setLaunchDate(LocalDate launchDate) {
    this.launchDate = launchDate;
  }

  public Integer getDuration() {
    return duration;
  }

  public void setDuration(Integer duration) {
    this.duration = duration;
  }

  public List<Long> getCapacityIds() {
    return capacityIds;
  }

  public void setCapacityIds(List<Long> capacityIds) {
    this.capacityIds = capacityIds;
  }
}


