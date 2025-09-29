package co.com.bancolombia.model;

import co.com.bancolombia.model.bootcamp.exception.DomainException;
import co.com.bancolombia.model.bootcamp.values.Name;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class NameTest {

  @Test
  void shouldThrowWhenNullOrEmpty() {
    Assertions.assertThrows(DomainException.class, () -> new Name(null));
    Assertions.assertThrows(DomainException.class, () -> new Name("   "));
  }

  @Test
  void shouldThrowWhenTooLong() {
    String longName = "x".repeat(51);
    Assertions.assertThrows(DomainException.class, () -> new Name(longName));
  }

  @Test
  void shouldTrimAndAcceptValid() {
    Name n = new Name("  abc  ");
    Assertions.assertEquals("abc", n.getValue());
  }
}


