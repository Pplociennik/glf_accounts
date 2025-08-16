package com.goaleaf.accounts.data.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.pplociennik.commons.dto.BaseAbstractExtendableDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Represents a data transfer object (DTO) for the authentication response.
 * <p>
 * This class encapsulates information returned by the authentication service after
 * a successful authentication attempt. It contains details such as the access token,
 * its expiration time, the refresh token, and other relevant information required
 * to manage user sessions and access rights.
 * </p>
 *
 * <p><b>Author:</b> Pplociennik</p>
 * <p><b>Created:</b> 25.03.2025 21:06</p>
 */
@EqualsAndHashCode( callSuper = true )
@Data
public class AuthenticationTokenDto extends BaseAbstractExtendableDto {

    /**
     * The token used to access secured resources within the system.
     */
    @JsonProperty( value = "access_token" )
    private String accessToken;
    /**
     * The duration, in seconds, for which the access token remains valid.
     */
    @JsonProperty( value = "expires_in" )
    private int expiresIn;
    /**
     * The duration, in seconds, for which the refresh token remains valid.
     */
    @JsonProperty( value = "refresh_expires_in" )
    private int refreshExpiresIn;
    /**
     * The token used to obtain a new access token once the current token expires.
     */
    @JsonProperty( value = "refresh_token" )
    private String refreshToken;
    /**
     * The type of the token, typically "Bearer", indicating how the token should be used.
     */
    @JsonProperty( value = "token_type" )
    private String tokenType;
    /**
     * A timestamp specifying the time before which the token is not valid.
     */
    @JsonProperty( value = "not_before_policy" )
    private int notBeforePolicy;
    /**
     * The unique state identifier of the user's session.
     */
    @JsonProperty( value = "session_state" )
    private String sessionState;
    /**
     * The scope of access granted, which defines the level of permissions
     * associated with the token.
     */
    @JsonProperty( value = "scope" )
    private String scope;

}

