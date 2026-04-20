package com.goaleaf.accounts.domain.session;

import com.goaleaf.accounts.domain.auth.model.AuthenticationToken;
import com.goaleaf.accounts.domain.session.model.UserSessionDetails;
import com.goaleaf.accounts.domain.session.model.UserSessionInfo;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;

/**
 * A service interface for managing user session details. Implementations of this interface provide functionality
 * for creating, retrieving, and managing user sessions, including the generation of session information based on
 * authentication tokens.
 *
 * @author Created by: Pplociennik at 01.04.2025 20:32
 * @since 1.0
 */
public interface UserSessionDetailsService {

    /**
     * Creates user session details using the provided session context and authentication token.
     * The session context supplies location and device information; the token supplies
     * user identity and the refresh token to be stored.
     *
     * @param aSessionContext
     *         a non-null {@link UserSessionDetails} carrying at least location and device information.
     * @param aAuthenticationToken
     *         a non-null {@link AuthenticationToken} containing the new token information.
     * @return a {@link UserSessionDetails} containing the newly created session details.
     */
    UserSessionDetails createUserSessionDetails( @NonNull UserSessionDetails aSessionContext, @NonNull AuthenticationToken aAuthenticationToken );

    /**
     * Retrieves publicly visible information about all sessions associated with the specified user access token.
     *
     * @param aAccessToken
     *         a non-null string representing the user's access token.
     * @return a list of {@link UserSessionInfo} objects describing each active session.
     */
    List<UserSessionInfo> getAllUserSessionsInfo( @NonNull String aAccessToken );

    /**
     * Validates the provided access token to ensure it is active and authorized for use.
     *
     * @param aAccessToken
     *         a non-null {@code String} representing the access token to be validated.
     * @return {@code true} if the access token is valid and active; {@code false} otherwise.
     */
    boolean checkAccessToken( @NonNull String aAccessToken );

    /**
     * Retrieves the session details of a user associated with the specified session ID.
     *
     * @param aSessionId
     *         a non-null {@code String} representing the unique identifier of the user session.
     * @return an {@link Optional} containing a {@link UserSessionDetails} if the session details are found,
     * or an empty {@link Optional} if no matching session details exist.
     */
    Optional<UserSessionDetails> getUserSessionDetails( @NonNull String aSessionId );

    /**
     * Deletes the specified user session details from the system.
     *
     * @param aSessionDetails
     *         the {@link UserSessionDetails} object representing the session to be removed; must not be null.
     */
    void deleteSessionDetails( @NonNull UserSessionDetails aSessionDetails );

    /**
     * Deletes the specified session details identified by the session ID from the system.
     *
     * @param aSessionId
     *         the unique identifier of the session to be deleted; must not be null.
     */
    void deleteSessionDetails( @NonNull String aSessionId );

    /**
     * Updates the specified user session details with a new authentication token.
     *
     * @param aSessionDetails
     *         the {@link UserSessionDetails} object containing the current session information; must not be null.
     * @param aAuthenticationToken
     *         the {@link AuthenticationToken} containing the new token details; must not be null.
     */
    void updateSessionDetails( @NonNull UserSessionDetails aSessionDetails, @NonNull AuthenticationToken aAuthenticationToken );
}
