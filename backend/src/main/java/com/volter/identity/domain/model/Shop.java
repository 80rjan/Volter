package com.volter.identity.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(schema = "public")
public class Shop {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @NotBlank(message = "Shop name is required")
    private String name;

    @NotBlank(message = "Shop schema name is required")
    private String schemaName;

    @ManyToMany(mappedBy = "assignedShops")
    List<IdentityUser> users;
}
