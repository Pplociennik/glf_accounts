package com.goaleaf.accounts.data.dto.account;

import com.github.pplociennik.commons.dto.BaseAbstractExtendableDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A data transfer object representing the data necessary for executing a password reset request.
 *
 * @author Created by: Pplociennik at 26.05.2025 20:35
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class PasswordResetRequestDto extends BaseAbstractExtendableDto {

    @Schema(
            name = "Email address",
            description = "An email address of the account."
    )
    private String email;
}
