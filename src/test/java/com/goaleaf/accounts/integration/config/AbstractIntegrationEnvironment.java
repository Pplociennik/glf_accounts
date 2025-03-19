package com.goaleaf.accounts.integration.config;

import com.goaleaf.accounts.integration.config.containers.KeycloakConfigTestContainer;
import com.goaleaf.accounts.integration.config.containers.KeycloakTestContainer;
import com.goaleaf.accounts.integration.config.containers.MySqlTestContainer;
import org.junit.jupiter.api.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Duration;

import static com.goaleaf.accounts.integration.config.ContainerDetails.*;

/**
 * Base class for integration tests containing necessary test containers configuration and shared properties.
 *
 * @author Created by: Pplociennik at 28.05.2025 21:37
 */
@SpringBootTest( webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT )
@TestPropertySource( locations = "classpath:application-integration.yml" )
@Testcontainers
@ActiveProfiles( "integration" )
@Tag( "integration" )
public abstract class AbstractIntegrationEnvironment {

    @Container
    protected static final MySqlTestContainer goaleafdb;

    @Container
    protected static final MySqlTestContainer keycloakdb;

    @Container
    protected static final KeycloakTestContainer keycloak;

    @Container
    protected static final KeycloakConfigTestContainer keycloakConfig;

    private static final String MYSQL_IMAGE_NAME = "mysql:8.0";
    private static final String KEYCLOAK_CONFIGURER_IMAGE_NAME = "keycloak-configurer:integration";
    private static final String TOKEN_VALIDATION_STRATEGY = "ONLINE";
    private static final String GOALEAFDB_NETWORK_ALIAS = "integration-goalefdb";
    private static final String KEYCLOAKDB_NETWORK_ALIAS = "integration-keycloakdb";

    protected static Network network = Network.newNetwork();

    static {
        goaleafdb = new MySqlTestContainer( MYSQL_IMAGE_NAME, GOALEAFDB_NETWORK_ALIAS, "goaleafdb" )
                .withNetwork( network )
                .withNetworkMode( network.getId() )
                .waitingFor( Wait.forLogMessage( ".*ready for connections.*", 1 )
                        .withStartupTimeout( Duration.ofMinutes( 2 ) ) );

        keycloakdb = new MySqlTestContainer( MYSQL_IMAGE_NAME, KEYCLOAKDB_NETWORK_ALIAS, "keycloakdb" )
                .withNetwork( network )
                .withNetworkMode( network.getId() )
                .waitingFor( Wait.forLogMessage( ".*ready for connections.*", 1 )
                        .withStartupTimeout( Duration.ofMinutes( 2 ) ) );

        keycloak = new KeycloakTestContainer()
                .withNetwork( network )
                .withNetworkMode( network.getId() )
                .withExposedPorts( 8080, 8443, 9000 )
                .dependsOn( keycloakdb );


        keycloakConfig = new KeycloakConfigTestContainer( KEYCLOAK_CONFIGURER_IMAGE_NAME )
                .withNetwork( network )
                .withNetworkMode( network.getId() )
                .dependsOn( keycloak )
                .withNetworkAliases( "keycloak-configurer" )
                .waitingFor( Wait.forLogMessage( ".*completed successfully.*", 1 ).withStartupTimeout( Duration.ofMinutes( 10 ) ) );

    }

    @LocalServerPort
    protected int port;

    @DynamicPropertySource
    static void configureProperties( DynamicPropertyRegistry registry ) {

        registry.add( "spring.datasource.url", goaleafdb::getJdbcUrl );
        registry.add( "spring.datasource.username", goaleafdb::getUsername );
        registry.add( "spring.datasource.password", goaleafdb::getPassword );
        registry.add( "spring.jpa.hibernate.ddl-auto", () -> "create" );
//        registry.add( "eureka.client.enabled", () -> "false" );
//        registry.add( "eureka.client.register-with-eureka", () -> "false" );
//        registry.add( "eureka.client.fetch-registry", () -> "false" );

        registry.add( "spring.jpa.properties.hibernate.jdbc.time_zone", () -> "UTC" );
        registry.add( "com.goaleaf.accounts.auth.service.url", () -> "http://localhost:" + keycloak.getMappedPort( 8080 ) );
        registry.add( "com.goaleaf.accounts.keycloak.realm.name", () -> KEYCLOAK_REALM );
        registry.add( "com.goaleaf.accounts.keycloak.clientId", () -> KEYCLOAK_CLIENT_ID );
        registry.add( "com.goaleaf.accounts.keycloak.clientSecret", () -> KEYCLOAK_CLIENT_SECRET );
        registry.add( "com.goaleaf.accounts.keycloak.grantType", () -> "client_credentials" );
        registry.add( "com.goaleaf.accounts.keycloak.scope", () -> "openid email profile roles" );
        registry.add( "com.goaleaf.accounts.auth.access.token.validationStrategy", () -> TOKEN_VALIDATION_STRATEGY );
        registry.add( "com.goaleaf.accounts.clientUri", () -> "http://localhost:4200/" );
    }

}