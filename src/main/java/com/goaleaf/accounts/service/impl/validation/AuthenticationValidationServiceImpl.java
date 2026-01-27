package com.goaleaf.accounts.service.impl.validation;

import com.github.pplociennik.commons.validation.Validator;
import com.goaleaf.accounts.data.dto.account.PasswordChangingRequestDto;
import com.goaleaf.accounts.data.dto.auth.AuthenticationRequestDto;
import com.goaleaf.accounts.data.dto.auth.CredentialsDto;
import com.goaleaf.accounts.data.dto.auth.RegistrationRequestDto;
import com.goaleaf.accounts.data.dto.keycloak.AccountDto;
import com.goaleaf.accounts.persistence.repository.UserDetailsRepository;
import com.goaleaf.accounts.service.AccountService;
import com.goaleaf.accounts.service.validation.AuthenticationValidationService;
import com.goaleaf.accounts.system.exc.auth.AccountNotVerifiedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import static com.github.pplociennik.commons.utility.CustomObjects.requireNonEmpty;
import static com.goaleaf.accounts.system.lang.AccountsExcTranslationKey.*;
import static java.util.Objects.requireNonNull;

/**
 * Implementation of the {@link AuthenticationValidationService} interface responsible for handling validation logic
 * for user authentication and registration. This class ensures that all provided inputs, such as username, email, and
 * password, conform to the defined business rules and security standards before proceeding with the registration process.
 * <p>
 * The validation includes checking username format, email format, and password strength based on predefined
 * regular expressions, ensuring consistency and standardization across the authentication flow.
 * </p>
 *
 * @author Created by: Pplociennik
 * @since 24.03.2025
 */
@Service
class AuthenticationValidationServiceImpl implements AuthenticationValidationService {

    /**
     * Regular expression used to validate if a username meets the required constraints for registration.
     * <p>
     * The pattern enforces usernames to:
     * <br>
     * - Consist of alphanumeric characters, underscores (_), or hyphens (-).
     * <br>
     * - Be at least 1 character long and no more than 20 characters.
     * <p>
     * It ensures that the username is simple, readable, and adheres to predefined format rules.
     */
    private static final String VALID_USERNAME_REGEX = "^[a-zA-Z0-9_-]{1,20}$";

    /**
     * Regular expression used to validate if an email address meets the standard email formatting rules.
     * <p>
     * The pattern enforces email addresses to:
     * <br>
     * - Contain alphanumeric characters (uppercase or lowercase), dots (.), underscores (_), percent (%),
     * plus (+), or hyphens (-) before the "@" symbol.
     * <br>
     * - Include a domain name consisting of alphanumeric characters and hyphens (-), separated by dots (.).
     * <br>
     * - End with a valid top-level domain (at least two alphabetic characters).
     * <p>
     * This ensures compatibility with common email address formats while preventing invalid entries.
     */
    private static final String VALID_EMAIL_ADDRESS_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

    /**
     * Regular expression used to validate if a password meets the required security standards.
     * <p>
     * The pattern enforces passwords to:
     * <br>
     * - Contain at least one lowercase letter.
     * <br>
     * - Contain at least one uppercase letter.
     * <br>
     * - Contain at least one digit.
     * <br>
     * - Contain at least one punctuation or symbol character.
     * <br>
     * - Exclude any whitespace characters.
     * <br>
     * - Be at least 8 characters long and no longer than 128 characters.
     * <p>
     * This ensures that passwords are strong and comply with modern security practices.
     */
    private static final String VALID_PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[\\p{P}\\p{S}])[^\\s]{8,128}$";

    /**
     * An injected instance of the {@link UserDetailsRepository}.
     */
    private final UserDetailsRepository userDetailsRepository;

    @Autowired
    AuthenticationValidationServiceImpl( UserDetailsRepository aUserDetailsRepository ) {
        this.userDetailsRepository = aUserDetailsRepository;
    }

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
    @Override
    public RegistrationRequestDto validateRegistrationRequest( @NonNull RegistrationRequestDto aDto ) {
        requireNonNull( aDto );
        Validator.of( aDto )
                .validate( RegistrationRequestDto::getUsername, this::validateUsername, INVALID_USERNAME )
                .validate( RegistrationRequestDto::getEmail, this::validateEmailAddress, INVALID_EMAIL_ADDRESS )
                .validate( this::validateCredentials, INVALID_PASSWORD )
                .perform();

        return aDto;
    }

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
    @Override
    public PasswordChangingRequestDto validatePasswordChangingRequest( @NonNull PasswordChangingRequestDto aDto ) {
        requireNonNull( aDto );
        Validator.of( aDto )
                .validate( this::validatePasswordsEqual, PASSWORDS_NOT_MATCH )
                .validate( PasswordChangingRequestDto::getNewPassword, this::validatePassword, INVALID_PASSWORD )
                .perform();

        return aDto;
    }

    /**
     * Validates the provided email to determine whether it is eligible for initiating a password reset request.
     * This method checks if a user with the specified email address exists in the database.
     *
     * @param aEmail
     *         the email address submitted for password reset validation. Must not be null.
     * @return {@code true} if the user exists, {@code false} otherwise.
     */
    @Override
    public boolean validatePasswordResetRequest( @NonNull String aEmail ) {
        requireNonEmpty( aEmail );
        return userDetailsRepository.findByEmailAddress( aEmail ).isPresent();
    }

    private boolean validatePasswordsEqual( PasswordChangingRequestDto aDto ) {
        String newPassword = aDto.getNewPassword();
        String confirmation = aDto.getConfirmation();
        return newPassword.equals( confirmation );
    }

    private boolean validateCredentials( @NonNull RegistrationRequestDto aDto ) {
        requireNonNull( aDto );
        CredentialsDto[] credentials = aDto.getCredentials();

        boolean validCredentials = true;

        for ( CredentialsDto credentialDto : credentials ) {
            String password = credentialDto.getValue();

            if ( !password.matches( VALID_PASSWORD_REGEX ) || password.contains( aDto.getUsername() ) ) {
                validCredentials = false;
                break;
            }
        }

        return validCredentials;
    }

    private boolean validatePassword( @NonNull String aPassword ) {
        return aPassword.matches( VALID_PASSWORD_REGEX );
    }

    private boolean validateEmailAddress( @NonNull String aEmailAddress ) {
        requireNonNull( aEmailAddress );
        return aEmailAddress.matches( VALID_EMAIL_ADDRESS_REGEX );
    }

    private boolean validateUsername( @NonNull String aUsername ) {
        requireNonNull( aUsername );
        return aUsername.matches( VALID_USERNAME_REGEX );
    }
}
