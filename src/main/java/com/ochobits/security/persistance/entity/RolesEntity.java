package com.ochobits.security.persistance.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table("roles")
public class RolesEntity {

    private Long Id;
    private RoleEnum roleEnum;
}
