package com.goaleaf.accounts.persistence.entity;

import com.github.pplociennik.commons.persistence.ModifiableDataEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.proxy.HibernateProxy;

import java.util.Objects;
import java.util.UUID;

/**
 * An entity describing the USERS table in the database.
 *
 * @author Created by: Pplociennik at 10.03.2024 17:07
 */
@Table( name = "USER_DETAILS" )
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class UserDetails extends ModifiableDataEntity {

    @Id
    @UuidGenerator
    @Column( name = "ID", nullable = false, unique = true, updatable = false )
    private UUID id;

    @Column( name = "USER_ID", nullable = false, unique = true, updatable = false )
    private String userId;

    @Column( name = "NAME" )
    private String userName;

    @Column( name = "EMAIL" )
    private String emailAddress;

    @Column( name = "DESCRIPTION" )
    private String description;

    @Override
    public final boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( o == null ) return false;
        Class< ? > oEffectiveClass = o instanceof HibernateProxy ? ( ( HibernateProxy ) o ).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class< ? > thisEffectiveClass = this instanceof HibernateProxy ? ( ( HibernateProxy ) this ).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if ( thisEffectiveClass != oEffectiveClass ) return false;
        UserDetails that = ( UserDetails ) o;
        return getId() != null && Objects.equals( getId(), that.getId() );
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ( ( HibernateProxy ) this ).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
