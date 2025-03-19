package com.supcarel.spribe.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.NaturalId;

import java.util.UUID;

@Data
@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = {
                "email"
        })
})
@EqualsAndHashCode(callSuper = false)
public class User extends DateAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @NotNull
    @Size(max = 50)
    @Column(name = "first_name")
    private String firstName;

    @NotNull
    @Size(max = 50)
    @Column(name = "last_name")
    private String lastName;

    @NaturalId
    @NotBlank
    @Size(max = 200)
    @Email
    private String email;

    @Size(max = 255)
    private String passwordHash;
}