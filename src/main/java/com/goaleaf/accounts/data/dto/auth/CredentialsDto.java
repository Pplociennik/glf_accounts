package com.goaleaf.accounts.data.dto.auth;

import com.github.pplociennik.commons.dto.BaseAbstractExtendableDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * A Data Transfer Object (DTO) that represents the credentials information for an account.
 * This class contains the password value for the account and a flag to indicate
 * whether the password is temporary (requiring the user to change it after first login).
 *
 * <p>It is annotated with OpenAPI schema descriptions for API documentation
 * and uses Lombok for automatic generation of boilerplate code such as getters,
 * setters, equals, and hashCode methods.
 * </p>
 *
 * @author Created by: Pplociennik at 24.03.2025 19:33
 * @since 2025-03-24
 */
@EqualsAndHashCode( callSuper = true )
@Data
@AllArgsConstructor
public class CredentialsDto extends BaseAbstractExtendableDto {

    @Schema(
            description = "A password to the account being created.",
            example = "T3stP@ssword!"
    )
    private String value;

    @Schema(
            description = "Marks whether the password is temporary (user needs to change it after the first login).",
            example = "false"
    )
    private boolean temporary;

    @Override
    public String toString() {
        return "CredentialsDto{" +
                "value='" + "********" + '\'' +
                ", temporary=" + temporary +
                '}';
    }
}
