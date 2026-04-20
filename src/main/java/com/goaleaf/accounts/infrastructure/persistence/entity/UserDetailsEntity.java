package com.goaleaf.accounts.infrastructure.persistence.entity;

import com.github.pplociennik.commons.persistence.ModifiableDataEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.Objects;
import java.util.UUID;

/**
 * An entity describing the USERS table in the database.
 *
 * @author Created by: Pplociennik at 10.03.2024 17:07
 */
@Table( name = "user_details" )
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class UserDetailsEntity extends ModifiableDataEntity {

    @Id
    @UuidGenerator
    @Column( name = "id", nullable = false, unique = true, updatable = false )
    private UUID id;

    @Column( name = "user_id", nullable = false, unique = true, updatable = false )
    private String userId;

    @Column( name = "name" )
    private String userName;

    @Column( name = "email" )
    private String emailAddress;

    @Column( name = "description" )
    private String description;

    @Override
    public boolean equals( Object o ) {
        if ( o == null || getClass() != o.getClass() ) return false;
        UserDetailsEntity that = ( UserDetailsEntity ) o;
        return Objects.equals( id, that.id ) && Objects.equals( userId, that.userId ) && Objects.equals( userName, that.userName ) && Objects.equals( emailAddress, that.emailAddress ) && Objects.equals( description, that.description );
    }

    @Override
    public int hashCode() {
        return Objects.hash( id, userId, userName, emailAddress, description );
    }
}
