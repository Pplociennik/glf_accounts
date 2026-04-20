package com.goaleaf.accounts.infrastructure.user;

import com.github.pplociennik.commons.exc.resources.ResourceNotFoundException;
import com.goaleaf.accounts.domain.user.UserDetailsService;
import com.goaleaf.accounts.domain.user.model.UserDetails;
import com.goaleaf.accounts.domain.user.port.UserDetailsRepository;
import lombok.AllArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

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
     * A repository used for performing CRUD operations on {@link UserDetails} domain objects.
     */
    private final UserDetailsRepository userDetailsRepository;

    /**
     * Creates a new user basing on the specified data.
     *
     * @param aUserDetails
     *         the user data.
     * @return the created user.
     */
    @Override
    public UserDetails createUserDetails( @NonNull UserDetails aUserDetails ) {
        requireNonNull( aUserDetails );
        return userDetailsRepository.save( aUserDetails );
    }

    /**
     * Retrieves user details by the provided email address.
     *
     * @param aEmail
     *         the email address of the user whose details are to be retrieved. Must not be null.
     * @return a {@link UserDetails} object containing the user's details.
     *
     * @throws ResourceNotFoundException
     *         if no user is found for the given email address.
     */
    @Override
    public UserDetails findUserDetailsByEmail( @NonNull String aEmail ) {
        requireNonNull( aEmail );
        return userDetailsRepository.findByEmailAddress( aEmail )
                .orElseThrow( () -> new ResourceNotFoundException( UserDetails.class.getSimpleName(), "email", aEmail ) );
    }

    /**
     * Deletes the user details associated with the specified user ID.
     *
     * @param aUserID
     *         the ID of the user whose details are to be deleted. Must not be null or empty.
     */
    @Override
    public void deleteUserDetails( @NonNull String aUserID ) {
        requireNonNull( aUserID );
        userDetailsRepository.deleteByUserId( aUserID );
    }
}
