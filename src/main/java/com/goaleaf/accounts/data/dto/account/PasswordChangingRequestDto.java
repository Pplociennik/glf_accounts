package com.goaleaf.accounts.data.dto.account;

import com.github.pplociennik.commons.dto.BaseAbstractExtendableDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * A DTO containing necessary information for changing the account's password.
 *
 * @author Created by: Pplociennik at 27.04.2025 00:30
 */
@Getter
@AllArgsConstructor
public class PasswordChangingRequestDto extends BaseAbstractExtendableDto {

    @Schema(
            description = "A current password for verification."
    )
    private String currentPassword;

    @Schema(
            description = "A new password. Needs to fulfill all the necessary requirements."
    )
    private String newPassword;

    @Schema(
            description = "Repeat the new password for verification."
    )
    private String confirmation;
}
