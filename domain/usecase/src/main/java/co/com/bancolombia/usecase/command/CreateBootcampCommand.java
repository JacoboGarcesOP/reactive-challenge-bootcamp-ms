package co.com.bancolombia.usecase.command;

import java.time.LocalDate;
import java.util.List;

public class CreateBootcampCommand {
  private final String name;
  private final String description;
  private final LocalDate launchDate;
  private final Integer duration;
  private final List<Long> capacityIds;

  public CreateBootcampCommand(String name, String description, LocalDate launchDate, Integer duration, List<Long> capacityIds) {
    this.name = name;
    this.description = description;
    this.launchDate = launchDate;
    this.duration = duration;
    this.capacityIds = capacityIds;
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

  public List<Long> getCapacityIds() {
    return capacityIds;
  }
}
