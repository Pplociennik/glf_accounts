package com.goaleaf.accounts.system.util.token;

/**
 * Enumeration that defines the strategies for validating access tokens
 * within the authentication system. Each strategy represents a different
 * approach to ensure the token's validity:
 * <p>
 * - {@link #OFFLINE}: Validates tokens using local resources without external service dependencies.
 * - {@link #ONLINE}: Validates tokens by engaging with an external authentication service.
 * <p>
 * These strategies help balance performance and security based on system requirements.
 *
 * @author Created by: Pplociennik at 16.04.2025 18:22
 */
public enum TokenValidationStrategy {

    /**
     * Represents the offline token validation strategy.
     * This strategy is used to validate the access tokens without relying on external services
     * by utilizing local resources to verify the token's expiration and validity.
     */
    OFFLINE,

    /**
     * Represents the online token validation strategy.
     * This strategy validates access tokens by interacting with an external authentication service.
     * It relies on active communication with the authentication system to verify the token's validity
     * and ensure it is active rather than expired.
     */
    ONLINE
}
