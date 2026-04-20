package com.goaleaf.accounts.infrastructure.session.adapter;

import com.goaleaf.accounts.domain.session.model.UserSessionDetails;
import com.goaleaf.accounts.infrastructure.persistence.entity.UserSessionDetailsEntity;

/**
 * A mapper providing conversions between the {@link UserSessionDetails} domain object and {@link UserSessionDetailsEntity}.
 *
 * @author Created by: Pplociennik at 20.04.2026 20:20
 */
class UserSessionDetailsPersistenceMapper {

    /**
     * Returns a {@link UserSessionDetails} domain object mapped from the specified {@link UserSessionDetailsEntity}.
     *
     * @param aEntity
     *         the entity to be mapped.
     * @return the {@link UserSessionDetails} domain object or null if the parameter is null.
     */
    static UserSessionDetails mapToDomain( UserSessionDetailsEntity aEntity ) {
        if ( aEntity == null ) {
            return null;
        }
        return UserSessionDetails.builder()
                .id( aEntity.getId() )
                .sessionId( aEntity.getSessionId() )
                .refreshToken( aEntity.getRefreshToken() )
                .authenticatedUserId( aEntity.getAuthenticatedUserId() )
                .location( aEntity.getLocation() )
                .device( aEntity.getDevice() )
                .build();
    }
}
