package com.goaleaf.accounts.integration.config.containers;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Map;

import static com.goaleaf.accounts.integration.config.ContainerDetails.*;

/**
 * A Keycloak test container class.
 *
 * @author Created by: Pplociennik at 28.05.2025 22:12
 */
public final class KeycloakTestContainer extends GenericContainer< KeycloakTestContainer > {


    public KeycloakTestContainer() {
        super( KEYCLOAK_IMAGE_NAME );

        withNetworkAliases( KEYCLOAK_CONTAINER_ALIAS );
        withCommand( "start-dev" );

        final Map< String, String > envMap = Map.of(
                "KC_BOOTSTRAP_ADMIN_USERNAME", KEYCLOAK_ADMIN_USERNAME,
                "KC_BOOTSTRAP_ADMIN_PASSWORD", KEYCLOAK_ADMIN_PASSWORD,
                "KC_HEALTH_ENABLED", "true",
                "KC_METRICS_ENABLED", "true",
                "KC_DB", "mysql",
                "KC_DB_URL", KEYCLOAK_DATABASE_URL,
                "KC_DB_USERNAME", KEYCLOAK_DATABASE_USERNAME,
                "KC_DB_PASSWORD", KEYCLOAK_DATABASE_PASSWORD,
                "KC_HOSTNAME", KEYCLOAK_DATABASE_CONTAINER_ALIAS,
                "KC_FEATURES", "scripts"
        );
        withEnv( envMap );

        waitingFor( Wait.forLogMessage( ".*Running the server in development mode.*", 1 ) );
        withStartupTimeout( Duration.of( 10, ChronoUnit.MINUTES ) );

    }

}
