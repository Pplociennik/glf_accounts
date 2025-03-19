package com.goaleaf.accounts.system.util.token;

import com.github.pplociennik.commons.service.TimeService;
import com.goaleaf.accounts.system.util.AccessTokenUtils;
import lombok.AllArgsConstructor;
import org.springframework.lang.NonNull;

import java.time.Instant;

import static java.util.Objects.requireNonNull;

/**
 * A strategy implementation for validating access tokens offline.
 * <p>
 * This class uses the {@link TimeService} to determine the current time and compares it
 * with the expiration time extracted from the provided access token using the {@link AccessTokenUtils}.
 * It ensures that the token is still valid and has not expired.
 * </p>
 *
 * @author Created by: Pplociennik
 * @date 16.04.2025 17:53
 */
@AllArgsConstructor
public final class OfflineValidationStrategy implements AccessTokenValidationStrategy {

    /**
     * Time service. Provides access to time-related utility functions.
     */
    private final TimeService timeService;

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
        Instant currentSystemTime = timeService.getCurrentSystemDateTime().toInstant();
        Instant tokenExpirationTime = AccessTokenUtils.getExpirationTime( aToken );

        return tokenExpirationTime.isAfter( currentSystemTime );
    }
}
