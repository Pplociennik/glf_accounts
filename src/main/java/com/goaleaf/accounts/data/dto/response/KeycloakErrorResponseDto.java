package com.goaleaf.accounts.data.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.pplociennik.commons.dto.BaseAbstractExtendableDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Represents a data transfer object (DTO) for handling error responses from Keycloak.
 * <p>
 * This class encapsulates information related to an error response, which includes
 * details about the error type and its description. It is typically used to provide
 * structured error information when an operation involving Keycloak fails.
 * <p>
 * The error message can describe the specific issue, while the error description
 * may provide additional context or details for understanding or troubleshooting the issue.
 */
@EqualsAndHashCode( callSuper = true )
@AllArgsConstructor
@Data
public class KeycloakErrorResponseDto extends BaseAbstractExtendableDto {

    @Schema( name = "error", description = "The type or identifier of the error that occurred within Keycloak." )
    private String error;

    @Schema( name = "errorDescription", description = "A human-readable description or details of the error." )
    @JsonProperty( value = "error_description" )
    private String errorDescription;
}