package com.goaleaf.accounts.integration.authentication;

import com.github.pplociennik.commons.dto.ResponseDto;
import com.goaleaf.accounts.data.dto.auth.CredentialsDto;
import com.goaleaf.accounts.data.dto.auth.RegistrationRequestDto;
import com.goaleaf.accounts.data.dto.user.UserDetailsDto;
import com.goaleaf.accounts.integration.AbstractIntegrationTest;
import com.goaleaf.accounts.service.UserDetailsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.http.HttpStatus.CREATED;

/**
 * Integration tests for a user registration process validating various scenarios and error cases.
 *
 * @author Created by: Pplociennik at 28.05.2025 21:54
 */
public class RegistrationProcessTest extends AbstractIntegrationTest {

    public static final String TEST_USERNAME = "TestUsername";
    public static final String TEST_USERNAME_2 = "TestUsername_2";
    public static final String TEST_PASSWORD = "Test1234!";

    // #################################################################################

    public static final String TEST_EMAIL_ADDRESS = "test@test.com";
    public static final String TEST_EMAIL_ADDRESS_2 = "test2@test.com";
    private static final String REGISTRATION_ENDPOINT = "/api/auth/register";

    // #################################################################################

    @Autowired
    private UserDetailsService userDetailsService;

    /**
     * Tests whether a correct status and message is returned when a new user has been created successfully.
     */
    @Test
    void shouldReturnCorrectStatusAndMessage_whenUserSuccessfullyCreated() {

        // GIVEN
        String clientAccessToken = authenticateKeycloakClient();

        CredentialsDto credentialsDto = new CredentialsDto( TEST_PASSWORD, false );
        CredentialsDto[] credentialsDtos = { credentialsDto };
        RegistrationRequestDto requestDto = new RegistrationRequestDto( TEST_USERNAME, TEST_EMAIL_ADDRESS, true, credentialsDtos );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType( MediaType.APPLICATION_JSON );
        headers.set( "Authorization", "Bearer " + clientAccessToken );
        HttpEntity< RegistrationRequestDto > request = new HttpEntity<>( requestDto, headers );

        // WHEN
        ResponseEntity< ResponseDto > response = restTemplate.postForEntity(
                getUrl( REGISTRATION_ENDPOINT ),
                request,
                ResponseDto.class
        );

        // THEN
        assertThat( response.getStatusCode() ).isEqualTo( CREATED );

        ResponseDto body = response.getBody();
        assertThat( body ).isNotNull();
        assertThat( body.getStatusInfo().getStatusMsg() ).isEqualTo( "User registered successfully." );

    }

    /**
     * Tests whether a user details object is created correctly during the registration process.
     */
    @Test
    void shouldCreateValidUserDetails_whenUserSuccessfullyCreated() {

        // GIVEN
        String registrationClientAccessToken = authenticateKeycloakClient();

        CredentialsDto credentialsDto = new CredentialsDto( TEST_PASSWORD, false );
        CredentialsDto[] credentialsDtos = { credentialsDto };
        RegistrationRequestDto requestDto = new RegistrationRequestDto( TEST_USERNAME_2, TEST_EMAIL_ADDRESS_2, true, credentialsDtos );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType( MediaType.APPLICATION_JSON );
        headers.set( "Authorization", "Bearer " + registrationClientAccessToken );

        HttpEntity< RegistrationRequestDto > requestEntity = new HttpEntity<>( requestDto, headers );

        // WHEN
        // Registration
        ResponseEntity< ResponseDto > response = restTemplate.postForEntity(
                getUrl( REGISTRATION_ENDPOINT ),
                requestEntity,
                ResponseDto.class
        );

        // THEN
        UserDetailsDto userDetails = userDetailsService.findUserDetailsByEmail( TEST_EMAIL_ADDRESS );

        assertThat( response.getStatusCode() ).isEqualTo( CREATED );
        assertThat( userDetails ).isNotNull();

    }

    // #############################################################################################################


}
