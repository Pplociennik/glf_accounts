package com.goaleaf.accounts.data.dto.keycloak;

import com.github.pplociennik.commons.dto.BaseAbstractExtendableDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Map;

/**
 * The AccountDto class represents a data transfer object for user account information.
 * It extends the BaseAbstractExtendableDto class and encapsulates various properties
 * and metadata associated with a user account in a system.
 */
@EqualsAndHashCode( callSuper = true )
@Data
public class AccountDto extends BaseAbstractExtendableDto {

    private String id; // ID of the user
    private String username; // Username
    private String firstName; // First name
    private String lastName; // Last name
    private String email; // Email address
    private Boolean emailVerified; // Whether the email is verified
    private Map< String, List< Object > > attributes; // A map of attributes (values stored as lists)

    private UserProfileMetadataDto userProfileMetadataDto; // Metadata for user profile

    private String self; // Self-referential link
    private String origin; // Origin of the user
    private Long createdTimestamp; // Timestamp of creation
    private Boolean enabled; // Whether the account is enabled
    private Boolean totp; // Whether TOTP is required
    private String federationLink; // Federation link identifier
    private String serviceAccountClientId; // Service account client ID

    private List< CredentialRepresentationDto > credentials; // List of credentials
    private List< String > disableableCredentialTypes; // Credential types that can be disabled

    private List< String > requiredActions; // Required actions
    private List< FederatedIdentityRepresentationDto > federatedIdentities; // Federated identities

    private List< String > realmRoles; // Realm roles
    private Map< String, List< Object > > clientRoles; // Client roles as a map of lists
    private List< UserConsentRepresentationDto > clientConsents; // User consents

    private Integer notBefore; // Not before timestamp

    private Map< String, List< Object > > applicationRoles; // Application roles
    private List< SocialLinkRepresentationDto > socialLinks; // Social links
    private List< String > groups; // Groups the user belongs to
    private Map< String, Boolean > access; // Access flags

}
