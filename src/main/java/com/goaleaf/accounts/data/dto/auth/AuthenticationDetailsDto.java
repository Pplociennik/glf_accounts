package com.goaleaf.accounts.data.dto.auth;

import com.github.pplociennik.commons.dto.BaseAbstractExtendableDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * A data transfer object that provides additional details about an authentication attempt.
 * This class contains information related to the location and device from which the user
 * is trying to authenticate.
 * <p>
 * This DTO is typically used as part of an authentication request in the system to provide
 * contextual information, such as where the login is initiated from and the device being used.
 * <p>
 * It extends the {@code BaseAbstractExtendableDto} for enabling additional functionality
 * or property extensions.
 */
@EqualsAndHashCode( callSuper = true )
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthenticationDetailsDto extends BaseAbstractExtendableDto {

    @Schema(
            description = "A location where the user is trying to authenticate from.",
            example = ""
    )
    private String location;

    @Schema(
            description = "A name of the device the user is trying to authenticate from.",
            example = ""
    )
    private String deviceName;
}
