package com.goaleaf.accounts.data.map;

import com.goaleaf.accounts.data.dto.user.UserDetailsDto;
import com.goaleaf.accounts.data.dto.keycloak.AccountDto;
import com.goaleaf.accounts.persistence.entity.UserDetails;

/**
 * A mapper for the {@link UserDetails} entity object.
 *
 * @author Created by: Pplociennik at 14.03.2024 19:07
 */
public class UserDetailsMapper {

    /**
     * Returns an object mapped to the {@link UserDetailsDto} type.
     *
     * @param aUserDetails
     *         an entity to be mapped.
     * @return an object of the type {@link UserDetailsDto} or null if th parameter is null.
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

    /**
     * Returns an object mapped to the {@link UserDetails} type.
     *
     * @param aUserDetailsDto
     *         a dto to be mapped.
     * @return an object of the type {@link UserDetails} or null if th parameter is null.
     */
    public static UserDetails mapToEntity( UserDetailsDto aUserDetailsDto ) {
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
     * Returns an object mapped to the {@link UserDetails} type.
     *
     * @param aAccountDto
     *         a dto to be mapped.
     * @return an object of the type {@link UserDetails} or null if th parameter is null.
     */
    public static UserDetailsDto mapToDto( AccountDto aAccountDto ) {
        if ( aAccountDto == null ) {
            return null;
        }

        return UserDetailsDto.builder()
                .userId( aAccountDto.getId() )
                .userName( aAccountDto.getUsername() )
                .emailAddress( aAccountDto.getEmail() )
                .build();
    }
}
