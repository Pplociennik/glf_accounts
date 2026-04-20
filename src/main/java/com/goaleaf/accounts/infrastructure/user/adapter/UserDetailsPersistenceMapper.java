package com.goaleaf.accounts.infrastructure.user.adapter;

import com.goaleaf.accounts.domain.user.model.UserDetails;
import com.goaleaf.accounts.infrastructure.persistence.entity.UserDetailsEntity;

/**
 * A mapper providing conversions between the {@link UserDetails} domain object and {@link UserDetailsEntity}.
 *
 * @author Created by: Pplociennik at 20.04.2026 20:20
 */
class UserDetailsPersistenceMapper {

    /**
     * Returns a {@link UserDetails} domain object mapped from the specified {@link UserDetailsEntity}.
     *
     * @param aEntity
     *         the entity to be mapped.
     * @return the {@link UserDetails} domain object or null if the parameter is null.
     */
    static UserDetails mapToDomain( UserDetailsEntity aEntity ) {
        if ( aEntity == null ) {
            return null;
        }
        return UserDetails.builder()
                .id( aEntity.getId() )
                .userId( aEntity.getUserId() )
                .userName( aEntity.getUserName() )
                .emailAddress( aEntity.getEmailAddress() )
                .description( aEntity.getDescription() )
                .build();
    }

    /**
     * Returns a {@link UserDetailsEntity} mapped from the specified {@link UserDetails} domain object.
     *
     * @param aUserDetails
     *         the domain object to be mapped.
     * @return the {@link UserDetailsEntity} or null if the parameter is null.
     */
    static UserDetailsEntity mapToEntity( UserDetails aUserDetails ) {
        if ( aUserDetails == null ) {
            return null;
        }
        return UserDetailsEntity.builder()
                .id( aUserDetails.getId() )
                .userId( aUserDetails.getUserId() )
                .userName( aUserDetails.getUserName() )
                .emailAddress( aUserDetails.getEmailAddress() )
                .description( aUserDetails.getDescription() )
                .build();
    }
}
