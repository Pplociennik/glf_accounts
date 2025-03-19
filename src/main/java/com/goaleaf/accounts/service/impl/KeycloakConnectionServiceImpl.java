package com.goaleaf.accounts.service.impl;

import com.github.pplociennik.commons.lang.CommonsResExcMsgTranslationKey;
import com.github.pplociennik.commons.service.SystemPropertiesReaderService;
import com.goaleaf.accounts.data.dto.response.AuthenticationTokenDto;
import com.goaleaf.accounts.data.dto.response.KeycloakErrorResponseDto;
import com.goaleaf.accounts.service.KeycloakServiceConnectionService;
import com.goaleaf.accounts.system.exc.request.KeycloakActionRequestFailedException;
import com.goaleaf.accounts.system.exc.request.TokenRefreshFailedException;
import com.goaleaf.accounts.system.util.AccessTokenUtils;
import com.goaleaf.accounts.system.util.KeycloakUrlTemplates;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Base64;
import java.util.Map;

import static com.goaleaf.accounts.system.properties.AccountsSystemProperties.*;
import static com.goaleaf.accounts.system.util.KeycloakUrlTemplates.*;
import static com.goaleaf.accounts.system.util.UrlTemplatesResolverUtil.resolveUrlTemplate;
import static java.util.Objects.requireNonNull;

/**
 * Implementation of {@link KeycloakServiceConnectionService} that communicates with a Keycloak server
 * to handle user authentication and related service requests.
 * <p>
 * This service provides methods to send authentication requests and to establish connections with
 * the authentication service using the WebClient API. The specific connection details are managed
 * using configuration properties.
 * <p>
 * Constants:
 * - REGISTER_URL_TEMPLATE: Template for the Keycloak user registration endpoint URL.
 * - AUTHENTICATION_URL_TEMPLATE: Template for the Keycloak authentication endpoint URL.
 * <p>
 * Dependencies:
 * - {@link SystemPropertiesReaderService}: Service used to read system properties for constructing
 * the necessary URLs and settings.
 */
@Log4j2
@Service
@AllArgsConstructor
class KeycloakConnectionServiceImpl implements KeycloakServiceConnectionService {

    /**
     * A service responsible for reading and accessing system properties.
     */
    private final SystemPropertiesReaderService systemPropertiesReaderService;

    /**
     * Creates and retrieves a configured instance of a {@code WebClient} for interacting with
     * an authentication service using the specified {@code KeycloakUrlTemplates} and parameters.
     *
     * @param aUrlTemplate
     *         a non-null {@code KeycloakUrlTemplates} object representing the URL template for the authentication service endpoint.
     * @param aParameters
     *         a non-null varargs array of {@code Object} containing parameters to format the URL template.
     * @return a configured {@code WebClient} instance for connecting to the authentication service.
     */
    @Override
    public WebClient getAuthServiceConnectionWebClient( @NonNull KeycloakUrlTemplates aUrlTemplate, @NonNull Object... aParameters ) {
        requireNonNull( aUrlTemplate );
        requireNonNull( aParameters );
        String keycloakBaseUrl = systemPropertiesReaderService.readProperty( AUTH_SERVICE_URL );
        String forwardingUrl = resolveUrlTemplate( aUrlTemplate, aParameters );
        return WebClient.builder().baseUrl( keycloakBaseUrl + "/" + forwardingUrl ).build();
    }

    /**
     * Sends a request to refresh the authentication tokens using the provided refresh token.
     *
     * @param aRefreshToken
     *         a non-null {@code String} representing the refresh token to be used for obtaining new authentication tokens.
     * @return an {@code AuthenticationTokenDto} object containing the refreshed authentication details including
     * access token, refresh token, token type, expiration information, and related metadata.
     */
    @Override
    public AuthenticationTokenDto sendRefreshTokenRequest( @NonNull String aRefreshToken ) {
        requireNonNull( aRefreshToken );
        final String grantType = "refresh_token";
        String realmName = systemPropertiesReaderService.readProperty( KEYCLOAK_REALM_NAME );
        String clientId = systemPropertiesReaderService.readProperty( KEYCLOAK_CLIENT_ID );
        String clientSecret = systemPropertiesReaderService.readProperty( KEYCLOAK_CLIENT_SECRET );

        WebClient client = getAuthServiceConnectionWebClient( REFRESH_SESSION_URL_TEMPLATE, realmName );

        try {
            return client.post()
                    .contentType( MediaType.APPLICATION_FORM_URLENCODED )
                    .bodyValue(
                            "grant_type=" + grantType
                                    + "&client_id=" + clientId
                                    + "&client_secret=" + clientSecret
                                    + "&refresh_token=" + aRefreshToken
                    )
                    .retrieve()
                    .bodyToMono( AuthenticationTokenDto.class )
                    .block();
        } catch ( WebClientResponseException aE ) {
            KeycloakErrorResponseDto errorResponse = aE.getResponseBodyAs( KeycloakErrorResponseDto.class );
            requireNonNull( errorResponse );
            throw new TokenRefreshFailedException( CommonsResExcMsgTranslationKey.UNEXPECTED_EXCEPTION, errorResponse.getErrorDescription() );
        }
    }

    /**
     * Sends a token introspection request to validate the provided token.
     *
     * @param aToken
     *         a non-null {@code String} representing the token to be introspected.
     * @return {@code true} if the token is valid based on the introspection result; {@code false} otherwise.
     */
    @Override
    public boolean sendTokenIntrospectionRequest( @NonNull String aToken ) {
        requireNonNull( aToken );
        String realmName = systemPropertiesReaderService.readProperty( KEYCLOAK_REALM_NAME );
        WebClient client = getAuthServiceConnectionWebClient( INTROSPECT_TOKEN_TEMPLATE, realmName );

        String clientId = systemPropertiesReaderService.readProperty( KEYCLOAK_CLIENT_ID );
        String clientSecret = systemPropertiesReaderService.readProperty( KEYCLOAK_CLIENT_SECRET );
        String credentials = Base64.getEncoder().encodeToString( ( clientId + ":" + clientSecret ).getBytes() );

        try {
            return Boolean.TRUE.equals( client.post()
                    .header( "Authorization", "Basic " + credentials )
                    .contentType( MediaType.APPLICATION_FORM_URLENCODED )
                    .bodyValue( "token=" + aToken )
                    .retrieve()
                    .bodyToMono( new ParameterizedTypeReference< Map< String, Object > >() {
                    } )
                    .map( responseMap -> ( boolean ) responseMap.getOrDefault( "active", false ) )
                    .onErrorReturn( false )
                    .block() );
        } catch ( WebClientResponseException aE ) {
            KeycloakErrorResponseDto errorResponse = aE.getResponseBodyAs( KeycloakErrorResponseDto.class );
            requireNonNull( errorResponse );
            throw new KeycloakActionRequestFailedException( CommonsResExcMsgTranslationKey.UNEXPECTED_EXCEPTION, errorResponse.getErrorDescription() );
        }

    }

    /**
     * Sends a request to delete a session identified by its unique session ID.
     * This method is used to remove an active session when it is no longer valid and cannot be refreshed.
     *
     * @param aSessionId
     *         a non-null {@code String} representing the unique identifier of the session to be deleted.
     */
    @Override
    public void sendSessionDeletionRequest( @NonNull String aSessionId ) {
        requireNonNull( aSessionId );
        String clientAccessToken = getClientAccessToken();
        String realmName = systemPropertiesReaderService.readProperty( KEYCLOAK_REALM_NAME );

        WebClient client = getAuthServiceConnectionWebClient( DELETE_SESSION_TEMPLATE, realmName, aSessionId );
        try {
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
    }

    /**
     * Retrieves the client access token by sending a client authentication request.
     * The token is returned with a predefined prefix.
     *
     * @return the client access token prefixed with the defined access token prefix
     */
    @Override
    public String getClientAccessToken() {
        log.info( "Getting Client Access Token" );
        AuthenticationTokenDto authenticationTokenDto = sendClientAuthenticationRequest();
        return AccessTokenUtils.ACCESS_TOKEN_PREFIX + " " + authenticationTokenDto.getAccessToken();
    }

    private AuthenticationTokenDto sendClientAuthenticationRequest() {
        log.info( "Sending Client Authentication Request" );
        String realmName = systemPropertiesReaderService.readProperty( KEYCLOAK_REALM_NAME );
        String clientId = systemPropertiesReaderService.readProperty( KEYCLOAK_CLIENT_ID );
        String clientSecret = systemPropertiesReaderService.readProperty( KEYCLOAK_CLIENT_SECRET );
        String grantType = systemPropertiesReaderService.readProperty( KEYCLOAK_GRANT_TYPE );
        String scope = systemPropertiesReaderService.readProperty( KEYCLOAK_SCOPE );

        WebClient client = getAuthServiceConnectionWebClient( AUTHENTICATION_URL_TEMPLATE, realmName );
        try {
            return client.post()
                    .contentType( MediaType.APPLICATION_FORM_URLENCODED )
                    .bodyValue(
                            "grant_type=" + grantType
                                    + "&client_id=" + clientId
                                    + "&client_secret=" + clientSecret
                                    + "&scope=" + scope
                    )
                    .retrieve()
                    .bodyToMono( AuthenticationTokenDto.class )
                    .block();
        } catch ( WebClientResponseException aE ) {
            KeycloakErrorResponseDto errorResponse = aE.getResponseBodyAs( KeycloakErrorResponseDto.class );
            requireNonNull( errorResponse );
            throw new KeycloakActionRequestFailedException( CommonsResExcMsgTranslationKey.UNEXPECTED_EXCEPTION, errorResponse.getErrorDescription() );
        }
    }

}
