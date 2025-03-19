package com.goaleaf.accounts.system.util.token;

import com.goaleaf.accounts.service.KeycloakServiceConnectionService;
import lombok.AllArgsConstructor;
import org.springframework.lang.NonNull;

import static java.util.Objects.requireNonNull;

/**
 * This class provides a strategy for validating access tokens through an online service.
 * An implementation of {@link AccessTokenValidationStrategy}, it uses the {@link KeycloakServiceConnectionService}
 * to interact with an external authentication system to validate whether a given token is active or expired.
 *
 * @author Created by: Pplociennik at 16.04.2025 17:58
 * @implNote The {@link OnlineValidationStrategy} relies on {@link KeycloakServiceConnectionService} for communication
 * with the authentication service.
 */
@AllArgsConstructor
public final class OnlineValidationStrategy implements AccessTokenValidationStrategy {

    /**
     * An instance of {@link  KeycloakServiceConnectionService} used for interacting with
     * the authentication service.
     */
    private final KeycloakServiceConnectionService keycloakServiceConnectionService;

    /**
     * Validates the given access token to ensure it is not expired.
     *
     * @param aToken
     *         the access token to be validated; must not be null
     * @return true if the access token is valid, false otherwise
     */
    @Override
    public boolean validateAccessToken( @NonNull String aToken ) {
        requireNonNull( aToken );
        return keycloakServiceConnectionService.sendTokenIntrospectionRequest( aToken );
    }
}
