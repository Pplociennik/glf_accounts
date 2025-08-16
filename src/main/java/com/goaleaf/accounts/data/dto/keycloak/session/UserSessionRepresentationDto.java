package com.goaleaf.accounts.data.dto.keycloak.session;

import com.github.pplociennik.commons.dto.BaseAbstractExtendableDto;
import lombok.*;

import java.util.Map;

/**
 * Data Transfer Object (DTO) representing a user's session details.
 * <p>
 * This class contains information about a user's session such as session identifiers,
 * user information, IP address, session timing, and other session-related metadata.
 * It is used to transfer session data between systems or application components.
 */
@EqualsAndHashCode( callSuper = true )
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSessionRepresentationDto extends BaseAbstractExtendableDto {

    private String id;

    private String username;

    private String userId;

    private String ipAddress;

    private Long start;

    private Long lastAccess;

    private Boolean rememberMe;

    private Map< String, String > clients;

    private Boolean transientUser;
}

