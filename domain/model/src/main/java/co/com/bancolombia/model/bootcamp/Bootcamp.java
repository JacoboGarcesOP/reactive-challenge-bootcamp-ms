package co.com.bancolombia.model.bootcamp;

import co.com.bancolombia.model.bootcamp.values.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class Bootcamp {
  private final String CAPACITIES_NULL_ERROR_MESSAGE = "The capacities list cannot be null.";
  private Id id;
  private Name name;
  private Description description;
  private LaunchDate launchDate;
  private Duration duration;
  private List<Capacity> capacities;

  public Bootcamp(Id id, Name name, Description description, LaunchDate launchDate, Duration duration, List<Capacity> capacities) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.launchDate = launchDate;
    this.duration = duration;
    this.capacities = capacities;
  }

  public Bootcamp(Long id, String name, String description, LocalDate launchDate, Integer duration, List<Capacity> capacities) {
    this.id = new Id(id);
    this.name = new Name(name);
    this.description = new Description(description);
    this.launchDate = new LaunchDate(launchDate);
    this.duration = new Duration(duration);
    this.capacities = Objects.requireNonNull(capacities, CAPACITIES_NULL_ERROR_MESSAGE);
  }

  public Bootcamp(Long id, String name, String description, LocalDate launchDate, Integer duration) {
    this.id = new Id(id);
    this.name = new Name(name);
    this.description = new Description(description);
    this.launchDate = new LaunchDate(launchDate);
    this.duration = new Duration(duration);
  }

  public Bootcamp(String name, String description, LocalDate launchDate, Integer duration, List<Capacity> capacities) {
    this.name = new Name(name);
    this.description = new Description(description);
    this.launchDate = new LaunchDate(launchDate);
    this.duration = new Duration(duration);
    this.capacities = capacities;
  }

  public Bootcamp(String name, String description, LocalDate launchDate, Integer duration) {
    this.name = new Name(name);
    this.description = new Description(description);
    this.launchDate = new LaunchDate(launchDate);
    this.duration = new Duration(duration);
  }

  public Id getId() {
    return id;
  }

  public void setId(Id id) {
    this.id = id;
  }

  public Name getName() {
    return name;
  }

  public void setName(Name name) {
    this.name = name;
  }

  public Description getDescription() {
    return description;
  }

  public void setDescription(Description description) {
    this.description = description;
  }

  public LaunchDate getLaunchDate() {
    return launchDate;
  }

  public void setLaunchDate(LaunchDate launchDate) {
    this.launchDate = launchDate;
  }

  public Duration getDuration() {
    return duration;
  }

  public void setDuration(Duration duration) {
    this.duration = duration;
  }

  public List<Capacity> getCapacities() {
    return capacities;
  }

  public void setCapacities(List<Capacity> capacities) {
    this.capacities = capacities;
  }
}
