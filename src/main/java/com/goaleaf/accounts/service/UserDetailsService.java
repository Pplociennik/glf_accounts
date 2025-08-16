package com.goaleaf.accounts.service;

import com.github.pplociennik.commons.exc.resources.ResourceNotFoundException;
import com.goaleaf.accounts.data.dto.user.UserDetailsDto;
import org.springframework.lang.NonNull;

/**
 * A service providing functions related to managing users.
 *
 * @author Created by: Pplociennik at 19.03.2024 18:08
 */
public interface UserDetailsService {

    /**
     * Creates a new user basing on the specified data.
     *
     * @param aUserDetailsDto
     *         the user data.
     * @return the created user.
     */
    UserDetailsDto createUserDetails( @NonNull UserDetailsDto aUserDetailsDto );

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
    UserDetailsDto findUserDetailsByEmail( @NonNull String aEmail );

}
