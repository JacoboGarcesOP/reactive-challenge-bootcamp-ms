package co.com.bancolombia.consumer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class CapacityResponse {
  private Long capacityId;
  private String name;
  private String description;
}


