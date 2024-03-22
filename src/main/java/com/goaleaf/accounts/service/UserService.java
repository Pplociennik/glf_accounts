package com.goaleaf.accounts.service;

import com.goaleaf.accounts.dto.UserDto;
import org.springframework.lang.NonNull;

/**
 * A service providing functions related to managing users.
 *
 * @author Created by: Pplociennik at 19.03.2024 18:08
 */
public interface UserService {

    /**
     * Creates a new user basing on the specified data.
     *
     * @param aUserDto
     *         the user data.
     * @return the created user.
     */
    UserDto createUser( @NonNull UserDto aUserDto );

    /**
     * Returns {@link UserDto} typed object being a representation of the user with the email address specified.
     *
     * @param aEmailAddress
     *         an email address connected to the user.
     * @return the user with the specified email address.
     */
    UserDto fetchUser( @NonNull String aEmailAddress );
}
