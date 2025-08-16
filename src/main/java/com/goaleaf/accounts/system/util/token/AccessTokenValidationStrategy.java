package com.goaleaf.accounts.system.util.token;

import org.springframework.lang.NonNull;

/**
 * Strategy interface for validating access tokens within the authentication system.
 * Implementations of this interface provide the logic to determine whether
 * a given access token is valid and not expired.
 * This is a critical component of secure authentication workflows.
 *
 * @author Created by: Pplociennik at 16.04.2025 17:49
 */
public interface AccessTokenValidationStrategy {

    /**
     * Validates the given access token to ensure it is not expired.
     *
     * @param aToken
     *         the access token to be validated; must not be null
     * @return true if the access token is valid, false otherwise
     */
    boolean validateAccessToken( @NonNull String aToken );
}
