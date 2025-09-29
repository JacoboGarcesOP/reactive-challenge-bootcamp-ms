package co.com.bancolombia.model;

import co.com.bancolombia.model.bootcamp.Technology;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TechnologyTest {

  @Test
  void shouldConstructWithIdNameDescription() {
    Technology tech = new Technology(1L, "Java", "Lang");
    Assertions.assertEquals(1L, tech.getId().getValue());
    Assertions.assertEquals("Java", tech.getName().getValue());
    Assertions.assertEquals("Lang", tech.getDescription().getValue());
  }

  @Test
  void shouldConstructWithoutId() {
    Technology tech = new Technology("Node", "JS");
    Assertions.assertNull(tech.getId());
    Assertions.assertEquals("Node", tech.getName().getValue());
    Assertions.assertEquals("JS", tech.getDescription().getValue());
  }
}


