package com.goaleaf.accounts.service.impl;

import com.github.pplociennik.commons.lang.CommonsResExcMsgTranslationKey;
import com.github.pplociennik.commons.service.SystemPropertiesReaderService;
import com.goaleaf.accounts.data.dto.account.PasswordChangingRequestDto;
import com.goaleaf.accounts.data.dto.account.PasswordResetRequestDto;
import com.goaleaf.accounts.data.dto.keycloak.AccountDto;
import com.goaleaf.accounts.data.dto.response.KeycloakErrorResponseDto;
import com.goaleaf.accounts.data.dto.user.UserDetailsDto;
import com.goaleaf.accounts.service.AccountService;
import com.goaleaf.accounts.service.KeycloakServiceConnectionService;
import com.goaleaf.accounts.service.UserDetailsService;
import com.goaleaf.accounts.service.validation.AuthenticationValidationService;
import com.goaleaf.accounts.system.exc.auth.AccountNotVerifiedException;
import com.goaleaf.accounts.system.exc.request.KeycloakActionRequestFailedException;
import com.goaleaf.accounts.system.exc.request.KeycloakResourceRequestFailedException;
import com.goaleaf.accounts.system.lang.AccountsExcTranslationKey;
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
     * Retrieves an account's details using the associated email address.
     *
     * @param aAccessToken
     *         the access token used for authentication, must not be null
     * @param aEmailAddress
     *         the email address of the account to retrieve, must not be null
     * @return the {@code AccountDto} object containing the account details
     *
     * @throws KeycloakResourceRequestFailedException
     *         if the request to the Keycloak server fails or the response contains an error
     */
    public AccountDto getAccountByEmailAddress( @NonNull String aAccessToken, @NonNull String aEmailAddress ) {
        requireNonNull( aEmailAddress );
        String realmName = systemPropertiesReaderService.readProperty( KEYCLOAK_REALM_NAME );
        WebClient client = keycloakConnectionService.getAuthServiceConnectionWebClient( GET_LIST_OF_ACCOUNTS_TEMPLATE, realmName );

        try {
            List< AccountDto > resultList = client.get()
                    .uri( uriBuilder -> uriBuilder
                            .queryParam( "email", aEmailAddress )
                            .build() )
                    .header( "Authorization", aAccessToken )
                    .retrieve()
                    .bodyToFlux( AccountDto.class )
                    .collectList()
                    .block();

            return resultList.get( 0 );
        } catch ( WebClientResponseException aE ) {
            KeycloakErrorResponseDto errorResponse = aE.getResponseBodyAs( KeycloakErrorResponseDto.class );
            requireNonNull( errorResponse );
            throw new KeycloakResourceRequestFailedException( CommonsResExcMsgTranslationKey.UNEXPECTED_EXCEPTION, errorResponse.getErrorDescription() );
        }
    }

    /**
     * Retrieves an account by the provided email address.
     * <p>
     * This method communicates with an external Keycloak service to fetch account
     * details associated with the specified email address. If an account with the
     * given email address exists, the first matching account is returned. In case
     * of an error during the request, an exception is thrown.
     *
     * @param aEmailAddress
     *         the email address of the account to retrieve; must not be null
     * @return the account details as an {@code AccountDto} object; never null
     *
     * @throws KeycloakResourceRequestFailedException
     *         if there is an error during the request or the response contains an error
     * @throws NullPointerException
     *         if {@code aEmailAddress} is null or if the response error details are null
     */
    public AccountDto getAccountByEmailAddress( @NonNull String aEmailAddress ) {
        requireNonNull( aEmailAddress );
        String realmName = systemPropertiesReaderService.readProperty( KEYCLOAK_REALM_NAME );
        String clientAccessToken = keycloakConnectionService.getClientAccessToken();
        WebClient client = keycloakConnectionService.getAuthServiceConnectionWebClient( GET_LIST_OF_ACCOUNTS_TEMPLATE, realmName );

        try {
            List< AccountDto > resultList = client.get()
                    .uri( uriBuilder -> uriBuilder
                            .queryParam( "email", aEmailAddress )
                            .build() )
                    .header( "Authorization", clientAccessToken )
                    .retrieve()
                    .bodyToFlux( AccountDto.class )
                    .collectList()
                    .block();

            return resultList.get( 0 );
        } catch ( WebClientResponseException aE ) {
            KeycloakErrorResponseDto errorResponse = aE.getResponseBodyAs( KeycloakErrorResponseDto.class );
            requireNonNull( errorResponse );
            throw new KeycloakResourceRequestFailedException( CommonsResExcMsgTranslationKey.UNEXPECTED_EXCEPTION, errorResponse.getErrorDescription() );
        }
    }

    /**
     * Sends an email address verification message to the user.
     *
     * @param aEmailAddress
     *         an email address which will be confirmed.
     * @throws KeycloakActionRequestFailedException
     *         when the request to keycloak failed for any reason
     */
    @Override
    public void requestEmailAddressVerificationMessage( @NonNull String aEmailAddress ) {
        requireNonNull( aEmailAddress );
        String userId = getUserId( aEmailAddress );
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
     * Checks if the email associated with the provided email address is verified.
     * Throws an AccountNotVerifiedException if the email is not verified.
     *
     * @param aEmailAddress
     *         the email address to check for verification status, must not be null
     */
    @Override
    public void checkIfEmailVerified( @NonNull String aEmailAddress ) {
        AccountDto accountDto = getAccountByEmailAddress( aEmailAddress );

        if ( !accountDto.getEmailVerified() ) {
            throw new AccountNotVerifiedException( aEmailAddress, AccountsExcTranslationKey.ACCOUNT_NOT_VERIFIED );
        }
    }

    /**
     * Deletes the account associated with the provided user access token.
     * This method handles the account deletion process by interacting with the external
     * authentication service. The access token must belong to an authorized user.
     *
     * @param aUserAccessToken
     *         the access token of the user whose account is to be deleted. Must not be null.
     * @throws NullPointerException
     *         if the provided access token is null.
     * @throws IllegalArgumentException
     *         if the provided access token is invalid or empty.
     */
    @Override
    public void deleteAccount( @NonNull String aUserAccessToken ) {
        requireNonNull( aUserAccessToken );
        String userId = AccessTokenUtils.getUserId( aUserAccessToken );
        userDetailsService.deleteUserDetails( userId );

        String realmName = systemPropertiesReaderService.readProperty( KEYCLOAK_REALM_NAME );

        String clientAccessToken = keycloakConnectionService.getClientAccessToken();
        WebClient client = keycloakConnectionService.getAuthServiceConnectionWebClient( DELETE_USER_TEMPLATE, realmName, userId );
        try {
            client.delete()
                    .header( "Authorization", clientAccessToken )
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
     * @param aEmailAddress
     *         the email address
     * @return the user ID associated with the provided email address
     */
    private String getUserId( String aEmailAddress ) {
        UserDetailsDto details = userDetailsService.findUserDetailsByEmail( aEmailAddress );
        return details.getUserId();
    }
}
