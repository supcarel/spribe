package com.supcarel.spribe.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "unit_types")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UnitType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    @Size(max = 50)
    private String name;

    @Column
    @Size(max = 255)
    private String description;
}
