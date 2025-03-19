package com.goaleaf.accounts.service;

import com.goaleaf.accounts.data.dto.auth.AuthenticationRequestDto;
import com.goaleaf.accounts.data.dto.response.AuthenticationTokenDto;
import com.goaleaf.accounts.data.dto.response.UserSessionResponseDto;
import com.goaleaf.accounts.data.dto.user.UserSessionDetailsDto;
import com.goaleaf.accounts.persistence.entity.UserSessionDetails;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;

/**
 * A service interface for managing user session details. Implementations of this interface provide functionality
 * for creating, retrieving, and managing user sessions, including the generation of session information based on
 * authentication tokens.
 *
 * @author Created by: Pplociennik at 01.04.2025 20:32
 */
public interface UserSessionDetailsService {

    /**
     * Creates user session details based on the provided authentication request data and authentication token.
     *
     * @param aDto
     *         the data transfer object containing the user's authentication request details such as username and password.
     * @param aAuthenticationToken
     *         the data transfer object containing authentication token details, including access and refresh tokens.
     * @return a {@code UserSessionDetailsDto} containing details of the user's session, such as the session ID,
     * creation time, and associated user information.
     */
    UserSessionDetailsDto createUserSessionDetails( @NonNull AuthenticationRequestDto aDto, @NonNull AuthenticationTokenDto aAuthenticationToken );

    /**
     * Creates user session details based on the provided user session data (old session) and authentication token.
     *
     * @param aDto
     *         a non-null {@code UserSessionDetailsDto} containing the details of the user's session such as session ID,
     *         user identifier, location, and device information.
     * @param aAuthenticationToken
     *         a non-null {@code AuthenticationTokenDto} containing authentication token information,
     *         including access and refresh tokens.
     * @return a {@code UserSessionDetailsDto} containing updated or created user session details.
     */
    UserSessionDetailsDto createUserSessionDetails( @NonNull UserSessionDetailsDto aDto, @NonNull AuthenticationTokenDto aAuthenticationToken );

    /**
     * Retrieves a list of all user session details associated with the provided access token.
     *
     * @param aAccessToken
     *         a non-null string representing the access token used to authenticate and identify the user sessions.
     * @return a list of {@code UserSessionResponseDto} objects containing details about the user sessions,
     * such as session ID, IP address, session timing, location, and device information.
     */
    List< UserSessionResponseDto > getAllUserSessionsInfo( @NonNull String aAccessToken );

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
     * @return an {@code Optional} containing a {@code UserSessionDetailsDto} if the session details are found,
     * or an empty {@code Optional} if no matching session details exist.
     */
    Optional< UserSessionDetailsDto > getUserSessionDetails( @NonNull String aSessionId );

    /**
     * Deletes the specified user session details from the system.
     *
     * @param aSessionDetails
     *         the {@code UserSessionDetails} object representing the session details
     *         to be removed; must not be null.
     */
    void deleteSessionDetails( @NonNull UserSessionDetails aSessionDetails );

    /**
     * Deletes the specified session details identified by the session ID from the system.
     *
     * @param aSessionId
     *         the unique identifier of the session to be deleted; must not be null.
     */
    void deleteSessionDetails( @NonNull String aSessionId );
}
