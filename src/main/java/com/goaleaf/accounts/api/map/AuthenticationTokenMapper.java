package com.goaleaf.accounts.api.map;

import com.goaleaf.accounts.api.dto.response.AuthenticationTokenDto;
import com.goaleaf.accounts.domain.auth.model.AuthenticationToken;
import com.goaleaf.accounts.domain.auth.model.map.AuthenticationTokenDomainMapper;

/**
 * A mapper for the {@link AuthenticationToken} domain object and related data transfer objects.
 *
 * @author Created by: Pplociennik at 20.04.2026 20:17
 */
public class AuthenticationTokenMapper {

    /**
     * Returns an {@link AuthenticationToken} domain object mapped from the specified data transfer object.
     *
     * @param aDto
     *         the data transfer object to be mapped.
     * @return the {@link AuthenticationToken} domain object or null if the parameter is null.
     */
    public static AuthenticationToken mapToDomain( AuthenticationTokenDto aDto ) {
        return AuthenticationTokenDomainMapper.mapToDomain( aDto );
    }
}
