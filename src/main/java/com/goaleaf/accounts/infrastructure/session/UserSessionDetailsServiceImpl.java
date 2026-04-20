package com.goaleaf.accounts.infrastructure.session;

import com.github.pplociennik.commons.service.SystemPropertiesReaderService;
import com.github.pplociennik.commons.service.TimeService;
import com.goaleaf.accounts.api.dto.keycloak.session.UserSessionRepresentationDto;
import com.goaleaf.accounts.domain.KeycloakClient;
import com.goaleaf.accounts.domain.auth.model.AuthenticationToken;
import com.goaleaf.accounts.domain.session.UserSessionDetailsService;
import com.goaleaf.accounts.domain.session.model.UserSessionDetails;
import com.goaleaf.accounts.domain.session.model.UserSessionInfo;
import com.goaleaf.accounts.domain.session.port.UserSessionDetailsRepository;
import com.goaleaf.accounts.domain.system.util.KeycloakUrlTemplates;
import com.goaleaf.accounts.domain.system.util.token.AccessTokenValidationStrategy;
import com.goaleaf.accounts.domain.system.util.token.OfflineValidationStrategy;
import com.goaleaf.accounts.domain.system.util.token.OnlineValidationStrategy;
import com.goaleaf.accounts.domain.system.util.token.TokenValidationStrategy;
import com.goaleaf.accounts.domain.user.UserDetailsService;
import lombok.AllArgsConstructor;
import lombok.Synchronized;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.github.pplociennik.commons.utility.OptionalUtils.getMandatoryValue;
import static com.goaleaf.accounts.domain.system.properties.AccountsSystemProperties.ACCESS_TOKEN_VALIDATION_STRATEGY;
import static com.goaleaf.accounts.domain.system.properties.AccountsSystemProperties.KEYCLOAK_REALM_NAME;
import static com.goaleaf.accounts.domain.system.util.AccessTokenUtils.getSessionId;
import static com.goaleaf.accounts.domain.system.util.AccessTokenUtils.getUserId;
import static com.goaleaf.accounts.domain.system.util.token.TokenValidationStrategy.OFFLINE;
import static java.util.Objects.requireNonNull;

/**
 * Implementation of the {@link UserSessionDetailsService} interface for managing user session details.
 *
 * <p>Provides functionality for creating, retrieving, updating, and deleting user sessions,
 * including integration with Keycloak for session validation and token management.</p>
 *
 * @author Created by: Pplociennik at 01.04.2025 20:37
 * @since 1.0
 * @see UserSessionDetailsService
 * @see UserSessionDetailsRepository
 */
@Log4j2
@Service
@AllArgsConstructor
class UserSessionDetailsServiceImpl implements UserSessionDetailsService {

    /**
     * A repository used for performing CRUD operations on {@link UserSessionDetails} domain objects.
     */
    private final UserSessionDetailsRepository userSessionDetailsRepository;

    /**
     * Provides access to time-related services.
     */
    private final TimeService timeService;

    /**
     * A service responsible for reading and managing system properties.
     */
    private final SystemPropertiesReaderService systemPropertiesReaderService;

    /**
     * A client for interacting with the Keycloak authentication server.
     */
    private final KeycloakClient keycloakClient;

    /**
     * A service responsible for handling user details retrieval and management operations.
     */
    private final UserDetailsService userDetailsService;

    /**
     * Creates user session details using the provided session context and authentication token.
     *
     * @param aSessionContext
     *         a non-null {@link UserSessionDetails} carrying location and device information
     * @param aAuthenticationToken
     *         a non-null {@link AuthenticationToken} containing the new token information
     * @return the {@link UserSessionDetails} of the saved session
     *
     * @throws NullPointerException
     *         if any of the input parameters are null
     */
    @Override
    public UserSessionDetails createUserSessionDetails( @NonNull UserSessionDetails aSessionContext, @NonNull AuthenticationToken aAuthenticationToken ) {
        log.info( "Creating user session details" );
        requireNonNull( aSessionContext );
        requireNonNull( aAuthenticationToken );
        String userId = getUserId( aAuthenticationToken.getAccessToken() );
        String sessionId = getSessionId( aAuthenticationToken.getAccessToken() );

        UserSessionDetails sessionDetails = UserSessionDetails.builder()
                .authenticatedUserId( userId )
                .location( aSessionContext.getLocation() )
                .device( aSessionContext.getDevice() )
                .sessionId( sessionId )
                .refreshToken( aAuthenticationToken.getRefreshToken() )
                .build();

        return userSessionDetailsRepository.save( sessionDetails );
    }

    /**
     * Retrieves publicly visible information about all sessions associated with the specified user access token.
     *
     * @param aAccessToken
     *         a non-null string representing the user's access token.
     * @return a list of {@link UserSessionInfo} objects describing each active session.
     */
    @Override
    public List<UserSessionInfo> getAllUserSessionsInfo( @NonNull String aAccessToken ) {
        log.debug( "getAllUserSessionDetails called" );
        requireNonNull( aAccessToken );

        String realmName = systemPropertiesReaderService.readProperty( KEYCLOAK_REALM_NAME );
        String userId = getUserId( aAccessToken );
        String clientAccessToken = keycloakClient.getClientAccessToken();

        WebClient client = keycloakClient.getAuthServiceConnectionWebClient( KeycloakUrlTemplates.GET_ALL_SESSIONS_URL_TEMPLATE, realmName, userId );
        try {
            List<UserSessionRepresentationDto> keycloakSessionsRep = client.get()
                    .header( "Authorization", clientAccessToken )
                    .retrieve()
                    .bodyToMono( new ParameterizedTypeReference<List<UserSessionRepresentationDto>>() {} )
                    .block();

            return buildSessionInfoList( userId, requireNonNull( keycloakSessionsRep ) );
        } catch ( Exception aE ) {
            throw new IllegalStateException( "Failed to get user session details: ", aE );
        }
    }

    /**
     * Validates the provided access token to ensure it is active and authorized for use.
     *
     * @param aAccessToken
     *         a non-null {@code String} representing the access token to be validated.
     * @return {@code true} if the access token is valid and active; {@code false} otherwise.
     */
    @Override
    public boolean checkAccessToken( @NonNull String aAccessToken ) {
        requireNonNull( aAccessToken );
        String tokenValidationStrategyName = systemPropertiesReaderService.readProperty( ACCESS_TOKEN_VALIDATION_STRATEGY );
        TokenValidationStrategy strategy = TokenValidationStrategy.valueOf( tokenValidationStrategyName );
        AccessTokenValidationStrategy strategyForExecution = strategy == OFFLINE
                ? new OfflineValidationStrategy( timeService )
                : new OnlineValidationStrategy( keycloakClient );
        return strategyForExecution.validateAccessToken( aAccessToken );
    }

    /**
     * Retrieves the session details of a user associated with the specified session ID.
     *
     * @param aSessionId
     *         a non-null {@code String} representing the unique identifier of the user session.
     * @return an {@link Optional} containing a {@link UserSessionDetails} if the session details are found,
     * or an empty {@link Optional} if no matching session details exist.
     */
    @Override
    public Optional<UserSessionDetails> getUserSessionDetails( @NonNull String aSessionId ) {
        requireNonNull( aSessionId );
        return userSessionDetailsRepository.findBySessionId( aSessionId );
    }

    /**
     * Deletes the specified user session details from the system.
     *
     * @param aSessionDetails
     *         the {@link UserSessionDetails} object representing the session to be removed; must not be null.
     */
    @Synchronized
    @Override
    public void deleteSessionDetails( @NonNull UserSessionDetails aSessionDetails ) {
        requireNonNull( aSessionDetails );
        userSessionDetailsRepository.delete( aSessionDetails );
    }

    /**
     * Deletes the session details associated with the provided session ID.
     *
     * @param aSessionId
     *         the unique identifier of the session to be deleted; must not be null
     */
    @Override
    public void deleteSessionDetails( @NonNull String aSessionId ) {
        requireNonNull( aSessionId );
        Optional<UserSessionDetails> optionalUserSessionDetails = userSessionDetailsRepository.findBySessionId( aSessionId );
        UserSessionDetails sessionDetails = getMandatoryValue( optionalUserSessionDetails );
        userSessionDetailsRepository.delete( sessionDetails );
    }

    /**
     * Updates the specified user session details with a new authentication token.
     *
     * @param aSessionDetails
     *         the {@link UserSessionDetails} object containing the current session information; must not be null.
     * @param aAuthenticationToken
     *         the {@link AuthenticationToken} containing the new token details; must not be null.
     */
    @Override
    public void updateSessionDetails( @NonNull UserSessionDetails aSessionDetails, @NonNull AuthenticationToken aAuthenticationToken ) {
        UserSessionDetails updated = UserSessionDetails.builder()
                .id( aSessionDetails.getId() )
                .sessionId( aSessionDetails.getSessionId() )
                .authenticatedUserId( aSessionDetails.getAuthenticatedUserId() )
                .location( aSessionDetails.getLocation() )
                .device( aSessionDetails.getDevice() )
                .refreshToken( aAuthenticationToken.getRefreshToken() )
                .build();
        userSessionDetailsRepository.save( updated );
    }

    private List<UserSessionInfo> buildSessionInfoList( @NonNull String aUserId, @NonNull List<UserSessionRepresentationDto> aKeycloakSessions ) {
        requireNonNull( aUserId );
        requireNonNull( aKeycloakSessions );
        List<UserSessionDetails> detailsList = userSessionDetailsRepository.findByAuthenticatedUserId( aUserId );

        return aKeycloakSessions.stream()
                .map( rep -> buildSessionInfo( rep, detailsList ) )
                .collect( Collectors.toList() );
    }

    private UserSessionInfo buildSessionInfo( UserSessionRepresentationDto aKeycloakRep, List<UserSessionDetails> aDetailsList ) {
        String sessionId = aKeycloakRep.getId();
        UserSessionDetails details = findDetails( aDetailsList, sessionId );

        return UserSessionInfo.builder()
                .id( sessionId )
                .start( aKeycloakRep.getStart() )
                .lastAccess( aKeycloakRep.getLastAccess() )
                .ipAddress( aKeycloakRep.getIpAddress() )
                .location( details.getLocation() )
                .device( details.getDevice() )
                .build();
    }

    private UserSessionDetails findDetails( List<UserSessionDetails> aDetailsList, String aSessionId ) {
        return aDetailsList.stream()
                .filter( details -> details.getSessionId().equals( aSessionId ) )
                .findFirst()
                .orElseThrow( () -> new IllegalStateException( "No such details found" ) );
    }
}
