package co.com.bancolombia.model.bootcamp;

import co.com.bancolombia.model.bootcamp.values.Description;
import co.com.bancolombia.model.bootcamp.values.Duration;
import co.com.bancolombia.model.bootcamp.values.Id;
import co.com.bancolombia.model.bootcamp.values.LaunchDate;
import co.com.bancolombia.model.bootcamp.values.Name;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DomainEntitiesTest {

    @Test
    void bootcamp_construct_with_all_values_objects() {
        Bootcamp b = new Bootcamp(new Id(1L), new Name("valid name"), new Description("valid description"), new LaunchDate(LocalDate.now().plusDays(1)), new Duration(5), Collections.emptyList());
        assertThat(b.getName().getValue()).isEqualTo("valid name");
    }

    @Test
    void bootcamp_construct_with_null_capacities_throws() {
        assertThrows(NullPointerException.class, () -> new Bootcamp(1L, "valid name", "valid description", LocalDate.now().plusDays(1), 5, null));
    }

    @Test
    void capacity_construct_with_null_technologies_throws() {
        assertThrows(NullPointerException.class, () -> new Capacity(1L, "n", "d", null));
    }

    @Test
    void setters_and_getters_work() {
        Bootcamp b = new Bootcamp("valid name", "valid description", LocalDate.now().plusDays(1), 5);
        b.setId(new Id(10L));
        b.setName(new Name("x name"));
        b.setDescription(new Description("y desc"));
        b.setLaunchDate(new LaunchDate(LocalDate.now().plusDays(1)));
        b.setDuration(new Duration(7));
        b.setCapacities(Collections.emptyList());

        assertThat(b.getId().getValue()).isEqualTo(10L);
        assertThat(b.getName().getValue()).isEqualTo("x name");
        assertThat(b.getDescription().getValue()).isEqualTo("y desc");
        assertThat(b.getDuration().getValue()).isEqualTo(7);
        assertThat(b.getCapacities()).isEmpty();
    }
}


