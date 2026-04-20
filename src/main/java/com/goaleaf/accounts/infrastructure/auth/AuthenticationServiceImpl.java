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
import com.goaleaf.accounts.api.dto.user.UserDetailsDto;
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
import com.goaleaf.accounts.infrastructure.persistence.dao.UserSessionDetailsDao;
import com.goaleaf.accounts.infrastructure.persistence.entity.UserSessionDetailsEntity;
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
    private KeycloakClient keycloakConnectionService;

    /**
     * Service responsible for managing user session-related operations.
     */
    private UserSessionDetailsService userSessionDetailsService;

    /**
     * Repository for managing user session details.
     */
    private UserSessionDetailsDao userSessionDetailsDao;


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
     * Authenticates a user account and returns an authentication response containing
     * user details and authentication tokens. This method communicates with a Keycloak
     * authentication server and performs required verifications.
     *
     * @param aDto
     *         the data transfer object containing the user's email and password for authentication
     * @return an AuthenticationResponseDto containing the authenticated user's details and authentication token
     *
     * @throws NullPointerException
     *         if the provided authentication request DTO is null
     * @throws AuthenticationFailedException
     *         if the authentication process fails or invalid credentials are provided
     * @throws AccountNotVerifiedException
     *         if the email address related to the account hasn't been verified yet
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

        userSessionDetailsService.createUserSessionDetails( aDto, requireNonNull( authenticationToken ) );
        UserDetails userDetails = userDetailsService.findUserDetailsByEmail( aDto.getEmail() );
        AuthenticationResponseUserDataDto userDataDto = new AuthenticationResponseUserDataDto( userDetails.getUserName() );
        return new AuthenticationResponseDto( userDataDto, authenticationToken );
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
        UserSessionDetailsEntity sessionDetails = getOptionalValue( userSessionDetailsDao.findBySessionId( sessionId ) );

        if ( sessionDetails != null ) {
            AuthenticationTokenDto refreshedToken = keycloakConnectionService.sendRefreshTokenRequest( sessionDetails.getRefreshToken() );
            userSessionDetailsService.updateSessionDetails( sessionDetails, refreshedToken );
            return refreshedToken;
        }

        return null;
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

        userSessionDetailsService.deleteSessionDetails( aSessionId );
        return true;
    }

    private UserDetailsDto createInnerUserDetails( @NonNull String aAccessToken, @NonNull RegistrationRequestDto aDto ) {
        requireNonNull( aDto );
        AccountDto retrievedAccount = accountService.getAccountByEmailAddress( aAccessToken, aDto.getEmail() );
        UserDetails userDetails = UserDetailsMapper.mapToDomain( retrievedAccount, aDto );
        UserDetails created = userDetailsService.createUserDetails( userDetails );
        return UserDetailsMapper.mapToDto( created );
    }


}
