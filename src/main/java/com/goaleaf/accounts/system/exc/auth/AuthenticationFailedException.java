package com.goaleaf.accounts.system.exc.auth;

import com.github.pplociennik.commons.exc.BaseRuntimeException;
import com.github.pplociennik.commons.lang.TranslationKey;
import org.springframework.lang.NonNull;

import java.io.Serializable;

/**
 * An exception class that handles authentication-related errors. This exception is thrown
 * when a user authentication operation encounters issues such as invalid credentials or
 * system-level problems.
 *
 * <p>
 * It extends the {@link BaseRuntimeException} class and supports message translation keys
 * for error localization. Additional parameters can be passed for detailed error descriptions.
 * </p>
 *
 * @see BaseRuntimeException
 * @see TranslationKey
 *
 * <p><b>Author:</b> Pplociennik</p>
 * <p><b>Created:</b> 25.03.2025 20:49</p>
 */
public class AuthenticationFailedException extends BaseRuntimeException {

    /**
     * Constructs a new {@link AuthenticationFailedException} using a translation key
     * and additional parameters for detailed error information.
     *
     * @param aTranslationKey
     *         The translation key used for error localization.
     * @param aParams
     *         The parameters used to construct a detailed error message.
     */
    public AuthenticationFailedException( @NonNull TranslationKey aTranslationKey, Serializable... aParams ) {
        super( aTranslationKey, aParams );
    }

    /**
     * Constructs a new {@link AuthenticationFailedException} using only a translation key for error localization.
     *
     * @param aTranslationKey
     *         The translation key used for error localization.
     */
    public AuthenticationFailedException( @NonNull TranslationKey aTranslationKey ) {
        super( aTranslationKey );
    }

    /**
     * Constructs an {@code AuthenticationException} with a specific error message.
     *
     * @param aMessage
     *         the detailed error message describing the authentication failure.
     */
    public AuthenticationFailedException( @NonNull String aMessage ) {
        super( aMessage );
    }
}
