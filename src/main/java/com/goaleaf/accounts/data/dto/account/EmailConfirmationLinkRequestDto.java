package com.goaleaf.accounts.data.dto.account;

import com.github.pplociennik.commons.dto.BaseAbstractExtendableDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A dto representing a request for sending a message with an email confirmation link.
 *
 * @author Created by: Pplociennik at 24.05.2025 02:22
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EmailConfirmationLinkRequestDto extends BaseAbstractExtendableDto {

    @Schema(
            name = "Email address",
            description = "An email address which should be confirmed."
    )
    private String email;
}
