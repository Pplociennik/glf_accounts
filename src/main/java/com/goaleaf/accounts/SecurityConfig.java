package com.goaleaf.accounts;

import com.github.pplociennik.commons.system.registry.CollectingSystemRegistry;
import com.goaleaf.accounts.service.AuthenticationService;
import com.goaleaf.accounts.service.UserSessionDetailsService;
import com.goaleaf.accounts.system.filter.UserTokenValidationFilter;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

/**
 * A configuration of endpoint's security.
 *
 * @author Created by: Pplociennik at 27.04.2025 22:51
 */
@Configuration
@EnableWebSecurity
@AllArgsConstructor
class SecurityConfig {

    /**
     * Provides functionality to manage and interact with user session details.
     * This service is used for operations such as validating user tokens,
     * retrieving user session information, and handling session deactivation or updates.
     * <p>
     * Used specifically in security filters for scenarios such as session refresh,
     * session termination, and access token validation to enforce security rules and policies.
     * <p>
     * Serves as a dependency for ensuring secure and controlled access to user-specific session data
     * across various security filter chains.
     */
    private final UserSessionDetailsService userSessionDetailsService;
    /**
     * Provides authentication-related functionalities required for securing API endpoints.
     * <p>
     * This service is used to:
     * - Authenticate user accounts during login.
     * - Register new user accounts.
     * - Terminate user sessions, including all associated sessions or specific ones.
     * - Refresh user sessions by regenerating authentication tokens.
     * - Delete
     */
    private final AuthenticationService authenticationService;

    /**
     * A registry for managing token validation filters in the security configuration.
     * This variable holds a collection of token validation filters mapped to their corresponding keys,
     * allowing dynamic configuration and retrieval of filters used for token validation in
     * secured endpoints.
     */
    private CollectingSystemRegistry< String > tokenValidationFilterRegistry;

// #####################################################################################################################

    /**
     * Configures security settings for the registration endpoint.
     * Allows unrestricted access to the registration API endpoint.
     *
     * @param aHttp
     *         the HttpSecurity to configure
     * @return the configured HttpSecurity object
     *
     * @throws Exception
     *         if an error occurs during configuration
     */
    private HttpSecurity configureRegistrationEndpoint( HttpSecurity aHttp ) throws Exception {
        aHttp.authorizeHttpRequests( auth ->
                auth.requestMatchers( "/api/auth/register" )
                        .permitAll() );
        return aHttp;
    }

    /**
     * Configures security settings for the login endpoint.
     * Allows unrestricted access to the login API endpoint.
     *
     * @param aHttp
     *         the HttpSecurity to configure
     * @return the configured HttpSecurity object
     *
     * @throws Exception
     *         if an error occurs during configuration
     */
    private HttpSecurity configureLoginEndpoint( HttpSecurity aHttp ) throws Exception {
        aHttp.authorizeHttpRequests( auth ->
                auth.requestMatchers( "/api/auth/login" )
                        .permitAll() );
        return aHttp;
    }

    /**
     * Configures security settings for the token refresh endpoint.
     * Allows unrestricted access to the session refresh API endpoint.
     *
     * @param aHttp
     *         the HttpSecurity to configure
     * @return the configured HttpSecurity object
     *
     * @throws Exception
     *         if an error occurs during configuration
     */
    private HttpSecurity configureUserAccessTokenRefreshEndpoint( HttpSecurity aHttp ) throws Exception {
        aHttp.authorizeHttpRequests( auth ->
                auth.requestMatchers( "/api/auth/session/refresh" )
                        .permitAll() );
        return aHttp;
    }

    /**
     * Configures security settings for the endpoint that terminates all user sessions.
     * Requires authentication to access this endpoint.
     *
     * @param aHttp
     *         the HttpSecurity to configure
     * @return the configured HttpSecurity object
     *
     * @throws Exception
     *         if an error occurs during configuration
     */
    private HttpSecurity configureTerminateAllUserSessionsEndpoint( HttpSecurity aHttp ) throws Exception {
        aHttp.authorizeHttpRequests( auth ->
                auth.requestMatchers( "/api/auth/logout/all" )
                        .permitAll() );
        tokenValidationFilterRegistry.add( "/api/auth/logout/all" );
        return aHttp;
    }

    /**
     * Configures security settings for the endpoint that retrieves all user sessions.
     * Allows unrestricted access to the "/api/sessions/all" API endpoint.
     *
     * @param aHttp
     *         the HttpSecurity to configure
     * @return the configured HttpSecurity object
     *
     * @throws Exception
     *         if an error occurs during configuration
     */
    private HttpSecurity configureGetAllUserSessionsEndpoint( HttpSecurity aHttp ) throws Exception {
        aHttp.authorizeHttpRequests( auth ->
                auth.requestMatchers( "/api/sessions/all" )
                        .permitAll() );
        tokenValidationFilterRegistry.add( "/api/sessions/all" );
        return aHttp;
    }

    /**
     * Configures security settings for the endpoint that terminates a current user session.
     * Requires authentication to access this endpoint.
     *
     * @param aHttp
     *         the HttpSecurity to configure
     * @return the configured HttpSecurity object
     *
     * @throws Exception
     *         if an error occurs during configuration
     */
    private HttpSecurity configureTerminateCurrentUserSessionEndpoint( HttpSecurity aHttp ) throws Exception {
        aHttp.authorizeHttpRequests( auth ->
                auth.requestMatchers( "/api/auth/logout" )
                        .permitAll() );
        tokenValidationFilterRegistry.add( "/api/auth/logout" );
        return aHttp;
    }

    /**
     * Configures security settings for the endpoint that terminates a current user session.
     * Requires authentication to access this endpoint.
     *
     * @param aHttp
     *         the HttpSecurity to configure
     * @return the configured HttpSecurity object
     *
     * @throws Exception
     *         if an error occurs during configuration
     */
    private HttpSecurity configureTerminateAnyUserSessionEndpoint( HttpSecurity aHttp ) throws Exception {
        aHttp.authorizeHttpRequests( auth ->
                auth.requestMatchers( "/api/auth/logout-session" )
                        .permitAll() );
        tokenValidationFilterRegistry.add( "/api/auth/logout-session" );
        return aHttp;
    }

    /**
     * Configures security settings for the email confirmation request endpoint.
     * Allows unrestricted access to the "/api/accounts/email-confirmation/request" API endpoint.
     *
     * @param aHttp
     *         the HttpSecurity to configure
     * @return the configured HttpSecurity object
     *
     * @throws Exception
     *         if an error occurs during configuration
     */
    private HttpSecurity configureEmailConfirmationRequest( HttpSecurity aHttp ) throws Exception {
        aHttp.authorizeHttpRequests( auth ->
                auth.requestMatchers( "/api/accounts/email-confirmation/request" )
                        .permitAll() );
        return aHttp;
    }

    /**
     * Configures security settings for the email confirmation endpoint.
     * Allows unrestricted access to the "/api/accounts/confirm" API endpoint.
     *
     * @param aHttp
     *         the HttpSecurity to configure
     * @return the configured HttpSecurity object
     *
     * @throws Exception
     *         if an error occurs during configuration
     */
    private HttpSecurity configureEmailConfirmation( HttpSecurity aHttp ) throws Exception {
        aHttp.authorizeHttpRequests( auth ->
                auth.requestMatchers( "/api/accounts/confirm" )
                        .permitAll() );
        tokenValidationFilterRegistry.add( "/api/accounts/confirm" );
        return aHttp;
    }

    /**
     * Configures security settings for the password change endpoint.
     * Allows unrestricted access to the "/api/accounts/password/change" API endpoint.
     *
     * @param aHttp
     *         the HttpSecurity to configure
     * @return the configured HttpSecurity object
     *
     * @throws Exception
     *         if an error occurs during configuration
     */
    private HttpSecurity configurePasswordChange( HttpSecurity aHttp ) throws Exception {
        aHttp.authorizeHttpRequests( auth ->
                auth.requestMatchers( "/api/accounts/password/change" )
                        .permitAll() );
        tokenValidationFilterRegistry.add( "/api/accounts/password/change" );
        return aHttp;
    }

    /**
     * Configures security settings for the password reset endpoint.
     * Allows unrestricted access to the "/api/accounts/password/reset" API endpoint.
     *
     * @param aHttp
     *         the HttpSecurity to configure
     * @return the configured HttpSecurity object
     *
     * @throws Exception
     *         if an error occurs during configuration
     */
    private HttpSecurity configurePasswordReset( HttpSecurity aHttp ) throws Exception {
        aHttp.authorizeHttpRequests( auth ->
                auth.requestMatchers( "/api/accounts/password/reset" )
                        .permitAll() );
        return aHttp;
    }

    /**
     * Configures the token validation filter for specific endpoints.
     * Adds a custom filter to validate user tokens for protected endpoints.
     *
     * @param aHttp
     *         the HttpSecurity to configure
     * @return the configured HttpSecurity object
     *
     * @throws Exception
     *         if an error occurs during configuration
     */
    private HttpSecurity configureTokenValidationFilter( HttpSecurity aHttp ) {
        aHttp.addFilterAt( new UserTokenValidationFilter( userSessionDetailsService, authenticationService, tokenValidationFilterRegistry ), BasicAuthenticationFilter.class );
        return aHttp;
    }

    /**
     * Configures security settings for actuator endpoints.
     * Allows unrestricted access to the actuator API endpoints, including health checks.
     *
     * @param aHttp
     *         the HttpSecurity to configure
     * @return the configured HttpSecurity object
     *
     * @throws Exception
     *         if an error occurs during configuration
     */
    private HttpSecurity configureActuatorEndpoint( HttpSecurity aHttp ) throws Exception {
        aHttp.authorizeHttpRequests( auth ->
                auth.requestMatchers( "/actuator/**", "/actuator/health/**" ).permitAll() );
        return aHttp;
    }

    /**
     * Configures security settings for the Eureka endpoint.
     * Allows unrestricted access to specific Eureka-related API endpoints.
     *
     * @param aHttp
     *         the HttpSecurity to configure
     * @return the configured HttpSecurity object
     *
     * @throws Exception
     *         if an error occurs during configuration
     */
    private HttpSecurity configureEurekaEndpoint( HttpSecurity aHttp ) throws Exception {
        aHttp.authorizeHttpRequests( auth ->
                auth.requestMatchers( "/eureka/**" ).permitAll()
                        .requestMatchers( "/" ).permitAll()
                        .requestMatchers( "/instance/**" ).permitAll()
                        .requestMatchers( "/apps/**" ).permitAll() );
        return aHttp;
    }

// #####################################################################################################################

    /**
     * Configures the main security filter chain for the application.
     * Sets up all security configurations, including endpoint access rules and custom filters.
     *
     * @param aHttp
     *         the HttpSecurity to configure
     * @return the configured SecurityFilterChain
     *
     * @throws Exception
     *         if an error occurs during configuration
     */
    @Bean
    public SecurityFilterChain securityWebFilterChain(
            HttpSecurity aHttp ) throws Exception {

        aHttp.csrf( AbstractHttpConfigurer::disable );

        // Service Authentication API access config
        configureRegistrationEndpoint( aHttp );
        configureLoginEndpoint( aHttp );
        configureUserAccessTokenRefreshEndpoint( aHttp );
        configureTerminateAllUserSessionsEndpoint( aHttp );
        configureTerminateCurrentUserSessionEndpoint( aHttp );
        configureTerminateAnyUserSessionEndpoint( aHttp );

        // Service Sessions API access config
        configureGetAllUserSessionsEndpoint( aHttp );

        // Service Accounts Management config
        configureEmailConfirmationRequest( aHttp );
        configureEmailConfirmation( aHttp );
        configurePasswordChange( aHttp );
        configurePasswordReset( aHttp );

        // System API config
        configureActuatorEndpoint( aHttp );
        configureEurekaEndpoint( aHttp );

        // All other requests
        aHttp.authorizeHttpRequests( auth ->
                auth.anyRequest().authenticated()
        );

        // Additional filters config
        configureTokenValidationFilter( aHttp );

        return aHttp.build();

    }

}
