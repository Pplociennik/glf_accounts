package com.goaleaf.accounts.service.impl;

import com.github.pplociennik.commons.lang.CommonsResExcMsgTranslationKey;
import com.github.pplociennik.commons.service.SystemPropertiesReaderService;
import com.goaleaf.accounts.data.dto.account.EmailConfirmationLinkRequestDto;
import com.goaleaf.accounts.data.dto.account.PasswordChangingRequestDto;
import com.goaleaf.accounts.data.dto.account.PasswordResetRequestDto;
import com.goaleaf.accounts.data.dto.keycloak.AccountDto;
import com.goaleaf.accounts.data.dto.response.KeycloakErrorResponseDto;
import com.goaleaf.accounts.data.dto.user.UserDetailsDto;
import com.goaleaf.accounts.service.AccountService;
import com.goaleaf.accounts.service.KeycloakServiceConnectionService;
import com.goaleaf.accounts.service.UserDetailsService;
import com.goaleaf.accounts.service.validation.AuthenticationValidationService;
import com.goaleaf.accounts.system.exc.request.KeycloakActionRequestFailedException;
import com.goaleaf.accounts.system.exc.request.KeycloakResourceRequestFailedException;
import com.goaleaf.accounts.system.util.AccessTokenUtils;
import com.goaleaf.accounts.system.util.KeycloakUrlTemplates;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.pplociennik.commons.utility.CustomObjects.requireNonEmpty;
import static com.goaleaf.accounts.system.properties.AccountsSystemProperties.*;
import static com.goaleaf.accounts.system.util.KeycloakUrlTemplates.*;
import static java.util.Objects.requireNonNull;

/**
 * Implementation of the {@link AccountService} interface, responsible for managing operations
 * related to accounts, such as resetting/changing credentials or confirming the email address.
 *
 * @author Created by: Pplociennik
 * @since 24.03.2025 23:34
 */
@Service
@AllArgsConstructor
class AccountServiceImpl implements AccountService {

    /**
     * The relative path for the client endpoint used to confirm email addresses.
     */
    private static final String EMAIL_ADDRESS_CONFIRMATION_CLIENT_PATH = "/verify";

    /**
     * Represents the lifespan of an email address confirmation link in seconds.
     * This constant defines the time duration for which the confirmation link
     * remains valid before it expires.
     */
    private static final String EMAIL_ADDRESS_CONFIRMATION_LINK_LIFESPAN = "86400";

    /**
     * A service for reading system properties and configurations.
     */
    private final SystemPropertiesReaderService systemPropertiesReaderService;

    /**
     * A service that facilitates communication with the authentication service.
     */
    private final KeycloakServiceConnectionService keycloakConnectionService;

    /**
     * A service providing validation functions for the authentication data.
     */
    private final AuthenticationValidationService authenticationValidationService;

    /**
     * A service providing functionalities related to the {@link com.goaleaf.accounts.persistence.entity.UserDetails}.
     */
    private final UserDetailsService userDetailsService;

    /**
     * Retrieves a list of accounts associated with a given email address.
     *
     * @param aAccessToken
     *         a non-null string representing the authorization token to authenticate the
     *         request; must not be null.
     * @param aEmailAddress
     *         a non-null string representing the email address used to look up the associated
     *         accounts; must not be null.
     * @return a list of AccountDto objects representing the accounts associated with the
     * specified email address.
     *
     * @throws KeycloakResourceRequestFailedException
     *         when the resource request to keycloak failed for any reason
     * @throws NullPointerException
     *         if aEmailAddress is null.
     */
    public List< AccountDto > getAccountByEmailAddress( @NonNull String aAccessToken, @NonNull String aEmailAddress ) {
        requireNonNull( aEmailAddress );
        String realmName = systemPropertiesReaderService.readProperty( KEYCLOAK_REALM_NAME );
        WebClient client = keycloakConnectionService.getAuthServiceConnectionWebClient( GET_LIST_OF_ACCOUNTS_TEMPLATE, realmName );

        try {
            return client.get()
                    .uri( uriBuilder -> uriBuilder
                            .queryParam( "email", aEmailAddress )
                            .build() )
                    .header( "Authorization", aAccessToken )
                    .retrieve()
                    .bodyToFlux( AccountDto.class )
                    .collectList()
                    .block();
        } catch ( WebClientResponseException aE ) {
            KeycloakErrorResponseDto errorResponse = aE.getResponseBodyAs( KeycloakErrorResponseDto.class );
            requireNonNull( errorResponse );
            throw new KeycloakResourceRequestFailedException( CommonsResExcMsgTranslationKey.UNEXPECTED_EXCEPTION, errorResponse.getErrorDescription() );
        }
    }

    /**
     * Sends an email address verification message to the user.
     *
     * @param aRequestDto
     *         a DTO being a representation of a request for sending a message with the email confirmation link
     * @throws KeycloakActionRequestFailedException
     *         when the request to keycloak failed for any reason
     */
    @Override
    public void requestEmailAddressVerificationMessage( EmailConfirmationLinkRequestDto aRequestDto ) {
        requireNonNull( aRequestDto );
        String userId = getUserId( aRequestDto );
        String clientAccessToken = keycloakConnectionService.getClientAccessToken();
        String realmName = systemPropertiesReaderService.readProperty( KEYCLOAK_REALM_NAME );
        String clientRedirectionUri = systemPropertiesReaderService.readProperty( CLIENT_URI );
        String redirectUri = clientRedirectionUri + EMAIL_ADDRESS_CONFIRMATION_CLIENT_PATH;

        WebClient client = keycloakConnectionService.getAuthServiceConnectionWebClient( SEND_EMAIL_ADDRESS_VERIFICATION_MESSAGE_TEMPLATE, realmName, userId );
        try {
            client.put()
                    .header( "Authorization", clientAccessToken )
                    .contentType( MediaType.APPLICATION_JSON )
                    .bodyValue( Map.of(
                            "lifespan", EMAIL_ADDRESS_CONFIRMATION_LINK_LIFESPAN,
                            "redirect_uri", redirectUri
                    ) )
                    .retrieve()
                    .toBodilessEntity()
                    .block();

        } catch ( WebClientResponseException aE ) {
            KeycloakErrorResponseDto errorResponse = aE.getResponseBodyAs( KeycloakErrorResponseDto.class );
            requireNonNull( errorResponse );
            throw new KeycloakActionRequestFailedException( CommonsResExcMsgTranslationKey.UNEXPECTED_EXCEPTION, errorResponse.getErrorDescription() );
        }
    }

    /**
     * Verifies a user's email address using a confirmation token. This method communicates with the external
     * authentication service to confirm the email address associated with the provided token.
     *
     * @param aEmailConfirmationToken
     *         a non-null string representing the email confirmation token used to validate the user's email address;
     *         must not be null.
     * @throws KeycloakActionRequestFailedException
     *         when the keycloak action request failed for any reason
     */
    @Override
    public void verifyEmailAddress( @NonNull String aEmailConfirmationToken ) {
        requireNonNull( aEmailConfirmationToken );
        String realmName = systemPropertiesReaderService.readProperty( KEYCLOAK_REALM_NAME );
        String clientId = systemPropertiesReaderService.readProperty( KEYCLOAK_CLIENT_ID );
        WebClient client = keycloakConnectionService.getAuthServiceConnectionWebClient( KeycloakUrlTemplates.VERIFY_ACCOUNT_EMAIL_ADDRESS_TEMPLATE, realmName );

        try {
            client.get()
                    .uri( uriBuilder -> uriBuilder
                            .queryParam( "key", aEmailConfirmationToken )
                            .queryParam( "client_id", clientId )
                            .build() )
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch ( WebClientResponseException aE ) {
            KeycloakErrorResponseDto errorResponse = aE.getResponseBodyAs( KeycloakErrorResponseDto.class );
            requireNonNull( errorResponse );
            throw new KeycloakActionRequestFailedException( CommonsResExcMsgTranslationKey.UNEXPECTED_EXCEPTION, errorResponse.getErrorDescription() );
        }
    }

    /**
     * Sends a credential reset link to the specified email address. This method communicates with an external
     * authentication service to generate and send the reset link to the given email address.
     *
     * @param aDto
     *         A data transfer object representing the data necessary for executing a password reset request.
     * @throws NullPointerException
     *         If the provided email address is null.
     * @throws KeycloakActionRequestFailedException
     *         when the keycloak action request failed for any reason
     */
    @Override
    public void sendCredentialsResetLink( PasswordResetRequestDto aDto ) {
        requireNonNull( aDto );
        boolean userExists = authenticationValidationService.validatePasswordResetRequest( aDto.getEmail() );

        if ( userExists ) {
            String realmName = systemPropertiesReaderService.readProperty( KEYCLOAK_REALM_NAME );
            String clientAccessToken = keycloakConnectionService.getClientAccessToken();
            UserDetailsDto details = userDetailsService.findUserDetailsByEmail( aDto.getEmail() );

            WebClient client = keycloakConnectionService.getAuthServiceConnectionWebClient( RESET_ACCOUNT_CREDENTIALS_TEMPLATE, realmName, details.getUserId() );
            try {
                client.put()
                        .header( "Authorization", clientAccessToken )
                        .bodyValue( List.of( "UPDATE_PASSWORD" ) )
                        .retrieve()
                        .toBodilessEntity()
                        .block();
            } catch ( WebClientResponseException aE ) {
                KeycloakErrorResponseDto errorResponse = aE.getResponseBodyAs( KeycloakErrorResponseDto.class );
                requireNonNull( errorResponse );
                throw new KeycloakActionRequestFailedException( CommonsResExcMsgTranslationKey.UNEXPECTED_EXCEPTION, errorResponse.getErrorDescription() );
            }
        }
    }

    /**
     * Changes the password of an account by sending a password change request to the authentication service.
     *
     * @param aUserAccessToken
     *         the access token of the user, used for authorization, must not be null or empty
     * @param aPasswordChangingRequestDto
     *         the data transfer object containing the new password details; must not be null
     * @throws NullPointerException
     *         when any of the parameters is null
     * @throws KeycloakActionRequestFailedException
     *         when the keycloak action request failed for any reason
     */
    @Override
    public void changeAccountPassword( @NonNull String aUserAccessToken, @NonNull PasswordChangingRequestDto aPasswordChangingRequestDto ) {
        requireNonEmpty( aUserAccessToken );
        requireNonNull( aPasswordChangingRequestDto );
        authenticationValidationService.validatePasswordChangingRequest( aPasswordChangingRequestDto );
        String realmName = systemPropertiesReaderService.readProperty( KEYCLOAK_REALM_NAME );
        String userId = AccessTokenUtils.getUserId( aUserAccessToken );
        String clientAccessToken = keycloakConnectionService.getClientAccessToken();

        Map< String, Object > passwordData = new HashMap<>();
        passwordData.put( "type", "password" );
        passwordData.put( "value", aPasswordChangingRequestDto.getNewPassword() );
        passwordData.put( "temporary", false );

        WebClient client = keycloakConnectionService.getAuthServiceConnectionWebClient( CHANGE_ACCOUNT_CREDENTIALS_TEMPLATE, realmName, userId );
        try {
            client.put()
                    .header( "Authorization", clientAccessToken )
                    .contentType( MediaType.APPLICATION_JSON )
                    .bodyValue( passwordData )
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch ( WebClientResponseException aE ) {
            KeycloakErrorResponseDto errorResponse = aE.getResponseBodyAs( KeycloakErrorResponseDto.class );
            requireNonNull( errorResponse );
            throw new KeycloakActionRequestFailedException( CommonsResExcMsgTranslationKey.UNEXPECTED_EXCEPTION, errorResponse.getErrorDescription() );
        }
    }

    /**
     * Retrieves the user ID associated with the given email confirmation link request.
     *
     * @param aRequestDto
     *         the email confirmation link request containing the email information
     * @return the user ID associated with the provided email address
     */
    private String getUserId( EmailConfirmationLinkRequestDto aRequestDto ) {
        UserDetailsDto details = userDetailsService.findUserDetailsByEmail( aRequestDto.getEmail() );
        return details.getUserId();
    }
}
