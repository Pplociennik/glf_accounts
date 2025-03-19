package com.goaleaf.accounts.system.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.goaleaf.accounts.data.dto.response.AuthenticationTokenDto;
import org.springframework.lang.NonNull;

import java.time.Instant;

import static java.util.Objects.requireNonNull;

/**
 * Utility class for handling operations related to access tokens.
 * Provides methods for extracting session IDs and user IDs from access tokens.
 * This class acts as a utility bridge for JWT-based token processing.
 *
 * <p>Note: This class assumes that the incoming access tokens are in JWT format
 * and signed using a compatible mechanism for decoding to work properly.</p>
 *
 * @author Created by: Pplociennik at 02.04.2025 19:42
 */
public final class AccessTokenUtils {

    /**
     * A constant representing the prefix used for access tokens in the API.
     * This value is typically included in the `Authorization` header to indicate
     * that the provided token is a Bearer Token, as per the OAuth 2.0 standard.
     * <p>
     * Usage:
     * <br>
     * - Specifies the token type when formatting authorization credentials.
     * <br>
     * - Ensures proper identification and validation of access tokens by the server.
     * <p>
     * The prefix is combined with the actual token, separated by a space,
     * and passed as part of authenticated API requests.
     */
    public static final String ACCESS_TOKEN_PREFIX = "Bearer";

    /**
     * Extracts the session ID from the given authentication token.
     *
     * @param aAuthenticationToken
     *         the authentication token containing the user's session information; must not be null
     * @return the session ID extracted from the token
     *
     * @throws NullPointerException
     *         if the provided authentication token is null
     */
    public static String getSessionId( @NonNull AuthenticationTokenDto aAuthenticationToken ) {
        requireNonNull( aAuthenticationToken );
        String accessToken = aAuthenticationToken.getAccessToken();
        return getSessionId( accessToken );
    }

    /**
     * Extracts the session ID from the provided access token.
     *
     * @param aAccessToken
     *         a non-null string representing the JWT access token; must not be null
     * @return the session ID extracted from the token
     *
     * @throws NullPointerException
     *         if the provided access token is null
     */
    public static String getSessionId( @NonNull String aAccessToken ) {
        requireNonNull( aAccessToken );
        DecodedJWT decodedJWT = JWT.decode( aAccessToken );
        return decodedJWT.getClaim( "sid" ).asString();
    }

    /**
     * Extracts the user ID from the given authentication token.
     *
     * @param aAuthenticationToken
     *         the authentication token containing the user's identification information; must not be null
     * @return the user ID extracted from the token
     *
     * @throws NullPointerException
     *         if the provided authentication token is null
     */
    public static String getUserId( @NonNull AuthenticationTokenDto aAuthenticationToken ) {
        requireNonNull( aAuthenticationToken );
        String accessToken = aAuthenticationToken.getAccessToken();
        return getUserId( accessToken );
    }

    /**
     * Extracts the user ID from the provided access token.
     *
     * @param aAccessToken
     *         a non-null string representing the JWT access token; must not be null
     * @return the user ID (subject) extracted from the token
     *
     * @throws NullPointerException
     *         if the provided access token is null
     */
    public static String getUserId( @NonNull String aAccessToken ) {
        requireNonNull( aAccessToken );
        String withoutPrefix = aAccessToken.contains( ACCESS_TOKEN_PREFIX ) ? getTokenWithoutPrefix( aAccessToken ) : aAccessToken;
        DecodedJWT decodedJWT = JWT.decode( withoutPrefix );
        return decodedJWT.getSubject();
    }

    /**
     * Retrieves the expiration time of a JWT access token.
     *
     * @param aAccessToken
     *         a non-null string representing the JWT access token; must not be null
     * @return an {@code Instant} representing the expiration timestamp of the token
     *
     * @throws NullPointerException
     *         if the provided access token is null
     */
    public static Instant getExpirationTime( @NonNull String aAccessToken ) {
        requireNonNull( aAccessToken );
        DecodedJWT decodedJWT = JWT.decode( aAccessToken );
        return decodedJWT.getExpiresAtAsInstant();
    }

    private static String getTokenWithoutPrefix( String aAccessToken ) {
        return aAccessToken.split( " " )[ 1 ];
    }
}
