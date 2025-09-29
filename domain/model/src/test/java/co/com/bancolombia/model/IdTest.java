package co.com.bancolombia.model;

import co.com.bancolombia.model.bootcamp.exception.DomainException;
import co.com.bancolombia.model.bootcamp.values.Id;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class IdTest {

  @Test
  void shouldThrowWhenNull() {
    Assertions.assertThrows(DomainException.class, () -> new Id(null));
  }

  @Test
  void shouldAcceptValid() {
    Id id = new Id(5L);
    Assertions.assertEquals(5L, id.getValue());
  }
}


