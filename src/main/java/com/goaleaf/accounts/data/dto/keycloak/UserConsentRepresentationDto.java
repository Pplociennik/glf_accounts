package com.goaleaf.accounts.data.dto.keycloak;

import lombok.Data;

import java.util.List;

/**
 * Represents a user's consent information in the system. This class contains details
 * about the client consents, roles, and scope permissions granted by the user.
 * <p>
 * Fields:
 * - `clientId`: The identifier of the client application to which the consent applies.
 * - `grantedClientScopes`: A list of client-specific scopes that the user has granted access to.
 * - `createdDate`: The timestamp indicating when the consent was initially created.
 * - `lastUpdatedDate`: The timestamp indicating the last time the consent was updated.
 * - `grantedRealmRoles`: A list of realm roles that the user has consented to.
 */
@Data
public class UserConsentRepresentationDto {
    private String clientId; // Client ID
    private List< String > grantedClientScopes; // Granted client scopes
    private Long createdDate; // Creation date
    private Long lastUpdatedDate; // Last updated date
    private List< String > grantedRealmRoles; // Granted realm roles
}

