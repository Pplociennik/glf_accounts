package com.goaleaf.accounts.system.properties;

import com.github.pplociennik.commons.system.SystemProperty;

import java.util.Set;

import static com.goaleaf.accounts.system.util.token.TokenValidationStrategy.OFFLINE;
import static com.goaleaf.accounts.system.util.token.TokenValidationStrategy.ONLINE;

/**
 * System properties related to the accounts service.
 *
 * @author Created by: Pplociennik at 19.03.2025 19:25
 */
public enum AccountsSystemProperties implements SystemProperty {

    /**
     * The URL of the auth service (keycloak).
     */
    AUTH_SERVICE_URL( "com.goaleaf.accounts.auth.service.url" ),

    /**
     * A name of the realm.
     */
    KEYCLOAK_REALM_NAME( "com.goaleaf.accounts.keycloak.realm.name" ),

    /**
     * An identifier of the keycloak's client.
     */
    KEYCLOAK_CLIENT_ID( "com.goaleaf.accounts.keycloak.clientId" ),

    /**
     * Represents the secret key associated with a Keycloak client.
     * This property is used for authenticating the client when interacting
     * with the Keycloak authorization server.
     */
    KEYCLOAK_CLIENT_SECRET( "com.goaleaf.accounts.keycloak.clientSecret" ),

    /**
     * The grant type used in the authentication process with Keycloak.
     * Represents the specific OAuth2.0 grant type for acquiring access tokens.
     */
    KEYCLOAK_GRANT_TYPE( "com.goaleaf.accounts.keycloak.grantType" ),

    /**
     * Represents the scope parameter used for Keycloak authentication requests.
     * The scope defines the level of access that the client is requesting.
     */
    KEYCLOAK_SCOPE( "com.goaleaf.accounts.keycloak.scope" ),

    /**
     * Defines the access token validation strategy which should be used during the application work. The possible values are:<br>
     * - ONLINE - sends a validation request to keycloak service each time the access token needs to be validated,<br>
     * - OFFLINE - uses date comparison (compares the expiration date time decoded from the access token with the system date time) locally for the access token validation
     */
    ACCESS_TOKEN_VALIDATION_STRATEGY( "com.goaleaf.accounts.auth.access.token.validationStrategy", OFFLINE.name(), ONLINE.name() ),

    /**
     * Represents the configuration key for the client URI in the application.
     * This variable holds the identifier for accessing the client URI property
     * within the application's configuration settings.
     */
    CLIENT_URI( "com.goaleaf.accounts.clientUri" ),

    ;

    // #################################################################################################################

    /**
     * A name of the system property.
     */
    private final String name;

    /**
     * Possible values of the system property.
     */
    private final Set< String > possibleValues;

    /**
     * Creates a new instance of the system property.
     *
     * @param name
     *         a name of the system property.
     */
    AccountsSystemProperties( String name ) {
        this.name = name;
        this.possibleValues = Set.of();
    }

    /**
     * Creates a new instance of the system property.
     *
     * @param name
     *         a name of the system property.
     * @param possibleValue
     *         a possible values of the system property.
     */
    AccountsSystemProperties( String name, String... possibleValue ) {
        this.name = name;
        this.possibleValues = Set.of( possibleValue );
    }

    /**
     * Returns the name of the system property.
     *
     * @return a {@link String} being a name of the property.
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Returns possible values of the system property.
     *
     * @return a {@link Set} of the property's possible values. Empty one if there is no restriction on values and all are possible.
     */
    @Override
    public Set< String > getPossibleValues() {
        return Set.of();
    }

}
