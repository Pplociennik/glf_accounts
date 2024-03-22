package com.goaleaf.accounts.dto;

import com.github.pplociennik.commons.dto.BaseAbstractExtendableDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * A data transfer object representing data of the {@link com.goaleaf.accounts.entity.User} entity.
 *
 * @author Created by: Pplociennik at 14.03.2024 19:00
 */
@Schema(
        name = "User",
        description = "A schema representing user data."
)
@EqualsAndHashCode( callSuper = true )
@Data
@AllArgsConstructor
public class UserDto extends BaseAbstractExtendableDto {

    @Schema( description = "The unique identifier of the user." )
    private Long userId;

    @Schema( description = "The username." )
    private String userName;

    @Schema( description = "The email address;" )
    private String emailAddress;

    @Schema( description = "The description of the user." )
    private String description;
}
