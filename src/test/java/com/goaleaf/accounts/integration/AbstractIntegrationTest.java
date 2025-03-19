package com.goaleaf.accounts.integration;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.goaleaf.accounts.integration.config.AbstractIntegrationEnvironment;
import com.goaleaf.accounts.integration.config.ContainerDetails;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.Serializable;

/**
 * A base class providing utilities for the integration tests.
 *
 * @author Created by: Pplociennik at 11.08.2025 18:28
 */
public class AbstractIntegrationTest extends AbstractIntegrationEnvironment {

    /**
     * A base URL of the service.
     */
    protected static final String URL = "http://localhost:";

    /**
     * An endpoint for retrieving a client token from Keycloak.
     */
    protected static final String GET_KEYCLOAK_CLIENT_TOKEN_ENDPOINT = "/realms/integration/protocol/openid-connect/token";

    /**
     * A test implementation of the {@link RestTemplate} class.
     */
    @Autowired
    protected TestRestTemplate restTemplate;

    // ############################################################################################

    /**
     * Returns a full URL of the specified endpoint.
     */
    protected @NotNull String getUrl( String aEndpoint ) {
        return URL + port + aEndpoint;
    }

    /**
     * Authenticates the keycloak client.
     *
     * @return an access token
     */
    protected String authenticateKeycloakClient() {
        Integer mappedPort = keycloak.getMappedPort( 8080 );

        String url = URL + mappedPort + GET_KEYCLOAK_CLIENT_TOKEN_ENDPOINT;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType( MediaType.APPLICATION_FORM_URLENCODED );

        MultiValueMap< String, String > requestParams = new LinkedMultiValueMap<>();
        requestParams.add( "grant_type", "client_credentials" );
        requestParams.add( "client_id", ContainerDetails.KEYCLOAK_CLIENT_ID );
        requestParams.add( "client_secret", ContainerDetails.KEYCLOAK_CLIENT_SECRET );
        requestParams.add( "scope", "openid email profile roles" );

        HttpEntity< MultiValueMap< String, String > > request = new HttpEntity<>( requestParams, headers );

        return restTemplate.postForObject( url, request, String.class );
    }

    // ############################################################################################

    @AllArgsConstructor
    @Getter
    @Setter
    static class ClientAuthRequest implements Serializable {

        @JsonProperty( value = "grant_type" )
        private String grantType;

        @JsonProperty( value = "client_id" )
        private String clientId;

        @JsonProperty( value = "client_secret" )
        private String clientSecret;

        @JsonProperty( value = "scope" )
        private String scope;
    }
}
