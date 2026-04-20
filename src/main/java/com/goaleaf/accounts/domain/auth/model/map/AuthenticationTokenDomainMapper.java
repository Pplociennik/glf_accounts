package com.goaleaf.accounts.domain.auth.model.map;

import com.goaleaf.accounts.api.dto.response.AuthenticationTokenDto;
import com.goaleaf.accounts.domain.auth.model.AuthenticationToken;

/**
 * A mapper providing conversions between the {@link AuthenticationToken} domain object and related data transfer objects.
 *
 * @author Created by: Pplociennik at 20.04.2026 20:17
 */
public class AuthenticationTokenDomainMapper {

    /**
     * Returns an {@link AuthenticationToken} domain object mapped from the specified data transfer object.
     *
     * @param aDto
     *         the data transfer object to be mapped.
     * @return the {@link AuthenticationToken} domain object or null if the parameter is null.
     */
    public static AuthenticationToken mapToDomain( AuthenticationTokenDto aDto ) {
        if ( aDto == null ) {
            return null;
        }

        return AuthenticationToken.builder()
                .accessToken( aDto.getAccessToken() )
                .refreshToken( aDto.getRefreshToken() )
                .expiresIn( aDto.getExpiresIn() )
                .build();
    }
}
