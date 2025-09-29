package co.com.bancolombia.model;

import co.com.bancolombia.model.bootcamp.CapacityBootcamp;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class CapacityBootcampTest {

  @Test
  void shouldBuildWithIds() {
    CapacityBootcamp cb = new CapacityBootcamp(10L, 20L);
    Assertions.assertEquals(10L, cb.getBootcampId().getValue());
    Assertions.assertEquals(20L, cb.getCapacityId().getValue());
  }
}


