package com.goaleaf.accounts.service;

import com.goaleaf.accounts.data.dto.response.AuthenticationTokenDto;
import com.goaleaf.accounts.system.util.KeycloakUrlTemplates;
import org.springframework.lang.NonNull;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * An interface that defines the operations for connecting to the keycloak server and processing the base token and session operations.
 */
public interface KeycloakServiceConnectionService {

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
    WebClient getAuthServiceConnectionWebClient( @NonNull KeycloakUrlTemplates aUrlTemplate, @NonNull Object... aParameters );

    /**
     * Sends a request to refresh the authentication tokens using the provided refresh token.
     *
     * @param aRefreshToken
     *         a non-null {@code String} representing the refresh token to be used for obtaining new authentication tokens.
     * @return an {@code AuthenticationTokenDto} object containing the refreshed authentication details including
     * access token, refresh token, token type, expiration information, and related metadata.
     */
    AuthenticationTokenDto sendRefreshTokenRequest( @NonNull String aRefreshToken );

    /**
     * Sends a token introspection request to validate the provided token.
     *
     * @param aToken
     *         a non-null {@code String} representing the token to be introspected.
     * @return {@code true} if the token is valid based on the introspection result; {@code false} otherwise.
     */
    boolean sendTokenIntrospectionRequest( @NonNull String aToken );

    /**
     * Sends a request to delete a session identified by its unique session ID.
     * This method is used to remove an active session when it is no longer valid and cannot be refreshed.
     *
     * @param aSessionId
     *         a non-null {@code String} representing the unique identifier of the session to be deleted.
     */
    void sendSessionDeletionRequest( @NonNull String aSessionId );

    /**
     * Retrieves the client access token by sending a client authentication request.
     * The token is returned with a predefined prefix.
     *
     * @return the client access token prefixed with the defined access token prefix
     */
    String getClientAccessToken();
}
