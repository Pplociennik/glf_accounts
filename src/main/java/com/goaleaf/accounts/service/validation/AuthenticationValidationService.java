package com.goaleaf.accounts.service.validation;

import com.goaleaf.accounts.data.dto.account.PasswordChangingRequestDto;
import com.goaleaf.accounts.data.dto.auth.AuthenticationRequestDto;
import com.goaleaf.accounts.data.dto.auth.RegistrationRequestDto;
import com.goaleaf.accounts.system.exc.auth.AccountNotVerifiedException;
import lombok.NonNull;

/**
 * Interface for validating user registration requests in the authentication process.
 * <p>
 * This service ensures that the provided registration data complies with the required rules before further processing.
 * It is a key part of the overall account registration workflow for maintaining data integrity and compliance.
 * </p>
 *
 * <p>Responsibilities:</p>
 * <ul>
 *     <li>Validation of user-provided data in registration requests.</li>
 *     <li>Returning validated or modified registration data transfer objects.</li>
 * </ul>
 *
 * @author Created by: Pplociennik
 * @since 24.03.2025
 */
public interface AuthenticationValidationService {

    /**
     * Validates the specified registration request and ensures that it meets the necessary criteria
     * before proceeding with further processing or registration.
     * <p>
     * This method checks the given data transfer object for completeness, correctness,
     * and compliance with predefined business and validation rules.
     * </p>
     *
     * @param aDto
     *         the registration request data transfer object containing user-provided details
     *         for account registration.
     * @return a validated and possibly modified instance of {@link RegistrationRequestDto}.
     *
     * @throws IllegalArgumentException
     *         if the provided data fails to meet validation criteria.
     */
    RegistrationRequestDto validateRegistrationRequest( @NonNull RegistrationRequestDto aDto );

    /**
     * Validates a {@link PasswordChangingRequestDto} to ensure that it meets the required constraints
     * for changing a user's password. This method checks whether the provided new password matches the confirmation
     * and whether the new password satisfies the defined password validation rules.
     *
     * @param aDto
     *         the password changing request data transfer object containing the current password,
     *         new password, and confirmation of the new password.
     * @return the validated instance of {@link PasswordChangingRequestDto}.
     *
     * @throws IllegalArgumentException
     *         if any of the validation constraints are not met.
     */
    PasswordChangingRequestDto validatePasswordChangingRequest( @NonNull PasswordChangingRequestDto aDto );

    /**
     * Validates the provided email to determine whether it is eligible for initiating a password reset request.
     * This method checks if a user with the specified email address exists in the database.
     *
     * @param aEmail
     *         the email address submitted for password reset validation. Must not be null.
     * @return {@code true} if the user exists, {@code false} otherwise.
     */
    boolean validatePasswordResetRequest( @NonNull String aEmail );
}
