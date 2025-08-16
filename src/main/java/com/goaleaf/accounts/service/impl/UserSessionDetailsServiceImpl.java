package com.goaleaf.accounts.service.impl;

import com.github.pplociennik.commons.service.SystemPropertiesReaderService;
import com.github.pplociennik.commons.service.TimeService;
import com.goaleaf.accounts.data.dto.auth.AuthenticationDetailsDto;
import com.goaleaf.accounts.data.dto.auth.AuthenticationRequestDto;
import com.goaleaf.accounts.data.dto.keycloak.session.UserSessionRepresentationDto;
import com.goaleaf.accounts.data.dto.response.AuthenticationTokenDto;
import com.goaleaf.accounts.data.dto.response.UserSessionResponseDto;
import com.goaleaf.accounts.data.dto.user.UserSessionDetailsDto;
import com.goaleaf.accounts.data.map.UserSessionDetailsMapper;
import com.goaleaf.accounts.persistence.entity.UserSessionDetails;
import com.goaleaf.accounts.persistence.repository.UserSessionDetailsRepository;
import com.goaleaf.accounts.service.KeycloakServiceConnectionService;
import com.goaleaf.accounts.service.UserDetailsService;
import com.goaleaf.accounts.service.UserSessionDetailsService;
import com.goaleaf.accounts.system.util.KeycloakUrlTemplates;
import com.goaleaf.accounts.system.util.token.AccessTokenValidationStrategy;
import com.goaleaf.accounts.system.util.token.OfflineValidationStrategy;
import com.goaleaf.accounts.system.util.token.OnlineValidationStrategy;
import com.goaleaf.accounts.system.util.token.TokenValidationStrategy;
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
import static com.github.pplociennik.commons.utility.OptionalUtils.getOptionalValue;
import static com.goaleaf.accounts.system.properties.AccountsSystemProperties.ACCESS_TOKEN_VALIDATION_STRATEGY;
import static com.goaleaf.accounts.system.properties.AccountsSystemProperties.KEYCLOAK_REALM_NAME;
import static com.goaleaf.accounts.system.util.AccessTokenUtils.getSessionId;
import static com.goaleaf.accounts.system.util.AccessTokenUtils.getUserId;
import static com.goaleaf.accounts.system.util.token.TokenValidationStrategy.OFFLINE;
import static java.util.Objects.requireNonNull;

/**
 * @author Created by: Pplociennik at 01.04.2025 20:37
 */
@Log4j2
@Service
@AllArgsConstructor
class UserSessionDetailsServiceImpl implements UserSessionDetailsService {

    /**
     * A repository interface for managing user session entities in the database.
     * Provides various methods for performing CRUD operations on user session details.
     * Acts as the data-access layer for handling {@link UserSessionDetails} objects, leveraging JPA for persistence.
     */
    private final UserSessionDetailsRepository userSessionDetailsRepository;

    /**
     * Provides access to time-related services, enabling functionalities such as retrieving
     * the current time, calculating durations, and time-specific operations required within
     * the user session management context.
     */
    private final TimeService timeService;

    /**
     * A service responsible for reading and managing system properties.
     * It provides mechanisms to access system-level configuration details,
     * ensuring a centralized and consistent approach to handle these properties
     * across the application.
     */
    private final SystemPropertiesReaderService systemPropertiesReaderService;

    /**
     * A final instance of AuthServiceRequestingService that represents the service responsible
     * for handling requests related to authentication. This variable is used to perform
     * operations or interact with the authentication service, ensuring secure request
     * processing within the system. Being final ensures its reference cannot be changed after
     * initialization.
     */
    private final KeycloakServiceConnectionService keycloakServiceConnectionService;

    /**
     * A service responsible for handling user details retrieval and management operations.
     * This variable is immutable and is intended to provide functionality for managing
     * and accessing information related to user authentication and profile details.
     */
    private final UserDetailsService userDetailsService;

    /**
     * Creates and persists details of a user session based on the provided authentication request and token.
     * The method extracts necessary information from the input parameters, creates a new session entity,
     * saves it to the repository, and maps the saved entity to its corresponding DTO.
     *
     * @param aDto
     *         the authentication request containing details required to create the session
     * @param aAuthenticationToken
     *         the authentication token containing user credentials and session information
     * @return the DTO representation of the saved user session details
     *
     * @throws NullPointerException
     *         if any of the input parameters are null
     */
    @Override
    public UserSessionDetailsDto createUserSessionDetails( @NonNull AuthenticationRequestDto aDto, @NonNull AuthenticationTokenDto aAuthenticationToken ) {
        log.info( "Creating user session details" );
        requireNonNull( aDto );
        requireNonNull( aAuthenticationToken );
        String userId = getUserId( aAuthenticationToken );
        String sessionId = getSessionId( aAuthenticationToken );
        AuthenticationDetailsDto details = aDto.getDetails();

        UserSessionDetails sessionDetails = createUserSessionDetails( aAuthenticationToken, userId, details, sessionId );
        UserSessionDetails savedSessionDetails = userSessionDetailsRepository.save( sessionDetails );
        return UserSessionDetailsMapper.mapToDto( savedSessionDetails );
    }

    /**
     * Creates user session details based on the provided data transfer object and authentication token.
     *
     * @param aDto
     *         the data transfer object containing user session details information
     * @param aAuthenticationToken
     *         the authentication token used to fetch user and session identification details
     * @return the data transfer object containing the created and saved user session details
     */
    @Override
    public UserSessionDetailsDto createUserSessionDetails( @NonNull UserSessionDetailsDto aDto, @NonNull AuthenticationTokenDto aAuthenticationToken ) {
        log.info( "Creating user session details" );
        requireNonNull( aDto );
        requireNonNull( aAuthenticationToken );
        String userId = getUserId( aAuthenticationToken );
        String sessionId = getSessionId( aAuthenticationToken );
        AuthenticationDetailsDto details = new AuthenticationDetailsDto( aDto.getLocation(), aDto.getDevice() );

        UserSessionDetails sessionDetails = createUserSessionDetails( aAuthenticationToken, userId, details, sessionId );
        UserSessionDetails savedSessionDetails = userSessionDetailsRepository.save( sessionDetails );
        return UserSessionDetailsMapper.mapToDto( savedSessionDetails );
    }

    /**
     * Retrieves a list of all user session details associated with the provided access token.
     *
     * @param aAccessToken
     *         a non-null string representing the access token used to authenticate and identify the user sessions.
     * @return a list of {@code UserSessionResponseDto} objects containing details about the user sessions,
     * such as session ID, IP address, session timing, location, and device information.
     */
    @Override
    public List< UserSessionResponseDto > getAllUserSessionsInfo( @NonNull String aAccessToken ) {
        log.debug( "getAllUserSessionDetails called" );
        requireNonNull( aAccessToken );

        String realmName = systemPropertiesReaderService.readProperty( KEYCLOAK_REALM_NAME );
        String userId = getUserId( aAccessToken );
        String clientAccessToken = keycloakServiceConnectionService.getClientAccessToken();

        WebClient client = keycloakServiceConnectionService.getAuthServiceConnectionWebClient( KeycloakUrlTemplates.GET_ALL_SESSIONS_URL_TEMPLATE, realmName, userId );
        try {
            List< UserSessionRepresentationDto > keycloakSessionsRep = client.get()
                    .header( "Authorization", clientAccessToken )
                    .retrieve()
                    .bodyToMono( new ParameterizedTypeReference< List< UserSessionRepresentationDto > >() {
                    } )
                    .block();

            return createDetailsResponses( userId, requireNonNull( keycloakSessionsRep ) );
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
                : new OnlineValidationStrategy( keycloakServiceConnectionService );
        return validateToken( strategyForExecution, aAccessToken );
    }

    /**
     * Retrieves the session details of a user associated with the specified session ID.
     *
     * @param aSessionId
     *         a non-null {@code String} representing the unique identifier of the user session.
     * @return an {@code Optional} containing a {@code UserSessionDetailsDto} if the session details are found,
     * or an empty {@code Optional} if no matching session details exist.
     */
    @Override
    public Optional< UserSessionDetailsDto > getUserSessionDetails( @NonNull String aSessionId ) {
        requireNonNull( aSessionId );
        Optional< UserSessionDetails > optionalSessionDetails = userSessionDetailsRepository.findBySessionId( aSessionId );
        UserSessionDetails sessionDetails = getOptionalValue( optionalSessionDetails );
        UserSessionDetailsDto resultDto = UserSessionDetailsMapper.mapToDto( sessionDetails );
        return Optional.ofNullable( resultDto );
    }

    /**
     * Deletes the specified user session details from the system.
     *
     * @param aSessionDetails
     *         the {@code UserSessionDetails} object representing the session details
     *         to be removed; must not be null.
     */
    @Synchronized
    @Override
    public void deleteSessionDetails( @NonNull UserSessionDetails aSessionDetails ) {
        requireNonNull( aSessionDetails );
        userSessionDetailsRepository.delete( aSessionDetails );
    }

    /**
     * Deletes the session details associated with the provided session ID.
     * This method ensures that the session ID is not null and retrieves the
     * corresponding active session details to delete from the repository.
     *
     * @param aSessionId
     *         the unique identifier of the session to be deleted; must not be null
     */
    @Override
    public void deleteSessionDetails( @NonNull String aSessionId ) {
        requireNonNull( aSessionId );
        Optional< UserSessionDetails > optionalUserSessionDetails = userSessionDetailsRepository.findBySessionId( aSessionId );
        UserSessionDetails sessionDetails = getMandatoryValue( optionalUserSessionDetails );
        userSessionDetailsRepository.delete( sessionDetails );
    }

    private boolean validateToken( @NonNull AccessTokenValidationStrategy aStrategy, @NonNull String aToken ) {
        return aStrategy.validateAccessToken( aToken );
    }

    private List< UserSessionResponseDto > createDetailsResponses( @NonNull String aUserId, @NonNull List< UserSessionRepresentationDto > aKeycloakSessionRepresentations ) {
        requireNonNull( aUserId );
        requireNonNull( aKeycloakSessionRepresentations );
        List< UserSessionDetails > detailsList = userSessionDetailsRepository.findByAuthenticatedUserId( aUserId );

        return aKeycloakSessionRepresentations.stream()
                .map( rep -> mapToResponseDto( rep, detailsList ) )
                .collect( Collectors.toList() );
    }

    private UserSessionResponseDto mapToResponseDto( UserSessionRepresentationDto aKeycloakRepresentation, List< UserSessionDetails > aDetailsList ) {
        String sessionId = aKeycloakRepresentation.getId();
        UserSessionDetails details = findDetails( aDetailsList, sessionId );

        return UserSessionResponseDto.builder()
                .id( sessionId )
                .start( aKeycloakRepresentation.getStart() )
                .lastAccess( aKeycloakRepresentation.getLastAccess() )
                .ipAddress( aKeycloakRepresentation.getIpAddress() )
                .location( details.getLocation() )
                .device( details.getDevice() )
                .build();
    }

    private UserSessionDetails findDetails( List< UserSessionDetails > aDetailsList, String aSessionId ) {
        return aDetailsList.stream()
                .filter( details -> details.getSessionId().equals( aSessionId ) )
                .findFirst()
                .orElseThrow( () -> new IllegalStateException( "No such details found" ) );
    }

    private UserSessionDetails createUserSessionDetails( AuthenticationTokenDto aAuthenticationToken, String aUserID, AuthenticationDetailsDto aDetails, String aSessionID ) {
        return UserSessionDetails.builder()
                .authenticatedUserId( aUserID )
                .location( aDetails.getLocation() )
                .device( aDetails.getDeviceName() )
                .sessionId( aSessionID )
                .refreshToken( aAuthenticationToken.getRefreshToken() )
                .build();
    }
}
