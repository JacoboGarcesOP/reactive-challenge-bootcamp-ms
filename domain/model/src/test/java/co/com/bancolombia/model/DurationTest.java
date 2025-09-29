package co.com.bancolombia.model;

import co.com.bancolombia.model.bootcamp.exception.DomainException;
import co.com.bancolombia.model.bootcamp.values.Duration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class DurationTest {

  @Test
  void shouldThrowWhenNull() {
    Assertions.assertThrows(DomainException.class, () -> new Duration(null));
  }

  @Test
  void shouldThrowWhenZero() {
    Assertions.assertThrows(DomainException.class, () -> new Duration(0));
  }

  @Test
  void shouldThrowWhenNegative() {
    Assertions.assertThrows(DomainException.class, () -> new Duration(-1));
  }

  @Test
  void shouldCreateWhenPositive() {
    Duration d = new Duration(10);
    Assertions.assertEquals(10, d.getValue());
  }
}


