package com.goaleaf.accounts.integration.config.containers;

import lombok.NonNull;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

/**
 * A container configuring keycloak realm for integration tests.
 *
 * @author Created by: Pplociennik at 02.06.2025 20:45
 */
public class KeycloakConfigTestContainer extends GenericContainer< KeycloakConfigTestContainer > {

    public KeycloakConfigTestContainer( @NonNull String dockerImageName ) {
        super( dockerImageName );
        waitingFor( Wait.forLogMessage( ".*yyyyyyyyyyyyyyyyyyy.*", 1 ) );
        withStartupTimeout( Duration.of( 10, ChronoUnit.MINUTES ) );
    }
}
