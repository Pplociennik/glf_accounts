package com.goaleaf.accounts.service;

import com.goaleaf.accounts.data.dto.auth.AuthenticationRequestDto;
import com.goaleaf.accounts.data.dto.auth.RegistrationRequestDto;
import com.goaleaf.accounts.data.dto.response.AuthenticationResponseDto;
import com.goaleaf.accounts.data.dto.response.AuthenticationTokenDto;
import com.goaleaf.accounts.data.dto.user.UserDetailsDto;
import org.springframework.lang.NonNull;

/**
 * A service providing authentication operations.
 *
 * @author Created by: Pplociennik at 19.03.2025 18:53
 */
public interface AuthenticationService {

    /**
     * Registers a new user account using the provided access token and registration details.
     *
     * @param aDto
     *         the data transfer object containing the details necessary for registering the user account.
     */
    UserDetailsDto registerUserAccount( @NonNull RegistrationRequestDto aDto );

    /**
     * Authenticates a user account using the specified access token and authentication request details.
     *
     * @param aDto
     *         the data transfer object containing the user's credentials necessary for authentication.
     * @return an {@code AuthenticationResponseDto} containing the authentication details, such as username, access token,
     * refresh token, token type, and expiration information.
     */
    AuthenticationResponseDto authenticateUserAccount( @NonNull AuthenticationRequestDto aDto );

    /**
     * Terminates all active sessions associated with the user identified by the provided access token.
     *
     * @param aUserAccessToken
     *         a non-null {@code String} that represents the access token of the user whose sessions are to be terminated.
     * @return {@code true} if the user sessions were successfully terminated; {@code false} otherwise.
     */
    boolean terminateAllSessions( @NonNull String aUserAccessToken );

    /**
     * Terminates the current active session for the user identified by the provided access token.
     *
     * @param aUserAccessToken
     *         a non-null {@code String} representing the access token of the user whose current session is to be terminated.
     * @return {@code true} if the current user's session was successfully terminated; {@code false} otherwise.
     */
    boolean terminateCurrentUserSession( @NonNull String aUserAccessToken );

    /**
     * Terminates a specific user session based on the provided session ID.
     *
     * @param aUserAccessToken
     *         a non-null string representing the access token of the user whose session is to be terminated.
     * @param aSessionId
     *         a non-null string representing the unique identifier of the session to be terminated.
     * @return {@code true} if the session was successfully terminated; {@code false} otherwise.
     */
    boolean terminateSession( @NonNull String aUserAccessToken, @NonNull String aSessionId );

    /**
     * Refreshes the user session by validating the provided authentication request details
     * and access token, and then generates a new {@code AuthenticationTokenDto}.
     *
     * @param aAccessToken
     *         a non-null {@code String} representing the current access token associated with the user session.
     * @return a new {@code AuthenticationTokenDto} containing refreshed tokens and other relevant
     * session details, such as updated expiration times and token types.
     */
    AuthenticationTokenDto refreshUserSession( @NonNull String aAccessToken );

    /**
     * Deletes an active user session identified by the given session ID. Session should be deleted when it is no longer valid and cannot be refreshed.
     *
     * @param aSessionId
     *         a non-null string representing the unique identifier of the session to be deleted.
     */
    void deleteUserSession( @NonNull String aSessionId );
}
