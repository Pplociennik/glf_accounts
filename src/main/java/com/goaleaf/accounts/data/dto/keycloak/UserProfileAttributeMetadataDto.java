package com.goaleaf.accounts.data.dto.keycloak;

import lombok.Data;

import java.util.Map;

/**
 * Represents metadata information about an attribute in a user profile.
 * This class is intended to encapsulate details about individual user profile attributes,
 * including their properties, validation rules, grouping information, and additional metadata.
 * <p>
 * Fields:
 * - `name`: The internal name of the attribute.
 * - `displayName`: The user-facing name of the attribute.
 * - `required`: Indicates whether this attribute is mandatory.
 * - `readOnly`: Specifies if the attribute is read-only.
 * - `annotations`: Additional metadata or annotations associated with the attribute.
 * - `validators`: Validation rules for this attribute, represented as a map.
 * - `group`: Specifies a group to which this attribute belongs.
 * - `multivalued`: Indicates whether this attribute can hold multiple values.
 */
@Data
public class UserProfileAttributeMetadataDto {
    private String name; // Attribute name
    private String displayName; // Display name
    private Boolean required; // Whether it is required
    private Boolean readOnly; // Whether it is read-only
    private Map< String, Object > annotations; // Annotations
    private Map< String, Map< String, String > > validators; // Validators
    private String group; // Grouping information
    private Boolean multivalued; // Whether it can hold multiple values
}

