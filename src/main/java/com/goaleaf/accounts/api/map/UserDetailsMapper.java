package com.goaleaf.accounts.api.map;

import com.goaleaf.accounts.api.dto.auth.RegistrationRequestDto;
import com.goaleaf.accounts.api.dto.keycloak.AccountDto;
import com.goaleaf.accounts.api.dto.user.UserDetailsDto;
import com.goaleaf.accounts.domain.user.model.UserDetails;
import com.goaleaf.accounts.domain.user.model.map.UserDetailsDomainMapper;

/**
 * A mapper for the {@link UserDetails} domain object and related data transfer objects.
 *
 * @author Created by: Pplociennik at 14.03.2024 19:07
 */
public class UserDetailsMapper {

    /**
     * Returns a {@link UserDetailsDto} mapped from the specified {@link UserDetails} domain object.
     *
     * @param aUserDetails
     *         the domain object to be mapped.
     * @return an object of the type {@link UserDetailsDto} or null if the parameter is null.
     */
    public static UserDetailsDto mapToDto( UserDetails aUserDetails ) {
        return UserDetailsDomainMapper.mapToDto( aUserDetails );
    }

    /**
     * Returns a {@link UserDetails} domain object mapped from the specified data transfer object.
     *
     * @param aUserDetailsDto
     *         the data transfer object to be mapped.
     * @return an object of the type {@link UserDetails} or null if the parameter is null.
     */
    public static UserDetails mapToDomain( UserDetailsDto aUserDetailsDto ) {
        return UserDetailsDomainMapper.mapToDomain( aUserDetailsDto );
    }

    /**
     * Returns a {@link UserDetails} domain object constructed from the account and registration request data.
     *
     * @param aAccountDto
     *         the account data transfer object retrieved from the identity provider.
     * @param aRequestDto
     *         the registration request data transfer object.
     * @return a {@link UserDetails} domain object or null if {@code aAccountDto} is null.
     */
    public static UserDetails mapToDomain( AccountDto aAccountDto, RegistrationRequestDto aRequestDto ) {
        if ( aAccountDto == null ) {
            return null;
        }

        return UserDetails.builder()
                .userId( aAccountDto.getId() )
                .userName( aRequestDto.getUsername() )
                .emailAddress( aAccountDto.getEmail() )
                .build();
    }
}
