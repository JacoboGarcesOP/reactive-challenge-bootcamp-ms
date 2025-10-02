package co.com.bancolombia.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BootcampMessage {
  private Long bootcampId;
  private String name;
  private String description;
  private Date launchDate;
  private Integer duration;
  private Integer capacitiesCount;
  private Integer technologiesCount;
}
