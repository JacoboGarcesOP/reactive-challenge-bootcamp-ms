package co.com.bancolombia.model.bootcamp.values;

import co.com.bancolombia.model.bootcamp.exception.DomainException;
import java.time.LocalDate;

public class LaunchDate {
  private static final String NULL_DATE_ERROR_MESSAGE = "The launch date cannot be null.";
  private static final String MIN_DATE_ERROR_MESSAGE = "The launch date must be greater than today.";

  private final LocalDate value;

  public LaunchDate(final LocalDate value) {
    if (value == null) {
      throw new DomainException(NULL_DATE_ERROR_MESSAGE);
    }

    if (!value.isAfter(LocalDate.now())) {
      throw new DomainException(MIN_DATE_ERROR_MESSAGE);
    }

    this.value = value;
  }

  public LocalDate getValue() {
    return value;
  }
}
