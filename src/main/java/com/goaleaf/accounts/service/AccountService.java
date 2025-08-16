package com.goaleaf.accounts.service;

import com.goaleaf.accounts.data.dto.account.EmailConfirmationLinkRequestDto;
import com.goaleaf.accounts.data.dto.account.PasswordChangingRequestDto;
import com.goaleaf.accounts.data.dto.account.PasswordResetRequestDto;
import com.goaleaf.accounts.data.dto.keycloak.AccountDto;
import lombok.NonNull;

import java.util.List;

/**
 * Service interface for managing accounts within the application.
 * Provides methods for retrieving account information based on various criteria.
 * <p>
 * This service is designed for working with data from an external authentication provider
 * (e.g., Keycloak) and handles operations like fetching user account details based on email addresses.
 * <p>
 * Implementations of this interface should handle connection details, validation, and data retrieval
 * specific to the accounts system.
 * </p>
 *
 * @author Created by: Pplociennik at 24.03.2025 23:31
 */
public interface AccountService {

    /**
     * Retrieves a list of accounts associated with the provided email address.
     * This method performs user account lookups using the given access token and
     * queries the external system or internal database for matching account details.
     *
     * @param aAccessToken
     *         the authentication token used for authorization purposes when making the request;
     *         must not be null.
     * @param aEmailAddress
     *         the email address used to search for accounts; must not be null.
     * @return a list of {@link AccountDto} containing account details associated with the specified email address;
     * may return an empty list if no matching accounts are found.
     */
    List< AccountDto > getAccountByEmailAddress( @NonNull String aAccessToken, @NonNull String aEmailAddress );

    /**
     * Sends an email address verification message to the user.
     * This method is typically used to trigger the process of verifying the user's email address
     * and ensures that the provided email address is valid and associated with the specified user account.
     *
     * @param aRequestDto
     *         a DTO being a representation of a request for sending a message with the email confirmation link
     */
    void requestEmailAddressVerificationMessage( EmailConfirmationLinkRequestDto aRequestDto );

    /**
     * Verifies a user's email address using a confirmation token. This method communicates with the external
     * authentication service to confirm the email address associated with the provided token.
     *
     * @param aEmailConfirmationToken
     *         a non-null string representing the email confirmation token used to validate the user's email address;
     *         must not be null.
     */
    void verifyEmailAddress( @NonNull String aEmailConfirmationToken );

    /**
     * Sends a credential reset link to the specified email address. This method communicates with an external
     * authentication service to generate and send the reset link to the given email address.
     *
     * @param aDto
     *         A data transfer object representing the data necessary for executing a password reset request.
     * @throws NullPointerException
     *         If the provided email address is null.
     * @throws IllegalArgumentException
     *         If the provided email address is empty.
     */
    void sendCredentialsResetLink( PasswordResetRequestDto aDto );

    /**
     * Changes the password of an account by sending a password change request to the authentication service.
     *
     * @param aUserAccessToken
     *         the access token of the user, used for authorization, must not be null or empty
     * @param aPasswordChangingRequestDto
     *         the data transfer object containing the new password details; must not be null
     */
    void changeAccountPassword( @NonNull String aUserAccessToken, @NonNull PasswordChangingRequestDto aPasswordChangingRequestDto );

}
