package com.goaleaf.accounts.domain.auth.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * A domain model representing an authentication token issued after a successful authentication.
 *
 * @author Created by: Pplociennik at 20.04.2026 20:17
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationToken {

    /**
     * The token used to access secured resources.
     */
    private String accessToken;

    /**
     * The token used to obtain a new access token once the current one expires.
     */
    private String refreshToken;

    /**
     * The duration, in seconds, for which the access token remains valid.
     */
    private int expiresIn;
}
