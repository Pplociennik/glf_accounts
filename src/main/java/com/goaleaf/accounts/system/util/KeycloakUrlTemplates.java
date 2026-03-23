package com.goaleaf.accounts.system.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

/**
 * The {@code KeycloakUrlTemplates} enum defines reusable templates for constructing
 * various Keycloak endpoint URLs. These templates include placeholders for
 * server URL, realm name, and user-specific information, allowing dynamic
 * URL generation for interacting with Keycloak's REST API.
 *
 * <p><b>Author:</b> Pplociennik</p>
 * <p><b>Created:</b> 01.04.2025 23:27</p>
 */
@AllArgsConstructor
@Getter
public enum KeycloakUrlTemplates {

    /**
     * Template for constructing the Keycloak user registration endpoint URL.
     * The URL is formatted with placeholders:
     * <br>
     * - The specific realm name.
     *
     * <p>
     * Example format: {@code https://keycloak.example.com/admin/realms/my-realm/users}
     * </p>
     *
     * <p>
     * Usage: Dynamically generate the endpoint URL for creating new user accounts
     * in a specified realm within a Keycloak instance. Typically used in services
     * interacting with Keycloak's admin REST API.
     * </p>
     */
    REGISTRATION_URL_TEMPLATE( "/admin/realms/%s/users" ) {
        @Override
        public Set< String > getRequiredParametersNames() {
            return Set.of( "Realm name" );
        }
    },


    /**
     * Template for constructing the Keycloak authentication endpoint URL.
     * The URL is formatted with placeholders:
     * <br>
     * - The specific realm name.
     *
     *
     * <p>
     * Usage: Utilized for generating the endpoint used to obtain authentication
     * tokens from Keycloak via the {@code KeycloakRequestingServiceImpl} class.
     * It supports flexibility by allowing interaction with multiple Keycloak
     * instances or realms dynamically.
     * </p>
     */
    AUTHENTICATION_URL_TEMPLATE( "/realms/%s/protocol/openid-connect/token" ) {
        @Override
        public Set< String > getRequiredParametersNames() {
            return Set.of( "Realm name" );
        }
    },


    /**
     * Template for constructing the Keycloak endpoint URL to retrieve all active sessions
     * for a specific user within a given realm.
     * <p>
     * This URL template contains placeholders:
     * <br>
     * - The realm name associated with the user.
     * <br>
     * - The user ID whose sessions are to be retrieved.
     * <p>
     * Usage:
     * This constant is used to dynamically generate a URL for fetching session information
     * for a specific user in Keycloak. The generated URL allows administrators to access
     * and manage the sessions of a user within a particular realm through Keycloak's
     * admin REST API.
     * <p>
     * Example format: {@code https://keycloak.example.com/admin/realms/my-realm/users/user-id/sessions}
     */
    GET_ALL_SESSIONS_URL_TEMPLATE( "/admin/realms/%s/users/%s/sessions" ) {
        @Override
        public Set< String > getRequiredParametersNames() {
            return Set.of( "Realm name", "User ID" );
        }
    },

    /**
     * A URL template for generating the endpoint to refresh an authentication session in a Keycloak realm.
     * <p>
     * This template defines the structure of the URL used to obtain a new access token using the OpenID Connect protocol.
     * The `%s` placeholder in the template must be replaced with the appropriate realm name to resolve the correct URL.
     * The realm name specifies the context in which the refresh token operation will be executed.
     * <p>
     * Required Parameters:
     * <br>
     * - Realm name: The name of the Keycloak realm.
     */
    REFRESH_SESSION_URL_TEMPLATE( "/realms/%s/protocol/openid-connect/token" ) {
        @Override
        public Set< String > getRequiredParametersNames() {
            return Set.of( "Realm name" );
        }
    },

    /**
     * Represents a URL template for terminating all active user sessions within a specified realm.
     * The template requires two parameters:
     * <br>
     * 1. Realm name - A string representing the name of the realm within which the operation applies.
     * <br>
     * 2. User ID - A string identifying the user whose sessions are to be terminated.
     * <p>
     * This template ensures that the required parameters are validated and provides the necessary
     * placeholders for forming a complete URL.
     */
    TERMINATE_ALL_USER_SESSIONS_TEMPLATE( "/admin/realms/%s/users/%s/logout" ) {
        @Override
        public Set< String > getRequiredParametersNames() {
            return Set.of( "Realm name", "User ID" );
        }
    },

    /**
     * A constant representing the URL template for terminating a user session in a specified realm.
     * <p>
     * This template is used to construct a REST API endpoint for terminating a user session within a Keycloak
     * environment. The placeholder "%s" is expected to be replaced with the realm name and session ID, respectively,
     * when resolving the URL template.
     * <p>
     * Required parameters:
     * <br>
     * - Realm name: The name of the Keycloak realm where the session exists.
     * <br>
     * - Session ID: The unique identifier of the user's session to be terminated.
     * <p>
     * The `getRequiredParametersNames` method provides the names of the parameters required to resolve this URL template.
     */
    TERMINATE_USER_SESSION_TEMPLATE( "/admin/realms/%s/sessions/%s" ) {
        @Override
        public Set< String > getRequiredParametersNames() {
            return Set.of( "Realm name", "Session ID" );
        }
    },

    /**
     * Represents the URL template for the introspection endpoint of a Keycloak realm.
     * This constant provides the formatted path for accessing the introspection URL
     * using a given realm name. The template requires the "Realm name" parameter to
     * replace the placeholder in the URL.
     * <p>
     * Required parameters:
     * <br>
     * - Realm name: The name of the Keycloak realm.
     */
    INTROSPECT_TOKEN_TEMPLATE( "/realms/%s/protocol/openid-connect/token/introspect" ) {
        @Override
        public Set< String > getRequiredParametersNames() {
            return Set.of( "Realm name" );
        }
    },

    /**
     * Represents a URL template for deleting a user session in a specified realm.
     * This template requires two parameters:<br>
     * 1. The realm name.<br>
     * 2. The session ID.
     */
    DELETE_SESSION_TEMPLATE( "/admin/realms/%s/sessions/%s" ) {
        @Override
        public Set< String > getRequiredParametersNames() {
            return Set.of( "Realm name", "Session ID" );
        }
    },

    /**
     * A constant representing the URL template for sending an email verification message to a user.
     * This is a part of administrative user management functionality in a Keycloak realm.
     * <p>
     * Required Parameters:
     * 1. Realm name - The name of the Keycloak realm where the user resides.
     * 2. User ID - The unique identifier of the user within the specified realm.
     */
    SEND_EMAIL_ADDRESS_VERIFICATION_MESSAGE_TEMPLATE( "/admin/realms/%s/users/%s/send-verify-email" ) {
        @Override
        public Set< String > getRequiredParametersNames() {
            return Set.of( "Realm name", "User ID" );
        }
    },

    /**
     * Represents a URL template for verifying an account email address within a specific Keycloak realm.
     * Required Parameters:<br>
     * - Realm name: Specifies the Keycloak realm for which the email verification URL is to be generated.
     */
    VERIFY_ACCOUNT_EMAIL_ADDRESS_TEMPLATE( "/realms/%s/login-actions/action-token" ) {
        @Override
        public Set< String > getRequiredParametersNames() {
            return Set.of( "Realm name" );
        }
    },

    /**
     * An enumeration constant in the {@code KeycloakUrlTemplates} enum that defines a URL template for resetting user
     * account credentials in a specific Keycloak realm.
     * Required parameters:<br>
     * - "Realm name" - indicates the name of the Keycloak realm for which the reset credentials URL is generated.
     */
    RESET_ACCOUNT_CREDENTIALS_TEMPLATE( "/admin/realms/%s/users/%s/execute-actions-email" ) {
        @Override
        public Set< String > getRequiredParametersNames() {
            return Set.of( "Realm name", "User ID" );
        }
    },

    /**
     * Represents a URL template for the Keycloak endpoint used to change account credentials,
     * specifically for updating a user's password.
     * <p>
     * The template takes a single parameter for constructing the URL:<br>
     * - `Realm name`: Specifies the name of the Keycloak realm.
     */
    CHANGE_ACCOUNT_CREDENTIALS_TEMPLATE( "/admin/realms/%s/users/%s/reset-password" ) {
        @Override
        public Set< String > getRequiredParametersNames() {
            return Set.of( "Realm name", "User ID" );
        }
    },

    /**
     * Enum constant representing the URL template used to retrieve a list of user accounts
     * in a specific realm from the Keycloak administration API.
     * Required parameter(s):<br>
     * - Realm name: The name of the Keycloak realm for which the list of accounts is to be retrieved.
     */
    GET_LIST_OF_ACCOUNTS_TEMPLATE( "/admin/realms/%s/users" ) {
        @Override
        public Set< String > getRequiredParametersNames() {
            return Set.of( "Realm name" );
        }
    },

    /**
     * A constant representing a URL template for deleting a user from a specified realm in the Keycloak system.
     * This template contains placeholders for the realm name and user ID that must be provided to construct the final URL.
     * <p>
     * Usage:
     * - Used to construct an endpoint for deleting a user from a specific realm in Keycloak.
     * <p>
     * Placeholders:
     * 1. `%s` - Represents the name of the realm (Realm name).
     * 2. `%s` - Represents the unique identifier of the user to be deleted (User ID).
     * <p>
     * Required Parameters:
     * - Realm name: Name of the realm from which the user will be deleted.
     * - User ID: The identifier of the user to delete.
     * <p>
     * Overrides:
     * - {@code getRequiredParametersNames}: Returns the set of required parameter names for this URL template,
     * including "Realm name" and "User ID".
     */
    DELETE_USER_TEMPLATE( "/admin/realms/%s/users/%s" ) {
        @Override
        public Set< String > getRequiredParametersNames() {
            return Set.of( "Realm name", "User ID" );
        }
    };

    /**
     * A string variable representing a templated URL for interacting with Keycloak.
     * This template includes placeholders for dynamic construction of Keycloak endpoint URLs,
     * such as those used for authentication, user management, or session retrieval.
     * The actual value is expected to comply with the format requirements for Keycloak endpoints.
     */
    private final String keycloakUrlTemplate;

    /**
     * Retrieves the set of required parameter names necessary for constructing
     * the URL template. These parameter names represent the placeholders in
     * the template that must be substituted with actual values to generate
     * a valid URL.
     *
     * @return A set of strings representing the required parameter names
     * for the URL template.
     */
    public abstract Set< String > getRequiredParametersNames();
}
