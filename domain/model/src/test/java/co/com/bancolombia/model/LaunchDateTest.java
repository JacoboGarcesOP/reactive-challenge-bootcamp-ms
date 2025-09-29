package co.com.bancolombia.model;

import co.com.bancolombia.model.bootcamp.exception.DomainException;
import co.com.bancolombia.model.bootcamp.values.LaunchDate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

class LaunchDateTest {

  @Test
  void shouldThrowWhenNull() {
    Assertions.assertThrows(DomainException.class, () -> new LaunchDate(null));
  }

  @Test
  void shouldThrowWhenToday() {
    Assertions.assertThrows(DomainException.class, () -> new LaunchDate(LocalDate.now()));
  }

  @Test
  void shouldThrowWhenBeforeToday() {
    Assertions.assertThrows(DomainException.class, () -> new LaunchDate(LocalDate.now().minusDays(1)));
  }

  @Test
  void shouldCreateWhenAfterToday() {
    LocalDate date = LocalDate.now().plusDays(1);
    LaunchDate launchDate = new LaunchDate(date);
    Assertions.assertEquals(date, launchDate.getValue());
  }
}


