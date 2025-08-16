package com.goaleaf.accounts.data.dto.keycloak;

import lombok.Data;

/**
 * The SocialLinkRepresentation class represents a social link associated with a user's account.
 * It is used to encapsulate information about a user's connection to a social identity provider.
 * <p>
 * Fields:
 * - `socialProvider`: The name of the social provider (e.g., Facebook, Google).
 * - `socialUserId`: The unique ID of the user in the social provider system.
 * - `socialUsername`: The username or display name of the user in the social provider system.
 */
@Data
public class SocialLinkRepresentationDto {
    private String socialProvider; // Name of the social provider
    private String socialUserId; // Social user ID
    private String socialUsername; // Social username
}

