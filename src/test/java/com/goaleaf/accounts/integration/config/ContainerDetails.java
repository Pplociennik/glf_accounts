package com.goaleaf.accounts.integration.config;

/**
 * A class holding containers' connection details.
 *
 * @author Created by: Pplociennik at 02.06.2025 22:22
 */
public final class ContainerDetails {

    // ### KEYCLOAK DB ###
    public static final String KEYCLOAK_DATABASE_CONTAINER_ALIAS = "integration-keycloakdb";
    public static final String KEYCLOAK_DATABASE_NAME = "keycloakdb";
    public static final int KEYCLOAK_DATABASE_CONTAINER_INNER_PORT = 3306;
    public static final String KEYCLOAK_DATABASE_URL = "jdbc:mysql://" + KEYCLOAK_DATABASE_CONTAINER_ALIAS + ":" + KEYCLOAK_DATABASE_CONTAINER_INNER_PORT + "/" + KEYCLOAK_DATABASE_NAME;
    public static final String KEYCLOAK_DATABASE_USERNAME = "test";
    public static final String KEYCLOAK_DATABASE_PASSWORD = "test";

    // ### KEYCLOAK ###
    // Container
    public static final String KEYCLOAK_IMAGE_NAME = "quay.io/keycloak/keycloak:latest";
    public static final String KEYCLOAK_CONTAINER_ALIAS = "integration-keycloak";

    // App
    public static final String KEYCLOAK_ADMIN_USERNAME = "admin";
    public static final String KEYCLOAK_ADMIN_PASSWORD = "admin";
    public static final String KEYCLOAK_REALM = "integration";
    public static final String KEYCLOAK_CLIENT_ID = "integration-client";
    public static final String KEYCLOAK_CLIENT_SECRET = "integration-client-secret";
}
