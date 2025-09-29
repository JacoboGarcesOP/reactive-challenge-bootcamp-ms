package co.com.bancolombia.r2dbc.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;

@Table(name = "bootcamp", schema = "bootcamp_schema")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BootcampEntity {
  @Id
  @Column("bootcamp_id")
  private Long id;

  @Column("name")
  private String name;

  @Column("description")
  private String description;

  @Column("launch_date")
  private LocalDate launchDate;

  @Column("duration")
  private Integer duration;
}


