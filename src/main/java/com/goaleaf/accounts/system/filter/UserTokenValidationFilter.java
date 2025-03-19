package com.goaleaf.accounts.system.filter;

import com.github.pplociennik.commons.system.registry.CollectingSystemRegistry;
import com.goaleaf.accounts.data.dto.response.AuthenticationTokenDto;
import com.goaleaf.accounts.data.dto.user.UserSessionDetailsDto;
import com.goaleaf.accounts.service.AuthenticationService;
import com.goaleaf.accounts.service.UserSessionDetailsService;
import com.goaleaf.accounts.system.exc.auth.SessionExpiredException;
import com.goaleaf.accounts.system.util.AccessTokenUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.*;

import static com.github.pplociennik.commons.utility.OptionalUtils.getMandatoryValue;

/**
 * A custom servlet filter that intercepts incoming HTTP requests to validate user tokens, ensuring that only authenticated
 * and authorized requests are processed. This filter performs the following flow:
 * <ul>
 *     <li>Extracts the user token from the custom "User-Token" HTTP header of the incoming request.</li>
 *     <li>Validates the user token using the {@link UserSessionDetailsService} to determine if the token is active and valid.</li>
 *     <li>Handles invalid tokens by attempting to refresh them via {@code AuthenticationService} and updating session details
 *         upon successful token refresh.</li>
 *     <li>Throws a {@link SessionExpiredException} or {@link IllegalStateException} if the token cannot be validated or refreshed.</li>
 *     <li>Allows the request to proceed if the token is valid and no session issues are encountered.</li>
 * </ul>
 * This filter provides robust token management and refresh mechanisms, ensuring secure and reliable session handling
 * based on user access tokens.
 * <p>
 * Dependencies:
 * <ul>
 *     <li>{@link UserSessionDetailsService}: Responsible for user session validation and management.</li>
 *     <li>{@link AuthenticationService}: Handles user authentication and session refreshing.</li>
 * </ul>
 * <p>
 * Usage of this filter requires incoming requests to include a valid "User-Token" header. If validation or refreshing fails,
 * an exception is raised to terminate invalid sessions, ensuring the application's integrity and security.
 *
 * @author Created
 * by: Pplociennik at 13.04.2025 21:36
 */
@AllArgsConstructor
public class UserTokenValidationFilter extends OncePerRequestFilter {

    /**
     * Represents the name of the custom HTTP header used to transmit the user token.
     * This header is typically included in incoming HTTP requests to authenticate
     * or identify a user within the application.
     */
    private static final String USER_TOKEN_HEADER_NAME = "User-Token";

    /**
     * Represents the name of the request attribute used to indicate whether a user's
     * access token has been refreshed during request processing.
     * <p>
     * This attribute is primarily utilized within the {@code UserTokenValidationFilter} class
     * to track and determine if a new access token was generated and updated in the process
     * of resolving and validating user authentication.
     * <p>
     * The value associated with this constant, "USER_ACCESS_TOKEN_REFRESHED", serves as the key
     * for storing this information in the request attribute map.
     */
    private static final String TOKEN_REFRESHED_ATTRIBUTE_NAME = "USER_ACCESS_TOKEN_REFRESHED";

    /**
     * A service dependency responsible for managing user session details within the system.
     * This field is an instance of {@link UserSessionDetailsService}, which provides
     * functionality for creating, retrieving, refreshing, invalidating, and managing user
     * session details linked to authentication tokens.
     * <p>
     * The service is leveraged in the context of filtering HTTP requests where validating
     * and managing user sessions is required. It ensures proper session lifecycle handling,
     * including authentication token validation and session invalidation based on predefined
     * criteria.
     */
    private final UserSessionDetailsService userSessionDetailsService;

    /**
     * An instance of {@code AuthenticationService}, the service responsible for performing authentication-related
     * operations. It is used for tasks such as registering user accounts, authenticating user accounts,
     * and managing user session termination. This field is used within the filter to support functionalities
     * related to authentication verification and session handling during HTTP request processing.
     */
    private final AuthenticationService authenticationService;

    /**
     * A registry for managing token validation filters in the {@code UserTokenValidationFilter} class.
     * This registry is used to collect and store functionalities or filters for validating user tokens,
     * allowing flexible and modular validation operations.
     * <p>
     * The registry utilizes {@link CollectingSystemRegistry} with a {@code String} type parameter
     * to organize and manage the filter components, enabling the addition, retrieval, and organization
     * of token validation filters.
     */
    private final CollectingSystemRegistry< String > registry;

    /**
     * Can be overridden in subclasses for custom filtering control,
     * returning {@code true} to avoid filtering of the given request.
     * <p>The default implementation always returns {@code false}.
     *
     * @param aRequest
     *         current HTTP request
     * @return whether the given request should <i>not</i> be filtered
     */
    @Override
    protected boolean shouldNotFilter( HttpServletRequest aRequest ) {
        String path = aRequest.getRequestURI();
        // Execute filter only if the path is on the list
        return registry.stream()
                .filter( path::startsWith )
                .findAny()
                .isEmpty();
    }

    /**
     * Same contract as for {@code doFilter}, but guaranteed to be
     * just invoked once per request within a single request thread.
     * See {@link #shouldNotFilterAsyncDispatch()} for details.
     * <p>Provides HttpServletRequest and HttpServletResponse arguments instead of the
     * default ServletRequest and ServletResponse ones.
     *
     * @param aRequest
     *         a request to be validated
     * @param aResponse
     *         a response on the request
     * @param aFilterChain
     *         a filter chain to be executed on the request
     */
    @Override
    protected void doFilterInternal( HttpServletRequest aRequest, HttpServletResponse aResponse, FilterChain aFilterChain ) throws ServletException, IOException {
        String userAccessToken = aRequest.getHeader( USER_TOKEN_HEADER_NAME );

        boolean isTokenValid = userSessionDetailsService.checkAccessToken( userAccessToken );
        boolean isTokenRefreshed = false;
        String newUserAccessToken = null;

        if ( !isTokenValid ) {
            newUserAccessToken = resolveInvalidToken( userAccessToken );
            isTokenRefreshed = true;
        }

        String finalUserAccessToken = newUserAccessToken == null ? userAccessToken : newUserAccessToken;
        ModifiedHeaderRequestWrapper modifiedHeaderRequestWrapper = new ModifiedHeaderRequestWrapper( aRequest, finalUserAccessToken );
        modifiedHeaderRequestWrapper.setAttribute( TOKEN_REFRESHED_ATTRIBUTE_NAME, isTokenRefreshed );
        aFilterChain.doFilter( modifiedHeaderRequestWrapper, aResponse );
    }

    /**
     * Resolves an invalid user access token by refreshing it using the associated refresh token.
     * Validates the new token and updates the user's session details.
     *
     * @param aUserAccessToken
     *         the user's current access token that needs to be resolved; must not be null
     * @return the refreshed and validated access token
     *
     * @throws IllegalStateException
     *         if the token refresh process fails or the new access token is invalid
     */
    private String resolveInvalidToken( String aUserAccessToken ) {
        String sessionId = AccessTokenUtils.getSessionId( aUserAccessToken );
        Optional< UserSessionDetailsDto > optionalDetails = userSessionDetailsService.getUserSessionDetails( sessionId );
        UserSessionDetailsDto details = getMandatoryValue( optionalDetails );

        String refreshToken = details.getRefreshToken();
        validateRefreshToken( aUserAccessToken, refreshToken );

        AuthenticationTokenDto refreshedToken = authenticationService.refreshUserSession( aUserAccessToken );
        String newUserAccessToken = refreshedToken.getAccessToken();

        boolean isNewUserAccessTokenValid = userSessionDetailsService.checkAccessToken( newUserAccessToken );
        if ( !isNewUserAccessTokenValid ) {
            throw new IllegalStateException( "Token refresh failed." );
        }

        userSessionDetailsService.deleteSessionDetails( sessionId );
        userSessionDetailsService.createUserSessionDetails( details, refreshedToken );

        return newUserAccessToken;
    }

    /**
     * Validates the provided refresh token to ensure its validity and deletes the corresponding
     * user session if the token is invalid. If the refresh token is invalid, a
     * {@code SessionExpiredException} is thrown.
     *
     * @param aUserAccessToken
     *         the user's current access token used to retrieve the session ID; must not be null
     * @param aRefreshToken
     *         the refresh token to validate; must not be null
     * @throws SessionExpiredException
     *         if the refresh token is invalid, indicating an expired session
     */
    private void validateRefreshToken( String aUserAccessToken, String aRefreshToken ) {
        boolean isTokenValid = userSessionDetailsService.checkAccessToken( aRefreshToken );

        if ( !isTokenValid ) {
            String sessionId = AccessTokenUtils.getSessionId( aUserAccessToken );
            authenticationService.deleteUserSession( sessionId );
            throw new SessionExpiredException();
        }
    }

    /**
     * A private wrapper class for {@link HttpServletRequest} that overrides the behavior of the
     * {@code getHeader} method to return a modified value for a specific header field. This class
     * is designed to replace the incoming user access token header with a new value, while delegating
     * all other header requests to the original request.
     * <p>
     * It is primarily used within the {@code UserTokenValidationFilter} class to handle scenarios
     * where the user access token needs to be updated and passed along in the filter chain.
     */
    protected static class ModifiedHeaderRequestWrapper extends HttpServletRequestWrapper {

        /**
         * Represents a new user access token that is used to replace or override
         * the existing token in the incoming HTTP request. This token is injected
         * into the {@code getHeader} and {@code getHeaders} methods when the
         * specified header name matches the user token header.
         * <p>
         * This field is immutable and is intended to store the replacement value
         * for the user access token header within the {@code ModifiedHeaderRequestWrapper}.
         */
        private final String newUserAccessToken;

        /**
         * A map for storing attribute key-value pairs.
         * The keys are strings representing attribute names, and the values are objects representing
         * the corresponding attribute values. This map is used for managing custom attributes
         * associated with the request.
         */
        private final Map< String, Object > attributes = new HashMap<>();


        /**
         * A constructor. Creates a new instance of the class.
         *
         * @param request
         *         a request
         * @param aNewUserAccessToken
         *         a new token after refreshing
         */
        public ModifiedHeaderRequestWrapper( HttpServletRequest request, String aNewUserAccessToken ) {
            super( request );
            this.newUserAccessToken = aNewUserAccessToken;
        }

        /**
         * Retrieves the value of the specified HTTP header. If the provided header name matches
         * the user access token header name, this method returns the updated user access token.
         * Otherwise, it delegates the call to the parent implementation to retrieve the original
         * header value.
         *
         * @param name
         *         the name of the header to retrieve
         * @return the value of the specified header; if the header name matches the user token
         * header name, the updated token is returned; otherwise, returns the original
         * header value from the parent implementation
         */
        @Override
        public String getHeader( String name ) {
            if ( USER_TOKEN_HEADER_NAME.equals( name ) ) {
                return newUserAccessToken;
            }
            return super.getHeader( name );
        }

        /**
         * Retrieves all the values of the specified HTTP header as an enumeration of strings.
         * If the provided header name matches the user access token header name, this method
         * returns an enumeration containing only the updated user access token. Otherwise,
         * it delegates the call to the parent implementation to retrieve the original header values.
         *
         * @param name
         *         the name of the header to retrieve values for
         * @return an enumeration of strings containing all the values of the specified header;
         * if the header name matches the user token header name, an enumeration with
         * the updated token is returned; otherwise, the original value(s) from the
         * parent implementation are returned
         */
        @Override
        public Enumeration< String > getHeaders( String name ) {
            if ( USER_TOKEN_HEADER_NAME.equals( name ) ) {
                return Collections.enumeration( Collections.singletonList( newUserAccessToken ) );
            }
            return super.getHeaders( name );
        }

        /**
         * Retrieves an enumeration of all the HTTP header names associated with the request.
         * If the user access token header name is not already present, it is added to the
         * list of header names.
         *
         * @return an enumeration of all HTTP header names, including the user token header name
         * if it is not already present
         */
        @Override
        public Enumeration< String > getHeaderNames() {
            Vector< String > headerNames = new Vector<>( Collections.list( super.getHeaderNames() ) );

            if ( !headerNames.contains( USER_TOKEN_HEADER_NAME ) ) {
                headerNames.add( USER_TOKEN_HEADER_NAME );
            }

            return headerNames.elements();
        }

        /**
         * Sets a request attribute with the specified name and value.
         * If an attribute with the same name already exists, its value will be replaced.
         *
         * @param name
         *         the name of the attribute to be set; must not be null
         * @param value
         *         the value of the attribute to be set; may be null
         */
        @Override
        public void setAttribute( String name, Object value ) {
            attributes.put( name, value );
        }

        /**
         * Retrieves the value of the specified request attribute.
         *
         * @param name
         *         the name of the attribute to retrieve; must not be null
         * @return the value of the specified attribute, or null if no attribute
         * exists with the given name
         */
        @Override
        public Object getAttribute( String name ) {
            return attributes.get( name );
        }

    }
}
