package com.goaleaf.accounts.persistence.entity;

import com.github.pplociennik.commons.persistence.BaseDataEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.Objects;
import java.util.UUID;

/**
 * Represents user session details stored in the "ACCOUNTS_USER_SESSION_DETAILS" table.
 * This entity contains information regarding a user's session, such as session ID, creation time,
 * activity status, and refresh token.
 * <p>
 * The entity is uniquely identified by its ID and is mapped to the "ACCOUNTS_USER_SESSION_DETAILS" table.
 * It provides equality and hash code implementations based on its attributes.
 * <p>
 * Inherits common identifiable entity functionality from BaseIdentifiableDataEntity.
 */
@Getter
@Setter
@NoArgsConstructor
@Builder
@Table( name = "ACCOUNTS_USER_SESSION_DETAILS" )
@Entity
@AllArgsConstructor
public class UserSessionDetails extends BaseDataEntity {

    @Id
    @UuidGenerator
    @Column( name = "ID", nullable = false, unique = true, updatable = false )
    private UUID id;

    @Column( name = "SESSION_ID", nullable = false, updatable = false, unique = true )
    private String sessionId;

    @Column( name = "REFRESH_TOKEN", nullable = false, length = 1000 )
    private String refreshToken;

    @Column( name = "AUTHENTICATED_USER_ID", nullable = false, updatable = false )
    private String authenticatedUserId;

    @Column( name = "LOCATION", nullable = false, updatable = false )
    private String location;

    @Column( name = "DEVICE" )
    private String device;

    @Override
    public boolean equals( Object o ) {
        if ( o == null || getClass() != o.getClass() ) return false;
        UserSessionDetails that = ( UserSessionDetails ) o;
        return Objects.equals( id, that.id ) && Objects.equals( sessionId, that.sessionId ) && Objects.equals( refreshToken, that.refreshToken ) && Objects.equals( authenticatedUserId, that.authenticatedUserId ) && Objects.equals( location, that.location ) && Objects.equals( device, that.device );
    }

    @Override
    public int hashCode() {
        return Objects.hash( id, sessionId, refreshToken, authenticatedUserId, location, device );
    }
}
