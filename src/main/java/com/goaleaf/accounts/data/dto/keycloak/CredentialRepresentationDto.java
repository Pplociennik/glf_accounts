package com.goaleaf.accounts.data.dto.keycloak;

import lombok.Data;

import java.util.Map;

/**
 * Represents a credential associated with a user account, containing information
 * about the credential's type, attributes, metadata, and security properties.
 * This is primarily used in conjunction with user account data transfer objects.
 * <p>
 * The class encapsulates details such as identification, type, and configuration
 * of the credential, which may include hashed values, encryption parameters, or
 * user-friendly labels. Additionally, it supports metadata such as creation time,
 * priority, or expiration state.
 * <p>
 * Fields:
 * - `id`: The unique identifier for the credential.
 * - `type`: Specifies the type of the credential (e.g., password, OTP).
 * - `userLabel`: A user-friendly label associated with the credential.
 * - `createdDate`: The timestamp indicating when the credential was created.
 * - `secretData`: Any sensitive or secret data connected with the credential.
 * - `credentialData`: Specific data or configuration for the credential type.
 * - `priority`: Priority assigned to the credential.
 * - `value`: Represents the credential's value, when applicable.
 * - `temporary`: A flag to indicate whether the credential is temporary.
 * - `device`: The associated device for this credential, if applicable.
 * - `hashedSaltedValue`: Stores a hashed and salted version of the credential value.
 * - `salt`: The salt used for hashing credential data.
 * - `hashIterations`: The number of iterations used for hashing processes.
 * - `counter`: Tracks counters for the credential, if needed (e.g., tokens).
 * - `algorithm`: Defines the algorithm used for encryption or hashing.
 * - `digits`: Indicates the number of digits (e.g., for OTP codes).
 * - `period`: The time period (e.g., TOTP interval duration).
 * - `config`: A map holding additional configuration properties for the credential.
 */
@Data
public class CredentialRepresentationDto {
    private String id; // The unique ID of the credential
    private String type; // The type of credential
    private String userLabel; // User-friendly label for the credential
    private Long createdDate; // The creation date of the credential
    private String secretData; // The secret data associated with the credential
    private String credentialData; // Credential-specific data
    private Integer priority; // Priority value for the credential
    private String value; // Value of the credential (if applicable)
    private Boolean temporary; // Indicator if the credential is temporary
    private String device; // Associated device for the credential
    private String hashedSaltedValue; // Hashed and salted value of the credential
    private String salt; // Salt value used for hashing
    private Integer hashIterations; // Number of hash iterations
    private Integer counter; // Counter information (if applicable)
    private String algorithm; // Algorithm used for the credential
    private Integer digits; // Number of digits (e.g., for OTP)
    private Integer period; // Period duration (e.g., for TOTP)
    private Map< String, String > config; // Configuration map for additional properties
}

