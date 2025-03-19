package com.goaleaf.accounts.integration.config.containers;

import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;

/**
 * MySqlTestContainer is a custom implementation of the MySQLContainer class provided by Testcontainers.
 * It is used to manage a MySQL database container for integration testing purposes.
 * The container can be configured with a custom image name, network alias, and database name.
 */
public class MySqlTestContainer extends MySQLContainer< MySqlTestContainer > {

    private static final String USERNAME = "test";
    private static final String PASSWORD = "test";

    private final String networkAlias;
    private final String databaseName;

    public MySqlTestContainer( String aImageName, String aNetworkAlias, String aDatabaseName ) {
        super( aImageName );
        this.networkAlias = aNetworkAlias;
        this.databaseName = aDatabaseName;

        withDatabaseName( aDatabaseName )
                .withUsername( USERNAME )
                .withPassword( PASSWORD )
                .withNetworkAliases( aNetworkAlias )
                .withExposedPorts( 3306 )
                .waitingFor( Wait.forListeningPort() );
    }

    @Override
    public String getJdbcUrl() {
        if ( !isRunning() ) {
            return String.format( "jdbc:mysql://localhost:3306/%s", databaseName );
        }
        return String.format( "jdbc:mysql://%s:%d/%s",
                getHost(),
                getMappedPort( 3306 ),
                databaseName
        );
    }

    public String getUpdatedJdbcUrl() {

        return String.format( "jdbc:mysql://%s:%d/%s",
                getNetworkAlias(),
                3306,
                databaseName
        );
    }

    @Override
    public String getUsername() {
        return USERNAME;
    }

    @Override
    public String getPassword() {
        return PASSWORD;
    }

    public String getNetworkAlias() {
        return networkAlias;
    }
}