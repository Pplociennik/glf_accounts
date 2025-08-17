package com.goaleaf.accounts.persistence.entity;

import com.github.pplociennik.commons.persistence.BaseDataEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
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

    private UserSessionDetails( Builder builder ) {
        super();
        this.id = builder.id;
        this.sessionId = builder.sessionId;
        this.refreshToken = builder.refreshToken;
        this.authenticatedUserId = builder.authenticatedUserId;
        this.location = builder.location;
        this.device = builder.device;

        setCreatedAt( builder.createdAt );
        setCreatedBy( builder.createdBy );
    }

    /**
     * Creates a new builder object.
     *
     * @return a new builder.
     */
    public static Builder builder() {
        return new Builder();
    }

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

    public static class Builder {
        private UUID id;
        private String sessionId;
        private String refreshToken;
        private String authenticatedUserId;
        private String location;
        private String device;
        private Instant createdAt;
        private String createdBy;

        private Builder() {
        }

        public Builder id( UUID aId ) {
            this.id = aId;
            return this;
        }

        public Builder sessionId( String aSessionId ) {
            this.sessionId = aSessionId;
            return this;
        }

        public Builder refreshToken( String aRefreshToken ) {
            this.refreshToken = aRefreshToken;
            return this;
        }

        public Builder authenticatedUserId( String aAuthenticatedUserId ) {
            this.authenticatedUserId = aAuthenticatedUserId;
            return this;
        }

        public Builder location( String aLocation ) {
            this.location = aLocation;
            return this;
        }

        public Builder device( String aDevice ) {
            this.device = aDevice;
            return this;
        }

        public Builder createdAt( Instant aCreatedAt ) {
            this.createdAt = aCreatedAt;
            return this;
        }

        public Builder createdBy( String aCreatedBy ) {
            this.createdBy = aCreatedBy;
            return this;
        }

        public UserSessionDetails build() {
            return new UserSessionDetails( this );
        }
    }
}