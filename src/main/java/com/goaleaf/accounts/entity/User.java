package com.goaleaf.accounts.entity;

import com.github.pplociennik.commons.persistence.ModifiableDataEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

/**
 * An entity describing the USERS table in the database.
 *
 * @author Created by: Pplociennik at 10.03.2024 17:07
 */
@EqualsAndHashCode( callSuper = true )
@Table( name = "USERS" )
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class User extends ModifiableDataEntity {

    @Id
    @UuidGenerator
    @Column( name = "ID", nullable = false, unique = true, updatable = false )
    private String userId;

    @Column( name = "NAME" )
    private String userName;

    @Column( name = "EMAIL" )
    private String emailAddress;

    @Column( name = "DESCRIPTION" )
    private String description;


}
