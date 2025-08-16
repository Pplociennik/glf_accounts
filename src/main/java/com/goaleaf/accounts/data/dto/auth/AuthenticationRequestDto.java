package com.goaleaf.accounts.data.dto.auth;

import com.github.pplociennik.commons.dto.BaseAbstractExtendableDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * A data transfer object representing an authentication request. This object contains the credentials
 * needed to authenticate a user's account in the system.
 *
 * <p><b>Usage:</b> This DTO is typically used in authentication service requests where a client provides their
 * username and password for login or authentication purposes.</p>
 *
 * <p><b>Author:</b> Pplociennik</p>
 * <p><b>Created:</b> 25.03.2025 20:34</p>
 */
@Schema(
        name = "An authentication request",
        description = "A data transfer object containing data necessary to authenticate user account in the system."
)
@EqualsAndHashCode( callSuper = true )
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationRequestDto extends BaseAbstractExtendableDto {

    @Schema(
            description = "The username associated with the user's account. This is used as an identifier during the authentication process.",
            example = "TestUsername"
    )
    private String username;

    @Schema(
            description = "The password associated with the user's account. This is required for verifying access and completing the authentication process.",
            example = "TestPassword1234!"
    )
    private String password;

    @Schema(
            description = "The details about the device and location the user is trying to authenticate from.",
            example = ""
    )
    private AuthenticationDetailsDto details;

    @Override
    public String toString() {
        return "AuthenticationRequestDto{" +
                "username='" + username + '\'' +
                ", password=********'" + '\'' +
                '}';
    }
}
