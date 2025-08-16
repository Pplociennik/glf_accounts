package com.goaleaf.accounts.data.dto.keycloak;

import lombok.Data;

/**
 * Represents a federated identity linked to a user account in an external identity provider.
 * This class is used to hold data related to the external authentication mechanisms tied to a specific user.
 * <p>
 * Fields:
 * - `identityProvider`: The name of the external identity provider (e.g., Google, Facebook).
 * - `userId`: The unique identifier of the user in the external identity provider.
 * - `userName`: The username or display name of the user in the external identity provider.
 */
@Data
public class FederatedIdentityRepresentationDto {
    private String identityProvider; // Name of the identity provider
    private String userId; // User ID from the identity provider
    private String userName; // Username from the identity provider
}

