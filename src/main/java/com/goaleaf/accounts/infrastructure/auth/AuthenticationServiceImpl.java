package com.goaleaf.accounts.infrastructure.auth;

import com.github.pplociennik.commons.lang.CommonsResExcMsgTranslationKey;
import com.github.pplociennik.commons.service.SystemPropertiesReaderService;
import com.goaleaf.accounts.api.dto.auth.AuthenticationRequestDto;
import com.goaleaf.accounts.api.dto.auth.RegistrationRequestDto;
import com.goaleaf.accounts.api.dto.keycloak.AccountDto;
import com.goaleaf.accounts.api.dto.response.AuthenticationResponseDto;
import com.goaleaf.accounts.api.dto.response.AuthenticationResponseUserDataDto;
import com.goaleaf.accounts.api.dto.response.AuthenticationTokenDto;
import com.goaleaf.accounts.api.dto.response.KeycloakErrorResponseDto;
import com.goaleaf.accounts.api.dto.auth.AuthenticationDetailsDto;
import com.goaleaf.accounts.api.dto.user.UserDetailsDto;
import com.goaleaf.accounts.api.map.AuthenticationTokenMapper;
import com.goaleaf.accounts.api.map.UserDetailsMapper;
import com.goaleaf.accounts.domain.KeycloakClient;
import com.goaleaf.accounts.domain.account.AccountService;
import com.goaleaf.accounts.domain.auth.AuthenticationService;
import com.goaleaf.accounts.domain.auth.AuthenticationValidationService;
import com.goaleaf.accounts.domain.session.UserSessionDetailsService;
import com.goaleaf.accounts.domain.system.exc.auth.AccountNotVerifiedException;
import com.goaleaf.accounts.domain.system.exc.auth.AuthenticationFailedException;
import com.goaleaf.accounts.domain.system.exc.auth.RegistrationFailedException;
import com.goaleaf.accounts.domain.system.exc.request.KeycloakActionRequestFailedException;
import com.goaleaf.accounts.domain.system.lang.AccountsExcTranslationKey;
import com.goaleaf.accounts.domain.system.util.AccessTokenUtils;
import com.goaleaf.accounts.domain.system.util.KeycloakUrlTemplates;
import com.goaleaf.accounts.domain.user.UserDetailsService;
import com.goaleaf.accounts.domain.user.model.UserDetails;
import com.goaleaf.accounts.domain.session.model.UserSessionDetails;
import lombok.AllArgsConstructor;
import lombok.Synchronized;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import static com.github.pplociennik.commons.utility.OptionalUtils.getOptionalValue;
import static com.goaleaf.accounts.domain.system.properties.AccountsSystemProperties.*;
import static com.goaleaf.accounts.domain.system.util.AccessTokenUtils.getSessionId;
import static com.goaleaf.accounts.domain.system.util.KeycloakUrlTemplates.TERMINATE_ALL_USER_SESSIONS_TEMPLATE;
import static com.goaleaf.accounts.domain.system.util.KeycloakUrlTemplates.TERMINATE_USER_SESSION_TEMPLATE;
import static java.util.Objects.requireNonNull;

/**
 * Implementation of the {@link AuthenticationService} interface responsible for managing user authentication processes.
 *
 * <p>This class provides functionality to interact with the authentication system to register new user accounts,
 * authenticate users in the system, and manage user sessions including termination and refreshing.</p>
 *
 * <p>Integrations include Keycloak for identity management and local session tracking via {@link UserSessionDetailsService}.</p>
 *
 * @author Pplociennik
 * @since 1.0
 * @see AuthenticationService
 * @see KeycloakClient
 * @see UserSessionDetailsService
 */
@Service
@AllArgsConstructor
@Log4j2
class AuthenticationServiceImpl implements AuthenticationService {

    /**
     * Service for reading system properties configuration.
     *
     * <p>Used to retrieve system-specific properties such as Keycloak realm name, client ID, and client secret
     * required for authentication-related operations.</p>
     */
    private final SystemPropertiesReaderService systemPropertiesReaderService;

    /**
     * Service for handling validation logic in the authentication process.
     *
     * <p>Validates registration requests and other authentication-related data before processing.</p>
     */
    private final AuthenticationValidationService authenticationValidationService;

    /**
     * Service for managing user details operations within the system.
     *
     * <p>Handles operations such as creating and retrieving user details from local storage.</p>
     */
    private final UserDetailsService userDetailsService;

    /**
     * Service for performing account management operations.
     *
     * <p>Handles credential management, email verification checks, and account-related queries.</p>
     */
    private final AccountService accountService;

    /**
     * Client service responsible for communicating with the Keycloak authentication server.
     *
     * <p>Manages WebClient connections, token requests, and session operations with Keycloak.</p>
     */
    private KeycloakClient keycloakConnectionService;

    /**
     * Service responsible for managing user session-related operations.
     *
     * <p>Handles creation, retrieval, updating, and deletion of user session details in local storage.</p>
     */
    private UserSessionDetailsService userSessionDetailsService;



    /**
     * Registers a new user account by sending the registration request to the Keycloak authentication service.
     *
     * <p>Validates the registration request, communicates with Keycloak to create the user account,
     * and then creates a corresponding user details record in the local system.</p>
     *
     * @param aDto the registration request data transfer object containing user registration details
     * @return a {@code UserDetailsDto} object representing the newly registered user details
     * @throws NullPointerException if {@code aDto} is null
     * @throws RegistrationFailedException if the registration process encounters an error from Keycloak
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
     * Authenticates a user account and returns an authentication response containing user details and authentication tokens.
     *
     * <p>Verifies that the user's email is verified, communicates with Keycloak to validate credentials,
     * and creates a local session record. The method returns both authentication tokens and user details.</p>
     *
     * @param aDto the data transfer object containing the user's email and password for authentication
     * @return an {@code AuthenticationResponseDto} containing the authenticated user's details and authentication token
     * @throws NullPointerException if {@code aDto} is null
     * @throws AuthenticationFailedException if the authentication process fails or invalid credentials are provided
     * @throws AccountNotVerifiedException if the email address related to the account has not been verified
     */
    @Override
    public AuthenticationResponseDto authenticateUserAccount( @NonNull AuthenticationRequestDto aDto ) {
        requireNonNull( aDto );

        accountService.checkIfEmailVerified( aDto.getEmail() );

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
                                    + "&username=" + aDto.getEmail()
                                    + "&password=" + aDto.getPassword()
                    )
                    .retrieve()
                    .bodyToMono( AuthenticationTokenDto.class )
                    .block();

        } catch ( WebClientResponseException aE ) {
            KeycloakErrorResponseDto errorResponse = aE.getResponseBodyAs( KeycloakErrorResponseDto.class );
            requireNonNull( errorResponse );
            throw new AuthenticationFailedException( AccountsExcTranslationKey.AUTHENTICATION_FAILED, aDto.getEmail(), errorResponse.getErrorDescription() );
        }

        UserSessionDetails sessionContext = buildSessionContext( aDto );
        userSessionDetailsService.createUserSessionDetails( sessionContext, AuthenticationTokenMapper.mapToDomain( requireNonNull( authenticationToken ) ) );
        UserDetails userDetails = userDetailsService.findUserDetailsByEmail( aDto.getEmail() );
        AuthenticationResponseUserDataDto userDataDto = new AuthenticationResponseUserDataDto( userDetails.getUserName() );
        return new AuthenticationResponseDto( userDataDto, authenticationToken );
    }

    /**
     * Terminates all active sessions associated with the user identified by the provided access token.
     *
     * <p>Extracts the user ID from the access token and sends a termination request to Keycloak
     * to invalidate all active sessions for that user.</p>
     *
     * @param aUserAccessToken a non-null string representing the access token of the user whose sessions are to be terminated
     * @return {@code true} if the user sessions were successfully terminated
     * @throws NullPointerException if {@code aUserAccessToken} is null
     * @throws KeycloakActionRequestFailedException if the termination request fails
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
     * <p>Extracts the session ID from the access token and terminates only that specific session.</p>
     *
     * @param aUserAccessToken the access token of the user whose session needs to be terminated; must not be null
     * @return {@code true} if the session is successfully terminated
     * @throws NullPointerException if {@code aUserAccessToken} is null
     * @throws KeycloakActionRequestFailedException if the termination request fails
     */
    @Override
    public boolean terminateCurrentUserSession( @NonNull String aUserAccessToken ) {
        requireNonNull( aUserAccessToken );
        String sessionId = AccessTokenUtils.getSessionId( aUserAccessToken );
        return terminateSession( aUserAccessToken, sessionId );
    }

    /**
     * Refreshes the user session by generating a new authentication token using the refresh token.
     *
     * <p>Retrieves the session details from local storage using the session ID extracted from the access token,
     * sends a refresh token request to Keycloak, and updates the local session record with the new token.</p>
     *
     * @param aUserAccessToken the current access token associated with the session to be refreshed
     * @return a new {@code AuthenticationTokenDto} containing the refreshed access and refresh token information,
     *         or null if the session details cannot be found
     * @throws NullPointerException if {@code aUserAccessToken} is null
     */
    @Override
    @Synchronized
    public AuthenticationTokenDto refreshUserSession( @NonNull String aUserAccessToken ) {
        log.info( "Refreshing user session details" );
        requireNonNull( aUserAccessToken );
        String sessionId = getSessionId( aUserAccessToken );
        UserSessionDetails sessionDetails = getOptionalValue( userSessionDetailsService.getUserSessionDetails( sessionId ) );

        if ( sessionDetails != null ) {
            AuthenticationTokenDto refreshedToken = keycloakConnectionService.sendRefreshTokenRequest( sessionDetails.getRefreshToken() );
            userSessionDetailsService.updateSessionDetails( sessionDetails, AuthenticationTokenMapper.mapToDomain( refreshedToken ) );
            return refreshedToken;
        }

        return null;
    }

    /**
     * Deletes an active user session identified by the given session ID.
     *
     * <p>Session should be deleted when it is no longer valid and cannot be refreshed. This sends
     * a deletion request to the Keycloak client to clean up the session record.</p>
     *
     * @param aSessionId a non-null string representing the unique identifier of the session to be deleted
     * @throws NullPointerException if {@code aSessionId} is null
     */
    @Override
    public void deleteUserSession( @NonNull String aSessionId ) {
        requireNonNull( aSessionId );
        keycloakConnectionService.sendSessionDeletionRequest( aSessionId );
    }

    /**
     * Terminates an active user session with the given session ID.
     *
     * <p>Sends a termination request to Keycloak and removes the session details from local storage.</p>
     *
     * @param aUserAccessToken the access token used to authenticate the session termination request; must not be null
     * @param aSessionId the unique identifier of the user session to be terminated; must not be null
     * @return {@code true} if the session is successfully terminated
     * @throws NullPointerException if {@code aUserAccessToken} or {@code aSessionId} is null
     * @throws KeycloakActionRequestFailedException if the termination request fails
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

        userSessionDetailsService.deleteSessionDetails( aSessionId );
        return true;
    }

    /**
     * Builds a user session context from the authentication request.
     *
     * <p>Extracts location and device information from the authentication details and constructs
     * a {@code UserSessionDetails} object with these details.</p>
     *
     * @param aDto the authentication request containing session context details; must not be null
     * @return a {@code UserSessionDetails} object with location and device information
     * @throws NullPointerException if {@code aDto} is null
     */
    private UserSessionDetails buildSessionContext( @NonNull AuthenticationRequestDto aDto ) {
        AuthenticationDetailsDto details = aDto.getDetails();
        String location = details != null ? details.getLocation() : null;
        String device = details != null ? details.getDeviceName() : null;
        return UserSessionDetails.builder()
                .location( location )
                .device( device )
                .build();
    }

    /**
     * Creates internal user details record after successful user registration.
     *
     * <p>Retrieves the account information from Keycloak using the access token, maps the account
     * and registration request data to a user details domain object, persists it in the local system,
     * and returns the mapped data transfer object.</p>
     *
     * @param aAccessToken the access token for authenticating the request to retrieve account details; must not be null
     * @param aDto the registration request containing user details; must not be null
     * @return a {@code UserDetailsDto} representing the newly created user details
     * @throws NullPointerException if {@code aAccessToken} or {@code aDto} is null
     */
    private UserDetailsDto createInnerUserDetails( @NonNull String aAccessToken, @NonNull RegistrationRequestDto aDto ) {
        requireNonNull( aDto );
        AccountDto retrievedAccount = accountService.getAccountByEmailAddress( aAccessToken, aDto.getEmail() );
        UserDetails userDetails = UserDetailsMapper.mapToDomain( retrievedAccount, aDto );
        UserDetails created = userDetailsService.createUserDetails( userDetails );
        return UserDetailsMapper.mapToDto( created );
    }


}
