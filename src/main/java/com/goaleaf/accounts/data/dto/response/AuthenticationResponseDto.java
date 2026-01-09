package com.goaleaf.accounts.data.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * A data transfer object to be returned to the client after successful authentication.
 *
 * @author Created by: Pplociennik at 13.01.2026 19:04
 */
@AllArgsConstructor
@Getter
public class AuthenticationResponseDto {

    /**
     * Data of the logged-in user.
     */
    private AuthenticationResponseUserDataDto userData;

    /**
     * A user access token data.
     */
    private AuthenticationTokenDto token;
}
