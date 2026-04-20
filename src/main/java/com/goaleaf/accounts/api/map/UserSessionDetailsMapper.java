package com.goaleaf.accounts.api.map;

import com.goaleaf.accounts.api.dto.user.UserSessionDetailsDto;
import com.goaleaf.accounts.infrastructure.persistence.entity.UserSessionDetailsEntity;

/**
 * A utility class for mapping {@link UserSessionDetailsEntity} entities to their corresponding
 * {@link UserSessionDetailsDto} data transfer object representations.
 * <p>
 * This class provides a stateless method to map fields from the {@link UserSessionDetailsEntity}
 * entity to the {@link UserSessionDetailsDto}, ensuring that session-related information is
 * transferred in a structured format for use in other layers of the application.
 * <p>
 * This class is final and cannot be extended.
 */
public final class UserSessionDetailsMapper {

    /**
     * Maps a {@link UserSessionDetailsEntity} entity to a {@link UserSessionDetailsDto} data transfer object.
     * This method transforms the key properties of the given entity into a corresponding DTO,
     * ensuring that relevant session details are transferred in a structured format.
     *
     * @param aUserSessionDetailsEntity
     *         the {@code UserSessionDetails} entity to be mapped; may be {@code null}.
     *         If {@code null}, the method will return {@code null}.
     * @return a {@code UserSessionDetailsDto} built from the provided {@code UserSessionDetails} entity,
     * or {@code null} if the input entity is {@code null}.
     */
    public static UserSessionDetailsDto mapToDto( UserSessionDetailsEntity aUserSessionDetailsEntity ) {
        if ( aUserSessionDetailsEntity == null ) {
            return null;
        }

        return UserSessionDetailsDto.builder()
                .sessionId( aUserSessionDetailsEntity.getSessionId() )
                .authenticatedUserId( aUserSessionDetailsEntity.getAuthenticatedUserId() )
                .refreshToken( aUserSessionDetailsEntity.getRefreshToken() )
                .location( aUserSessionDetailsEntity.getLocation() )
                .device( aUserSessionDetailsEntity.getDevice() )
                .build();
    }
}
