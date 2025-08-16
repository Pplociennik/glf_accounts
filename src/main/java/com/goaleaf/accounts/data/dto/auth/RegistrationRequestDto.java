package com.goaleaf.accounts.data.dto.auth;

import com.github.pplociennik.commons.dto.BaseAbstractExtendableDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * A Data Transfer Object (DTO) used for creating a registration request.
 * This class encapsulates the necessary information to register a new user
 * account, including username, email, account status, and credentials.
 *
 * <p>This class is annotated with OpenAPI schema descriptions and
 * utilizes Lombok annotations for getter, setter, equals, and hashcode implementations.
 * </p>
 *
 * @author Created by: Pplociennik at 24.03.2025 19:28
 */
@Schema(
        name = "Registration Request Data Transfer Object",
        description = "A DTO being a necessary data for creating a registration request."
)
@EqualsAndHashCode( callSuper = true )
@Data
@AllArgsConstructor
public class RegistrationRequestDto extends BaseAbstractExtendableDto {

    @Schema(
            description = "A username.",
            example = "Test Username"
    )
    private String username;

    @Schema(
            description = "The email address.",
            example = "test@testmail.com" )
    private String email;

    @Schema(
            description = "Marks whether the account is enabled or not.",
            example = "true"
    )
    private boolean enabled;

    @Schema(
            description = "User's credentials (password; defines the password's value and marks it temporary or not)."
    )
    private CredentialsDto[] credentials;

}
