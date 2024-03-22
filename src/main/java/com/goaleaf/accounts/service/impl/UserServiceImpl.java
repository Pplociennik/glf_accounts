package com.goaleaf.accounts.service.impl;

import com.github.pplociennik.commons.exc.ResourceNotFoundException;
import com.goaleaf.accounts.dto.UserDto;
import com.goaleaf.accounts.entity.User;
import com.goaleaf.accounts.map.UserMapper;
import com.goaleaf.accounts.repository.UserRepository;
import com.goaleaf.accounts.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.github.pplociennik.commons.utility.CustomObjects.requireNonEmpty;
import static java.util.Objects.requireNonNull;

/**
 * An implementation of the {@link com.goaleaf.accounts.service.UserService}.
 *
 * @author Created by: Pplociennik at 19.03.2024 18:20
 */
@Service
@AllArgsConstructor
class UserServiceImpl implements UserService {

    private UserRepository userRepository;

    /**
     * Creates a new user basing on the specified data.
     *
     * @param aUserDto
     *         the user data.
     * @return the created user.
     */
    @Override
    public UserDto createUser( @NonNull UserDto aUserDto ) {
        requireNonNull( aUserDto );
        User userEntity = UserMapper.mapToEntity( aUserDto );
        User savedUser = userRepository.save( userEntity );

        return UserMapper.mapToDto( savedUser );
    }

    /**
     * Returns {@link UserDto} typed object being a representation of the user with the email address specified.
     *
     * @param aEmailAddress
     *         an email address connected to the user.
     * @return the user with the specified email address.
     */
    @Override
    public UserDto fetchUser( @NonNull String aEmailAddress ) {
        requireNonEmpty( aEmailAddress );
        Optional< User > optionalUser = userRepository.findByEmailAddress( aEmailAddress );

        return UserMapper.mapToDto(
                optionalUser.orElseThrow(
                        () -> new ResourceNotFoundException( User.class.getName(), "emailAddress", aEmailAddress )
                ) );
    }
}
