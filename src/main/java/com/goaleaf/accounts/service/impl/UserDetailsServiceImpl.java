package com.goaleaf.accounts.service.impl;

import com.github.pplociennik.commons.exc.resources.ResourceNotFoundException;
import com.goaleaf.accounts.data.dto.user.UserDetailsDto;
import com.goaleaf.accounts.data.map.UserDetailsMapper;
import com.goaleaf.accounts.persistence.entity.UserDetails;
import com.goaleaf.accounts.persistence.repository.UserDetailsRepository;
import com.goaleaf.accounts.service.UserDetailsService;
import lombok.AllArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

/**
 * An implementation of the {@link UserDetailsService}.
 *
 * @author Created by: Pplociennik at 19.03.2024 18:20
 */
@Service
@AllArgsConstructor
class UserDetailsServiceImpl implements UserDetailsService {

    /**
     * A repository used for performing CRUD operations on {@link UserDetails} entities.
     * Provides methods for interacting with the database to manage user details.
     */
    private UserDetailsRepository userDetailsRepository;

    /**
     * Creates a new user basing on the specified data.
     *
     * @param aUserDetailsDto
     *         the user data.
     * @return the created user.
     */
    @Override
    public UserDetailsDto createUserDetails( @NonNull UserDetailsDto aUserDetailsDto ) {
        requireNonNull( aUserDetailsDto );
        UserDetails userDetailsEntity = UserDetailsMapper.mapToEntity( aUserDetailsDto );
        userDetailsEntity.setCreatedAt( Instant.now() );
        UserDetails savedUserDetails = userDetailsRepository.save( userDetailsEntity );

        return UserDetailsMapper.mapToDto( savedUserDetails );
    }

    /**
     * Retrieves user details by the provided email address.
     *
     * @param aEmail
     *         the email address of the user whose details are to be retrieved. Must not be null.
     * @return a {@link UserDetailsDto} object containing the user's details.
     *
     * @throws ResourceNotFoundException
     *         if no user is found for the given email address.
     * @throws IllegalStateException
     *         if the conversion to {@link UserDetailsDto} fails.
     */
    @Override
    public UserDetailsDto findUserDetailsByEmail( @NonNull String aEmail ) {
        requireNonNull( aEmail );
        Optional< UserDetails > optionalUserDetails = userDetailsRepository.findByEmailAddress( aEmail );

        if ( optionalUserDetails.isEmpty() ) {
            throw new ResourceNotFoundException( UserDetails.class.getSimpleName(), "email", aEmail );
        }

        return optionalUserDetails
                .map( UserDetailsMapper::mapToDto )
                .orElseThrow( () -> new IllegalStateException( "Could not convert to " + UserDetailsDto.class.getSimpleName() ) );
    }
}
