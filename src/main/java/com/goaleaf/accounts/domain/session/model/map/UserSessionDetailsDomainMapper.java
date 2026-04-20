package com.goaleaf.accounts.domain.session.model.map;

import com.goaleaf.accounts.api.dto.user.UserSessionDetailsDto;
import com.goaleaf.accounts.domain.session.model.UserSessionDetails;

/**
 * A mapper providing conversions between the {@link UserSessionDetails} domain object and related data transfer objects.
 *
 * @author Created by: Pplociennik at 20.04.2026 20:17
 */
public class UserSessionDetailsDomainMapper {

    /**
     * Returns a {@link UserSessionDetails} domain object mapped from the specified data transfer object.
     *
     * @param aDto
     *         the data transfer object to be mapped.
     * @return the {@link UserSessionDetails} domain object or null if the parameter is null.
     */
    public static UserSessionDetails mapToDomain( UserSessionDetailsDto aDto ) {
        if ( aDto == null ) {
            return null;
        }

        return UserSessionDetails.builder()
                .sessionId( aDto.getSessionId() )
                .refreshToken( aDto.getRefreshToken() )
                .authenticatedUserId( aDto.getAuthenticatedUserId() )
                .location( aDto.getLocation() )
                .device( aDto.getDevice() )
                .build();
    }

    /**
     * Returns a {@link UserSessionDetailsDto} mapped from the specified {@link UserSessionDetails} domain object.
     *
     * @param aUserSessionDetails
     *         the domain object to be mapped.
     * @return the {@link UserSessionDetailsDto} or null if the parameter is null.
     */
    public static UserSessionDetailsDto mapToDto( UserSessionDetails aUserSessionDetails ) {
        if ( aUserSessionDetails == null ) {
            return null;
        }

        return UserSessionDetailsDto.builder()
                .sessionId( aUserSessionDetails.getSessionId() )
                .refreshToken( aUserSessionDetails.getRefreshToken() )
                .authenticatedUserId( aUserSessionDetails.getAuthenticatedUserId() )
                .location( aUserSessionDetails.getLocation() )
                .device( aUserSessionDetails.getDevice() )
                .build();
    }
}
