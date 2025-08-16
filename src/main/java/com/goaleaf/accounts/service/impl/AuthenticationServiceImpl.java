package com.goaleaf.accounts.service.impl;

import com.github.pplociennik.commons.lang.CommonsResExcMsgTranslationKey;
import com.github.pplociennik.commons.service.SystemPropertiesReaderService;
import com.goaleaf.accounts.data.dto.auth.AuthenticationRequestDto;
import com.goaleaf.accounts.data.dto.auth.RegistrationRequestDto;
import com.goaleaf.accounts.data.dto.keycloak.AccountDto;
import com.goaleaf.accounts.data.dto.response.AuthenticationTokenDto;
import com.goaleaf.accounts.data.dto.response.KeycloakErrorResponseDto;
import com.goaleaf.accounts.data.dto.user.UserDetailsDto;
import com.goaleaf.accounts.data.map.UserDetailsMapper;
import com.goaleaf.accounts.data.map.UserSessionDetailsMapper;
import com.goaleaf.accounts.persistence.entity.UserSessionDetails;
import com.goaleaf.accounts.persistence.repository.UserSessionDetailsRepository;
import com.goaleaf.accounts.service.*;
import com.goaleaf.accounts.service.validation.AuthenticationValidationService;
import com.goaleaf.accounts.system.exc.auth.AuthenticationFailedException;
import com.goaleaf.accounts.system.exc.auth.RegistrationFailedException;
import com.goaleaf.accounts.system.exc.request.KeycloakActionRequestFailedException;
import com.goaleaf.accounts.system.lang.AccountsExcTranslationKey;
import com.goaleaf.accounts.system.util.AccessTokenUtils;
import com.goaleaf.accounts.system.util.KeycloakUrlTemplates;
import lombok.AllArgsConstructor;
import lombok.Synchronized;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;

import static com.github.pplociennik.commons.utility.OptionalUtils.getMandatoryValue;
import static com.goaleaf.accounts.system.properties.AccountsSystemProperties.*;
import static com.goaleaf.accounts.system.util.AccessTokenUtils.getSessionId;
import static com.goaleaf.accounts.system.util.KeycloakUrlTemplates.TERMINATE_ALL_USER_SESSIONS_TEMPLATE;
import static com.goaleaf.accounts.system.util.KeycloakUrlTemplates.TERMINATE_USER_SESSION_TEMPLATE;
import static java.util.Objects.requireNonNull;

/**
 * Implementation of the {@link AuthenticationService} interface responsible for managing user authentication processes. This class provides the functionality to interact with the authentication system
 * to register new user accounts, authenticate user in the system or terminate user sessions.
 *
 * <p><b>Author:</b> Pplociennik</p>
 * <p><b>Created:</b> 19.03.2025 19:03</p>
 */
@Service
@AllArgsConstructor
@Log4j2
class AuthenticationServiceImpl implements AuthenticationService {

    /**
     * A service for reading system properties configuration.
     * This variable is used to retrieve system-specific properties required in the authentication-related operations.
     */
    private final SystemPropertiesReaderService systemPropertiesReaderService;

    /**
     * A service for handling validation logic in the authentication process.
     */
    private final AuthenticationValidationService authenticationValidationService;

    /**
     * A service used for managing user details related operations within the system.
     */
    private final UserDetailsService userDetailsService;

    /**
     * A service used for performing operations
     * related to account management, such as credential management.
     */
    private final AccountService accountService;

    /**
     * A service responsible for connecting to the keycloak server.
     */
    private KeycloakServiceConnectionService keycloakConnectionService;

    /**
     * Service responsible for managing user session-related operations.
     */
    private UserSessionDetailsService userSessionDetailsService;

    /**
     * Repository for managing user session details.
     */
    private UserSessionDetailsRepository userSessionDetailsRepository;


    /**
     * Registers a new user account by sending the registration request to the authentication service.
     * Validates the registration request and creates the user account after receiving a successful response.
     *
     * @param aDto
     *         the registration request data transfer object containing user registration details
     * @return a UserDetailsDto object representing the newly registered user details
     *
     * @throws RegistrationFailedException
     *         if the registration process encounters an error
     */
    @Override
    public UserDetailsDto registerUserAccount( @NonNull RegistrationRequestDto aDto ) {
        requireNonNull( aDto );
        authenticationValidationService.validateRegistrationRequest( aDto );
        String realmName = systemPropertiesReaderService.readProperty( KEYCLOAK_REALM_NAME );
        WebClient client = keycloakConnectionService.getAuthServiceConnectionWebClient( KeycloakUrlTemplates.REGISTRATION_URL_TEMPLATE, realmName );
        String clientAccessToken = keycloakConnectionService.getClientAccessToken();

        try {
            client.post()
                    .header( "Authorization", clientAccessToken )
                    .contentType( MediaType.APPLICATION_JSON )
                    .bodyValue( aDto )
                    .retrieve()
                    .toBodilessEntity()
                    .block();

            return createInnerUserDetails( clientAccessToken, aDto );
        } catch ( WebClientResponseException aE ) {
            KeycloakErrorResponseDto responseBody = aE.getResponseBodyAs( KeycloakErrorResponseDto.class );
            requireNonNull( responseBody );
            throw new RegistrationFailedException( AccountsExcTranslationKey.REGISTRATION_FAILED, responseBody.getError() );
        }
    }

    /**
     * Authenticates a user account using the provided access token and authentication request details.
     *
     * @param aDto
     *         the authentication request details containing username and password; must not be null
     * @return an {@link AuthenticationTokenDto} containing the authentication token details for the authenticated user
     *
     * @throws NullPointerException
     *         if aAccessToken or aDto is null
     * @throws AuthenticationFailedException
     *         if authentication fails due to any reason
     */
    @Override
    public AuthenticationTokenDto authenticateUserAccount( @NonNull AuthenticationRequestDto aDto ) {
        requireNonNull( aDto );

        String realmName = systemPropertiesReaderService.readProperty( KEYCLOAK_REALM_NAME );
        String clientID = systemPropertiesReaderService.readProperty( KEYCLOAK_CLIENT_ID );
        String clientSecret = systemPropertiesReaderService.readProperty( KEYCLOAK_CLIENT_SECRET );
        WebClient client = keycloakConnectionService.getAuthServiceConnectionWebClient( KeycloakUrlTemplates.AUTHENTICATION_URL_TEMPLATE, realmName );

        AuthenticationTokenDto authenticationToken;
        try {
            authenticationToken = client.post()
                    .contentType( MediaType.APPLICATION_FORM_URLENCODED )
                    .bodyValue(
                            "grant_type=password"
                                    + "&client_id=" + clientID
                                    + "&client_secret=" + clientSecret
                                    + "&username=" + aDto.getUsername()
                                    + "&password=" + aDto.getPassword()
                    )
                    .retrieve()
                    .bodyToMono( AuthenticationTokenDto.class )
                    .block();

        } catch ( WebClientResponseException aE ) {
            KeycloakErrorResponseDto errorResponse = aE.getResponseBodyAs( KeycloakErrorResponseDto.class );
            requireNonNull( errorResponse );
            throw new AuthenticationFailedException( AccountsExcTranslationKey.AUTHENTICATION_FAILED, aDto.getUsername(), errorResponse.getErrorDescription() );
        }

        userSessionDetailsService.createUserSessionDetails( aDto, requireNonNull( authenticationToken ) );
        return authenticationToken;
    }

    /**
     * Terminates all active sessions associated with the user identified by the provided access token.
     *
     * @param aUserAccessToken
     *         a non-null {@code String} that represents the access token of the user whose sessions are to be terminated.
     * @return {@code true} if the user sessions were successfully terminated.
     */
    @Override
    public boolean terminateAllSessions( @NonNull String aUserAccessToken ) {
        requireNonNull( aUserAccessToken );
        String realmName = systemPropertiesReaderService.readProperty( KEYCLOAK_REALM_NAME );
        String userId = AccessTokenUtils.getUserId( aUserAccessToken );

        WebClient client = keycloakConnectionService.getAuthServiceConnectionWebClient( TERMINATE_ALL_USER_SESSIONS_TEMPLATE, realmName, userId );
        String clientAccessToken = keycloakConnectionService.getClientAccessToken();

        try {
            client.post()
                    .header( "Authorization", clientAccessToken )
                    .contentType( MediaType.APPLICATION_FORM_URLENCODED )
                    .bodyValue(
                            "realm=" + realmName
                                    + "&user-id=" + userId
                    ).retrieve()
                    .toBodilessEntity()
                    .block();
        } catch ( WebClientResponseException aE ) {
            KeycloakErrorResponseDto errorResponse = aE.getResponseBodyAs( KeycloakErrorResponseDto.class );
            requireNonNull( errorResponse );
            throw new KeycloakActionRequestFailedException( CommonsResExcMsgTranslationKey.UNEXPECTED_EXCEPTION, errorResponse.getErrorDescription() );
        }

        return true;
    }

    /**
     * Terminates the current user's session associated with the provided user access token.
     *
     * @param aUserAccessToken
     *         the access token of the user whose session needs to be terminated; must not be null
     * @return true if the session is successfully terminated
     */
    @Override
    public boolean terminateCurrentUserSession( @NonNull String aUserAccessToken ) {
        requireNonNull( aUserAccessToken );
        String sessionId = AccessTokenUtils.getSessionId( aUserAccessToken );
        return terminateSession( aUserAccessToken, sessionId );
    }

    /**
     * Refreshes the user session by invalidating the current session and generating a new authentication token.
     *
     * @param aUserAccessToken
     *         the current access token associated with the session to be refreshed
     * @return a new {@code AuthenticationTokenDto} containing the refreshed access and refresh token information
     *
     * @throws NullPointerException
     *         if {@code aAuthenticationRequestDto} or {@code aAccessToken} is null
     */
    @Override
    @Synchronized
    public AuthenticationTokenDto refreshUserSession( @NonNull String aUserAccessToken ) {
        log.info( "Refreshing user session details" );
        requireNonNull( aUserAccessToken );
        String sessionId = getSessionId( aUserAccessToken );
        UserSessionDetails sessionDetails = getMandatoryValue( userSessionDetailsRepository.findBySessionId( sessionId ) );

        AuthenticationTokenDto refreshedToken = keycloakConnectionService.sendRefreshTokenRequest( sessionDetails.getRefreshToken() );
        userSessionDetailsService.deleteSessionDetails( sessionDetails );
        userSessionDetailsService.createUserSessionDetails( UserSessionDetailsMapper.mapToDto( sessionDetails ), refreshedToken );
        return refreshedToken;
    }

    /**
     * Deletes an active user session identified by the given session ID. Session should be deleted when it is no longer valid and cannot be refreshed.
     *
     * @param aSessionId
     *         a non-null string representing the unique identifier of the session to be deleted.
     */
    @Override
    public void deleteUserSession( @NonNull String aSessionId ) {
        requireNonNull( aSessionId );
        keycloakConnectionService.sendSessionDeletionRequest( aSessionId );
    }

    /**
     * Terminates an active user session with the given session ID.
     *
     * @param aUserAccessToken
     *         the access token used to authenticate the session termination request
     * @param aSessionId
     *         the unique identifier of the user session to be terminated
     * @return true if the session is successfully terminated
     */
    @Override
    public boolean terminateSession( @NonNull String aUserAccessToken, @NonNull String aSessionId ) {
        requireNonNull( aUserAccessToken );
        requireNonNull( aSessionId );
        String realmName = systemPropertiesReaderService.readProperty( KEYCLOAK_REALM_NAME );
        String clientAccessToken = keycloakConnectionService.getClientAccessToken();

        try {
            WebClient client = keycloakConnectionService.getAuthServiceConnectionWebClient( TERMINATE_USER_SESSION_TEMPLATE, realmName, aSessionId );
            client.delete()
                    .header( "Authorization", clientAccessToken )
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch ( WebClientResponseException aE ) {
            KeycloakErrorResponseDto errorResponse = aE.getResponseBodyAs( KeycloakErrorResponseDto.class );
            requireNonNull( errorResponse );
            throw new KeycloakActionRequestFailedException( CommonsResExcMsgTranslationKey.UNEXPECTED_EXCEPTION, errorResponse.getErrorDescription() );
        }

        return true;
    }

    private UserDetailsDto createInnerUserDetails( @NonNull String aAccessToken, @NonNull RegistrationRequestDto aDto ) {
        requireNonNull( aDto );
        List< AccountDto > retrievedAccounts = accountService.getAccountByEmailAddress( aAccessToken, aDto.getEmail() );
        AccountDto accountDto = retrievedAccounts.get( 0 );
        UserDetailsDto userDetailsDto = UserDetailsMapper.mapToDto( accountDto );
        return userDetailsService.createUserDetails( userDetailsDto );
    }


}
