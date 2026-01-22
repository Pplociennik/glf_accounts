package com.goaleaf.accounts.controller;

import com.github.pplociennik.commons.dto.ResponseDto;
import com.goaleaf.accounts.data.dto.account.EmailConfirmationLinkRequestDto;
import com.goaleaf.accounts.data.dto.account.PasswordChangingRequestDto;
import com.goaleaf.accounts.data.dto.account.PasswordResetRequestDto;
import com.goaleaf.accounts.service.AccountService;
import com.goaleaf.accounts.system.util.AccessTokenUtils;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import static com.github.pplociennik.commons.utility.CustomObjects.requireNonEmpty;
import static java.util.Objects.requireNonNull;

/**
 * The AccountManagementController class provides REST API endpoints for managing user accounts, including
 * actions such as requesting email verification, verifying email addresses, resetting passwords, and changing passwords.
 * It acts as a controller in the Spring framework and delegates business logic to the AccountService.
 */
@RestController
@RequestMapping( path = "/api/accounts" )
@AllArgsConstructor
@Log4j2
class AccountManagementController {

    /**
     * An instance of the {@link AccountService} interface used for handling account-related operations.
     * This service provides methods to manage user accounts, including actions such as email verification,
     * password resetting, and password changing. It acts as a key dependency for delegating account business logic
     * in the {@code AccountManagementController}.
     * <p>
     * The service is designed to interact with external authentication systems or databases to perform operations
     * like validating user tokens, managing email confirmations, and updating account credentials.
     */
    private final AccountService accountService;

    /**
     * Triggers an email verification process by sending an email confirmation link to the user.
     * This endpoint is used to request email address verification for a specified user account.
     *
     * @param aRequestDto
     *         a DTO being a representation of a request for sending a message with the email confirmation link
     * @return a ResponseEntity containing a ResponseDto that includes the status code, a message indicating the
     * outcome of the request, the token refresh status, and the submitted access token.
     */
    @PostMapping( path = "/email-confirmation/request" )
    ResponseEntity< ResponseDto > requestEmailAddressVerification( @NonNull @RequestBody EmailConfirmationLinkRequestDto aRequestDto ) {
        requireNonNull( aRequestDto );
        log.info( "Requesting email address verification." );
        accountService.requestEmailAddressVerificationMessage( aRequestDto.getEmail() );
        log.info( "Message with email confirmation link has been sent." );
        return ResponseEntity
                .status( HttpStatus.OK )
                .body(
                        ResponseDto.builder()
                                .withStatusInfo( "200", "Email verification request sent." )
                                .build()
                );
    }

    /**
     * Verifies a user's email address using the provided email confirmation token. It validates the token and confirms
     * the user's email address in the system.
     *
     * @param aToken
     *         a non-null string representing the email confirmation token. It must not be null or empty.
     * @return a ResponseEntity containing a ResponseDto with the status code and a message indicating the result of the operation.
     */
    @PostMapping( path = "/confirm" )
    ResponseEntity< ResponseDto > verifyEmailAddress( @RequestAttribute( value = "USER_ACCESS_TOKEN_REFRESHED" ) boolean aTokenRefreshed, @RequestHeader( value = "User-Token" ) @NonNull String aUserAccessToken, @RequestParam @NonNull String aToken ) {
        requireNonEmpty( aToken );
        log.info( "Verifying email address." );
        accountService.verifyEmailAddress( aToken );
        log.info( "Email address has been verified." );
        return ResponseEntity
                .status( HttpStatus.OK )
                .body(
                        ResponseDto.builder()
                                .withStatusInfo( "200", "Email address confirmed successfully." )
                                .withUserAccessToken( aTokenRefreshed, aUserAccessToken, AccessTokenUtils.getExpiresIn( aUserAccessToken ) )
                                .build()
                );
    }

    /**
     * Initiates the process to reset a user's password by sending a password reset link
     * to the specified email address. This endpoint requires a valid and non-empty email address.
     *
     * @param aRequestDto
     *         A data transfer object representing the data necessary for executing a password reset request.
     * @return a ResponseEntity containing a ResponseDto with the status code and a message
     * indicating the outcome of the operation.
     */
    @PostMapping( "/password/reset" )
    ResponseEntity< ResponseDto > resetPassword( @NonNull @RequestBody PasswordResetRequestDto aRequestDto ) {
        requireNonNull( aRequestDto );
        log.info( "Password reset requested." );
        accountService.sendCredentialsResetLink( aRequestDto );
        log.info( "Password reset link has been sent." );
        return ResponseEntity
                .status( HttpStatus.OK )
                .body(
                        ResponseDto.builder()
                                .withStatusInfo( "200", "Password reset link has been sent." )
                                .build()
                );
    }

    /**
     * Changes the user's account password based on the given password change request details.
     * It requires a valid user access token and ensures that the current password and new password
     * provided in the request fulfill the necessary requirements.
     *
     * @param aTokenRefreshed
     *         indicates whether the user's access token was refreshed, provided as a request attribute.
     * @param aUserAccessToken
     *         a non-null string representing the user's access token, provided as a request header, must not be empty.
     * @param aPasswordChangingRequestDto
     *         a non-null object containing the current password, new password, and its confirmation details for password change.
     * @return a ResponseEntity containing a ResponseDto with the status code and a message indicating the outcome of the password change request.
     */
    @PostMapping( "/password/change" )
    ResponseEntity< ResponseDto > changePassword( @RequestAttribute( value = "USER_ACCESS_TOKEN_REFRESHED" ) boolean aTokenRefreshed, @RequestHeader( value = "User-Token" ) String aUserAccessToken, @RequestBody @NonNull PasswordChangingRequestDto aPasswordChangingRequestDto ) {
        requireNonEmpty( aUserAccessToken );
        log.info( "Changing password requested." );
        accountService.changeAccountPassword( aUserAccessToken, aPasswordChangingRequestDto );
        log.info( "Password changed successfully." );
        return ResponseEntity
                .status( HttpStatus.OK )
                .body(
                        ResponseDto.builder()
                                .withStatusInfo( "200", "Password changed successfully." )
                                .withUserAccessToken( aTokenRefreshed, aUserAccessToken, AccessTokenUtils.getExpiresIn( aUserAccessToken ) )
                                .build()
                );

    }

}
