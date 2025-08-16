package com.goaleaf.accounts.data.dto.keycloak;

import lombok.Data;

import java.util.List;

/**
 * Represents metadata for a user's profile, including attribute and group metadata.
 * This class encapsulates details about the structure and organization of user profile
 * data to support customization, validation, and grouping.
 * <p>
 * Fields:
 * - `attributes`: A list of metadata for individual user profile attributes. Each attribute's metadata
 * defines properties like name, display name, validation rules, and more.
 * - `groups`: A list of metadata for groups of attributes. Each group's metadata includes
 * information on group name, display properties, and annotations for contextual grouping.
 */
@Data
public class UserProfileMetadataDto {
    private List< UserProfileAttributeMetadataDto > attributes; // Attributes metadata
    private List< UserProfileAttributeGroupMetadataDto > groups; // Groups metadata
}

