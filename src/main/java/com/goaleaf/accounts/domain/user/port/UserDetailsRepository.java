package com.goaleaf.accounts.domain.user.port;

import com.goaleaf.accounts.domain.user.model.UserDetails;
import org.springframework.lang.NonNull;

import java.util.Optional;

/**
 * A port defining persistence operations for {@link UserDetails} domain objects.
 *
 * @author Created by: Pplociennik at 20.04.2026 20:19
 */
public interface UserDetailsRepository {

    /**
     * Returns the optional {@link UserDetails} with the specified email address.
     *
     * @param aEmailAddress
     *         an email address of the user being searched.
     * @return the {@link Optional} containing the {@link UserDetails} with the specified email address or the empty one otherwise.
     */
    Optional<UserDetails> findByEmailAddress( String aEmailAddress );

    /**
     * Returns the optional {@link UserDetails} with the specified identifier.
     *
     * @param aUserId
     *         an identifier of the user being searched.
     * @return the {@link Optional} containing the {@link UserDetails} with the specified identifier or the empty one otherwise.
     */
    Optional<UserDetails> findByUserId( String aUserId );

    /**
     * Saves the specified {@link UserDetails} and returns the persisted result.
     *
     * @param aUserDetails
     *         the user details to be saved.
     * @return the saved {@link UserDetails}.
     */
    UserDetails save( @NonNull UserDetails aUserDetails );

    /**
     * Deletes the specified {@link UserDetails}.
     *
     * @param aUserDetails
     *         the user details to be deleted.
     */
    void delete( @NonNull UserDetails aUserDetails );

    /**
     * Deletes the user details associated with the specified user identifier.
     *
     * @param aUserId
     *         the identifier of the user whose details are to be deleted.
     */
    void deleteByUserId( String aUserId );
}
