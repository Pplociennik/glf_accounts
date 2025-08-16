package com.goaleaf.accounts.data.map;

import com.goaleaf.accounts.data.dto.user.UserSessionDetailsDto;
import com.goaleaf.accounts.persistence.entity.UserSessionDetails;

/**
 * A utility class for mapping {@link UserSessionDetails} entities to their corresponding
 * {@link UserSessionDetailsDto} data transfer object representations.
 * <p>
 * This class provides a stateless method to map fields from the {@link UserSessionDetails}
 * entity to the {@link UserSessionDetailsDto}, ensuring that session-related information is
 * transferred in a structured format for use in other layers of the application.
 * <p>
 * This class is final and cannot be extended.
 */
public final class UserSessionDetailsMapper {

    /**
     * Maps a {@link UserSessionDetails} entity to a {@link UserSessionDetailsDto} data transfer object.
     * This method transforms the key properties of the given entity into a corresponding DTO,
     * ensuring that relevant session details are transferred in a structured format.
     *
     * @param aUserSessionDetails
     *         the {@code UserSessionDetails} entity to be mapped; may be {@code null}.
     *         If {@code null}, the method will return {@code null}.
     * @return a {@code UserSessionDetailsDto} built from the provided {@code UserSessionDetails} entity,
     * or {@code null} if the input entity is {@code null}.
     */
    public static UserSessionDetailsDto mapToDto( UserSessionDetails aUserSessionDetails ) {
        if ( aUserSessionDetails == null ) {
            return null;
        }

        return UserSessionDetailsDto.builder()
                .sessionId( aUserSessionDetails.getSessionId() )
                .authenticatedUserId( aUserSessionDetails.getAuthenticatedUserId() )
                .refreshToken( aUserSessionDetails.getRefreshToken() )
                .location( aUserSessionDetails.getLocation() )
                .device( aUserSessionDetails.getDevice() )
                .build();
    }
}
