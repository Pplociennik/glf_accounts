package com.goaleaf.accounts.domain.session.port;

import com.goaleaf.accounts.domain.session.model.UserSessionDetails;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;

/**
 * A port defining persistence operations for {@link UserSessionDetails} domain objects.
 *
 * @author Created by: Pplociennik at 20.04.2026 20:19
 */
public interface UserSessionDetailsRepository {

    /**
     * Returns a list of {@link UserSessionDetails} associated with the specified authenticated user identifier.
     *
     * @param aAuthenticatedUserId
     *         the identifier of the authenticated user.
     * @return a list of {@link UserSessionDetails} for the given user, or an empty list if none found.
     */
    List< UserSessionDetails > findByAuthenticatedUserId( @NonNull String aAuthenticatedUserId );

    /**
     * Returns the optional {@link UserSessionDetails} with the specified session identifier.
     *
     * @param aSessionId
     *         the unique identifier of the session.
     * @return the {@link Optional} containing the {@link UserSessionDetails} or the empty one if not found.
     */
    Optional< UserSessionDetails > findBySessionId( @NonNull String aSessionId );

    /**
     * Saves the specified {@link UserSessionDetails} and returns the persisted result.
     *
     * @param aUserSessionDetails
     *         the session details to be saved.
     * @return the saved {@link UserSessionDetails}.
     */
    UserSessionDetails save( @NonNull UserSessionDetails aUserSessionDetails );

    /**
     * Deletes the specified {@link UserSessionDetails}.
     *
     * @param aUserSessionDetails
     *         the session details to be deleted.
     */
    void delete( @NonNull UserSessionDetails aUserSessionDetails );
}
