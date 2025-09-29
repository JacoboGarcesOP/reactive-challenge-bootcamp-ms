package co.com.bancolombia.model;

import co.com.bancolombia.model.bootcamp.exception.DomainException;
import co.com.bancolombia.model.bootcamp.values.Description;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class DescriptionTest {

  @Test
  void shouldThrowWhenNullOrEmpty() {
    Assertions.assertThrows(DomainException.class, () -> new Description(null));
    Assertions.assertThrows(DomainException.class, () -> new Description("   "));
  }

  @Test
  void shouldThrowWhenTooLong() {
    String longDesc = "x".repeat(91);
    Assertions.assertThrows(DomainException.class, () -> new Description(longDesc));
  }

  @Test
  void shouldTrimAndAcceptValid() {
    Description d = new Description("  nice description  ");
    Assertions.assertEquals("nice description", d.getValue());
  }
}


