package com.goaleaf.accounts.controller;

import com.github.pplociennik.commons.dto.ResponseDto;
import com.goaleaf.accounts.data.dto.auth.AuthenticationRequestDto;
import com.goaleaf.accounts.data.dto.auth.RegistrationRequestDto;
import com.goaleaf.accounts.data.dto.response.AuthenticationResponseDto;
import com.goaleaf.accounts.data.dto.response.AuthenticationResponseUserDataDto;
import com.goaleaf.accounts.data.dto.response.AuthenticationTokenDto;
import com.goaleaf.accounts.service.AccountService;
import com.goaleaf.accounts.service.AuthenticationService;
import com.goaleaf.accounts.system.util.AccessTokenUtils;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import static java.util.Objects.requireNonNull;

/**
 * A controller being a REST API for authentication operations.
 *
 * @author Created by: Pplociennik at 19.03.2025 18:58
 */
@RestController
@RequestMapping( path = "/api/auth" )
@AllArgsConstructor
@Log4j2
class AuthenticationController {

    /**
     * A service responsible for handling authentication-related operations
     * such as registering new user accounts, validating credentials, or
     * managing access tokens within the authentication workflow.
     */
    private AuthenticationService authenticationService;

    /**
     * A service responsible for managing user account operations, including
     * account creation, updates, deletions, and retrieval of user account details.
     */
    private AccountService accountService;

    /**
     * Registers a new user account using the provided registration request data.
     *
     * @param aRegistrationRequestDto
     *         a non-null {@code RegistrationRequestDto} containing the user registration details.
     * @return a {@code ResponseEntity<ResponseDto>} containing the HTTP status and a message indicating
     * the result of the registration process.
     */
    @PostMapping( path = "/register" )
    @Transactional
    ResponseEntity< ResponseDto > registerNewUserAccount( @NonNull @RequestBody RegistrationRequestDto aRegistrationRequestDto ) {
        requireNonNull( aRegistrationRequestDto );
        log.debug( "Registering new user account {}", aRegistrationRequestDto );
        authenticationService.registerUserAccount( aRegistrationRequestDto );
        log.debug( "Registered new user account {}", aRegistrationRequestDto );
        accountService.requestEmailAddressVerificationMessage( aRegistrationRequestDto.getEmail() );
        return ResponseEntity
                .status( HttpStatus.CREATED )
                .body(
                        ResponseDto.builder()
                                .withStatusInfo( "200", "User registered successfully." )
                                .build()
                );
    }

    /**
     * Authenticates a user account based on the provided access token and authentication request data.
     *
     * @param authenticationRequestDto
     *         a non-null {@code AuthenticationRequestDto} containing the user's authentication details, such as credentials.
     * @return a {@code ResponseEntity<AuthenticationTokenDto>} containing the issued authentication token data and the
     * HTTP status representing the result of the authentication process.
     */
    @PostMapping( path = "/login" )
    @Transactional
    ResponseEntity< ResponseDto< AuthenticationResponseUserDataDto > > authenticateUserAccount( @NonNull @RequestBody AuthenticationRequestDto authenticationRequestDto ) {
        requireNonNull( authenticationRequestDto );
        log.debug( "Authenticating user account {}", authenticationRequestDto );
        AuthenticationResponseDto response = authenticationService.authenticateUserAccount( authenticationRequestDto );
        AuthenticationTokenDto tokenData = response.getToken();
        AuthenticationResponseUserDataDto userData = response.getUserData();
        log.debug( "Authenticated user account {}", response );
        return ResponseEntity
                .status( HttpStatus.ACCEPTED )
                .body(
                        ResponseDto.< AuthenticationResponseUserDataDto >builder()
                                .withStatusInfo( "200", "User authenticated successfully." )
                                .withUserAccessToken( true, tokenData.getAccessToken(), tokenData.getExpiresIn() )
                                .withResponseData( userData )
                                .build()
                );
    }

    /**
     * Refreshes the user access token by validating the provided user access token
     * and issuing a new token if the current session is still valid.
     *
     * @param aUserAccessToken
     *         the current user access token sent in the request header
     *         that needs to be refreshed; must not be null
     * @return ResponseEntity containing an updated {@link AuthenticationTokenDto}
     * if the token refresh is successful, with an HTTP status of 200 (OK).
     */
    @PostMapping( path = "/session/refresh" )
    @Transactional
    ResponseEntity< ResponseDto< AuthenticationTokenDto > > refreshUserAccessToken( @NonNull @RequestHeader( value = "User-Token" ) String aUserAccessToken ) {
        requireNonNull( aUserAccessToken );
        log.debug( "Refreshing user access token {}", aUserAccessToken );
        AuthenticationTokenDto authenticationTokenDto = authenticationService.refreshUserSession( aUserAccessToken );
        log.debug( "Refreshed user access token {}", authenticationTokenDto );
        return ResponseEntity
                .status( HttpStatus.OK )
                .body( ResponseDto.< AuthenticationTokenDto >builder()
                        .withStatusInfo( "200", "User access token refreshed successfully." )
                        .withUserAccessToken( true, authenticationTokenDto.getAccessToken(), authenticationTokenDto.getExpiresIn() ).build() );
    }

    /**
     * Terminates all active sessions associated with the user identified by the provided access token.
     *
     * @param aTokenRefreshed
     *         an attribute indicating whether the user access token has been refreshed or not
     * @param aUserAccessToken
     *         a non-null {@code String} representing the access token of the user whose sessions are to be terminated.
     * @return a {@code ResponseEntity<ResponseDto>} containing the HTTP status and a confirmation message
     * indicating the result of the session termination process.
     */
    @PostMapping( path = "/logout/all" )
    ResponseEntity< ResponseDto > logoutAllUserSessions( @RequestAttribute( value = "USER_ACCESS_TOKEN_REFRESHED" ) @NonNull boolean aTokenRefreshed, @RequestHeader( value = "User-Token" ) @NonNull String aUserAccessToken ) {
        requireNonNull( aUserAccessToken );
        log.debug( "Terminating user sessions for {}", aUserAccessToken );
        authenticationService.terminateAllSessions( aUserAccessToken );
        log.debug( "Terminated user sessions for {}", aUserAccessToken );
        return ResponseEntity
                .status( HttpStatus.OK )
                .body(
                        ResponseDto.builder()
                                .withStatusInfo( "200", "All user sessions terminated successfully." )
                                .build()
                );

    }

    /**
     * Logs out the current user's session by terminating it based on the provided access token.
     * This endpoint is responsible for invalidating the session associated with the given token.
     *
     * @param aTokenRefreshed
     *         an attribute indicating whether the user access token has been refreshed or not
     * @param aUserAccessToken
     *         the access token of the user whose session should be terminated; must not be null
     * @return a ResponseEntity containing a ResponseDto with the status and message indicating
     * if the session termination was successful
     */
    @DeleteMapping( path = "/logout" )
    ResponseEntity< ResponseDto > logoutCurrentUserSession( @RequestAttribute( value = "USER_ACCESS_TOKEN_REFRESHED" ) @NonNull boolean aTokenRefreshed, @NonNull @RequestHeader( value = "User-Token" ) String aUserAccessToken ) {
        requireNonNull( aUserAccessToken );
        log.debug( "Logout current user session for {}", aUserAccessToken );
        authenticationService.terminateCurrentUserSession( aUserAccessToken );
        log.debug( "Terminated current user session for {}", aUserAccessToken );
        return ResponseEntity
                .status( HttpStatus.OK )
                .body(
                        ResponseDto.builder()
                                .withStatusInfo( "200", "User session terminated successfully." )
                                .build()
                );
    }

    /**
     * Logs out a specific session for the given session ID and user access token.
     *
     * @param aTokenRefreshed
     *         an attribute indicating whether the user access token has been refreshed or not
     * @param aUserAccessToken
     *         the access token of the user requesting the logout
     * @param aSessionId
     *         the unique identifier of the session to be terminated
     * @return a {@code ResponseEntity} containing a {@code ResponseDto} with the status code and message indicating the result of the operation
     */
    @DeleteMapping( path = "/logout-session" )
    ResponseEntity< ResponseDto > logoutSpecificSession( @RequestAttribute( value = "USER_ACCESS_TOKEN_REFRESHED" ) @NonNull boolean aTokenRefreshed, @NonNull @RequestHeader( value = "User-Token" ) String aUserAccessToken, @NonNull @RequestParam( value = "sessionId" ) String aSessionId ) {
        requireNonNull( aUserAccessToken );
        requireNonNull( aSessionId );
        log.debug( "Logout specific session for {}", aSessionId );
        authenticationService.terminateSession( aUserAccessToken, aSessionId );
        log.debug( "Terminated specific session for {}", aSessionId );
        // If the token was refreshed but the user terminates the current session, the new token must not be returned as it is no longer valid.
        boolean shouldTokenBeReturned = !( aTokenRefreshed && AccessTokenUtils.getSessionId( aUserAccessToken ).equals( aSessionId ) );
        return ResponseEntity
                .status( HttpStatus.OK )
                .body(
                        ResponseDto.builder()
                                .withStatusInfo( "200", "Session terminated successfully." )
                                // Method .withUserAccessToken adds token info only if the first argument is true.
                                .withUserAccessToken( shouldTokenBeReturned, aUserAccessToken, AccessTokenUtils.getExpiresIn( aUserAccessToken ) )
                                .build()
                );
    }

}
