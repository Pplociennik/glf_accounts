package com.goaleaf.accounts.api.map;

import com.goaleaf.accounts.api.dto.response.UserSessionResponseDto;
import com.goaleaf.accounts.domain.session.model.UserSessionInfo;

/**
 * A mapper for the {@link UserSessionInfo} domain object and related data transfer objects.
 *
 * @author Created by: Pplociennik at 20.04.2026 20:17
 */
public class UserSessionInfoMapper {

    /**
     * Returns a {@link UserSessionResponseDto} mapped from the specified {@link UserSessionInfo} domain object.
     *
     * @param aUserSessionInfo
     *         the domain object to be mapped.
     * @return the {@link UserSessionResponseDto} or null if the parameter is null.
     */
    public static UserSessionResponseDto mapToDto( UserSessionInfo aUserSessionInfo ) {
        if ( aUserSessionInfo == null ) {
            return null;
        }

        return UserSessionResponseDto.builder()
                .id( aUserSessionInfo.getId() )
                .ipAddress( aUserSessionInfo.getIpAddress() )
                .start( aUserSessionInfo.getStart() )
                .lastAccess( aUserSessionInfo.getLastAccess() )
                .location( aUserSessionInfo.getLocation() )
                .device( aUserSessionInfo.getDevice() )
                .build();
    }
}
