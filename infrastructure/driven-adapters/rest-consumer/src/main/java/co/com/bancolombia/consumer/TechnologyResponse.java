package co.com.bancolombia.consumer;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class TechnologyResponse {
  private Long technologyId;
  private String name;
  private String description;
}