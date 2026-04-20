package com.goaleaf.accounts.domain.user.model.map;

import com.goaleaf.accounts.api.dto.user.UserDetailsDto;
import com.goaleaf.accounts.domain.user.model.UserDetails;

/**
 * A mapper providing conversions between the {@link UserDetails} domain object and related data transfer objects.
 *
 * @author Created by: Pplociennik at 20.04.2026 20:17
 */
public class UserDetailsDomainMapper {

    /**
     * Returns a {@link UserDetails} domain object mapped from the specified data transfer object.
     *
     * @param aUserDetailsDto
     *         the data transfer object to be mapped.
     * @return the {@link UserDetails} domain object or null if the parameter is null.
     */
    public static UserDetails mapToDomain( UserDetailsDto aUserDetailsDto ) {
        if ( aUserDetailsDto == null ) {
            return null;
        }

        return UserDetails.builder()
                .userId( aUserDetailsDto.getUserId() )
                .userName( aUserDetailsDto.getUserName() )
                .emailAddress( aUserDetailsDto.getEmailAddress() )
                .description( aUserDetailsDto.getDescription() )
                .build();
    }

    /**
     * Returns a {@link UserDetailsDto} mapped from the specified {@link UserDetails} domain object.
     *
     * @param aUserDetails
     *         the domain object to be mapped.
     * @return the {@link UserDetailsDto} or null if the parameter is null.
     */
    public static UserDetailsDto mapToDto( UserDetails aUserDetails ) {
        if ( aUserDetails == null ) {
            return null;
        }

        return UserDetailsDto.builder()
                .userId( aUserDetails.getUserId() )
                .userName( aUserDetails.getUserName() )
                .emailAddress( aUserDetails.getEmailAddress() )
                .description( aUserDetails.getDescription() )
                .build();
    }
}
