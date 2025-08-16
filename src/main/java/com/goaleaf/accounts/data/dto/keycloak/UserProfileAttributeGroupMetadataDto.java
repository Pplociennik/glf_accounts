package com.goaleaf.accounts.data.dto.keycloak;

import lombok.Data;

import java.util.Map;

/**
 * Represents the metadata for a group of user profile attributes. This class encapsulates
 * information used to define and describe a specific group of attributes within a user profile.
 * <p>
 * Fields:
 * - `name`: The name of the attribute group.
 * - `displayHeader`: Header text to be displayed for the group in user interfaces.
 * - `displayDescription`: Description text providing additional context about the group.
 * - `annotations`: A map of additional metadata or annotations associated with the group,
 * which can be used to provide further customization or configuration.
 */
@Data
public class UserProfileAttributeGroupMetadataDto {
    private String name; // Group name
    private String displayHeader; // Group display header
    private String displayDescription; // Group description
    private Map< String, Object > annotations; // Annotations
}
