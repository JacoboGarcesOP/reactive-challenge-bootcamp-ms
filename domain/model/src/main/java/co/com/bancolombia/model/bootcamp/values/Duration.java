package co.com.bancolombia.model.bootcamp.values;

import co.com.bancolombia.model.bootcamp.exception.DomainException;

public class Duration {
  private static final String NULL_DURATION_ERROR_MESSAGE = "The duration cannot be null.";
  private static final String MIN_DURATION_ERROR_MESSAGE = "The duration must be greater than zero.";

  private final Integer value;

  public Duration(final Integer value) {
    if (value == null) {
      throw new DomainException(NULL_DURATION_ERROR_MESSAGE);
    }

    if (value <= 0) {
      throw new DomainException(MIN_DURATION_ERROR_MESSAGE);
    }

    this.value = value;
  }

  public Integer getValue() {
    return value;
  }
}
