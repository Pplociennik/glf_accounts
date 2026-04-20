package com.goaleaf.accounts.api.map;

import com.goaleaf.accounts.api.dto.user.UserSessionDetailsDto;
import com.goaleaf.accounts.domain.session.model.UserSessionDetails;
import com.goaleaf.accounts.domain.session.model.map.UserSessionDetailsDomainMapper;

/**
 * A mapper for the {@link UserSessionDetails} domain object and related data transfer objects.
 *
 * @author Created by: Pplociennik at 01.04.2025 20:37
 */
public final class UserSessionDetailsMapper {

    /**
     * Returns a {@link UserSessionDetailsDto} mapped from the specified {@link UserSessionDetails} domain object.
     *
     * @param aUserSessionDetails
     *         the domain object to be mapped.
     * @return an object of the type {@link UserSessionDetailsDto} or null if the parameter is null.
     */
    public static UserSessionDetailsDto mapToDto( UserSessionDetails aUserSessionDetails ) {
        return UserSessionDetailsDomainMapper.mapToDto( aUserSessionDetails );
    }

    /**
     * Returns a {@link UserSessionDetails} domain object mapped from the specified data transfer object.
     *
     * @param aDto
     *         the data transfer object to be mapped.
     * @return an object of the type {@link UserSessionDetails} or null if the parameter is null.
     */
    public static UserSessionDetails mapToDomain( UserSessionDetailsDto aDto ) {
        return UserSessionDetailsDomainMapper.mapToDomain( aDto );
    }
}
