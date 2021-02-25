package icu.ngte.model;

import javax.persistence.*;
import lombok.Data;

@Data
@Entity
public class Role {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long id;

  @Column private String name;

  @Column private String description;
}
