package com.goaleaf.accounts.system.exc.auth;

import com.github.pplociennik.commons.exc.BaseRuntimeException;
import com.github.pplociennik.commons.lang.TranslationKey;
import org.springframework.lang.NonNull;

import java.io.Serializable;

/**
 * A custom exception representing a failure during user registration within the system. This exception is typically
 * thrown when an issue arises during the user registration process, such as invalid input, unmet constraints,
 * or a similar failure. It extends {@link BaseRuntimeException} to leverage localization capabilities for error
 * messages using translation keys.
 * <p>
 * Clients can utilize this exception to handle registration failures with meaningful, localized messages.
 * </p>
 *
 * @author Created by: Pplociennik at 24.03.2025 21:02
 * @see BaseRuntimeException
 * @see TranslationKey
 * @see Serializable
 */
public class RegistrationFailedException extends BaseRuntimeException {

    /**
     * Constructs a {@code RegistrationFailedException} with a specified translation key for localized error messaging.
     *
     * @param aTranslationKey
     *         the translation key indicating the specific error message to be used.
     */
    public RegistrationFailedException( @NonNull TranslationKey aTranslationKey ) {
        super( aTranslationKey );
    }

    /**
     * Constructs a {@code RegistrationFailedException} with a specified translation key and parameters for dynamic
     * error message formatting.
     *
     * @param aTranslationKey
     *         the translation key indicating the specific error message to be used.
     * @param aParams
     *         the parameters to be applied to the localized error message template.
     */
    public RegistrationFailedException( @NonNull TranslationKey aTranslationKey, Serializable... aParams ) {
        super( aTranslationKey, aParams );
    }

    /**
     * Constructs a {@code RegistrationFailedException} with a specific error message.
     *
     * @param aMessage
     *         the detailed error message describing the registration failure.
     */
    public RegistrationFailedException( @NonNull String aMessage ) {
        super( aMessage );
    }


}
