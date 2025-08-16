package com.goaleaf.accounts.system.exc.request;

import com.github.pplociennik.commons.exc.BaseRuntimeException;
import com.github.pplociennik.commons.lang.TranslationKey;

import java.io.Serializable;

/**
 * An exception being thrown when the token could not be refreshed properly.
 *
 * @author Created by: Pplociennik at 27.04.2025 20:37
 */
public class TokenRefreshFailedException extends BaseRuntimeException {


    /**
     * Constructs a new {@code BaseRuntimeException} with a specified translation key and parameters.
     * The localized message is generated using the provided translation key and parameters.
     *
     * @param aTranslationKey
     *         the translation key used to retrieve the localized message
     * @param aParams
     *         optional parameters for constructing the localized message
     */
    public TokenRefreshFailedException( TranslationKey aTranslationKey, Serializable... aParams ) {
        super( aTranslationKey, aParams );
    }

    /**
     * Constructs a new {@code BaseRuntimeException} with a specified translation key.
     * The localized message is generated using the provided translation key.
     *
     * @param aTranslationKey
     *         the translation key used to retrieve the localized message
     */
    public TokenRefreshFailedException( TranslationKey aTranslationKey ) {
        super( aTranslationKey );
    }

    /**
     * Constructs a new {@code BaseRuntimeException} with the specified cause, translation key, and parameters.
     * This constructor allows specifying a throwable cause, a translation key for the localized message,
     * and optional parameters for constructing the localized message.
     *
     * @param aCause
     *         the cause of the exception, which may be retrieved later by the {@link #getCause()} method.
     *         A {@code null} value is permitted and indicates that the cause is nonexistent or unknown.
     * @param aMessageKey
     *         the translation key used to retrieve the localized message.
     * @param aParams
     *         optional parameters for constructing the localized message.
     */
    public TokenRefreshFailedException( Throwable aCause, TranslationKey aMessageKey, Serializable... aParams ) {
        super( aCause, aMessageKey, aParams );
    }

    /**
     * Constructs a new runtime exception with the specified detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     *
     * @param message
     *         the detail message. The detail message is saved for
     *         later retrieval by the {@link #getMessage()} method.
     */
    public TokenRefreshFailedException( String message ) {
        super( message );
    }

    /**
     * Constructs a new runtime exception with the specified detail message and
     * cause.  <p>Note that the detail message associated with
     * {@code cause} is <i>not</i> automatically incorporated in
     * this runtime exception's detail message.
     *
     * @param message
     *         the detail message (which is saved for later retrieval
     *         by the {@link #getMessage()} method).
     * @param cause
     *         the cause (which is saved for later retrieval by the
     *         {@link #getCause()} method).  (A {@code null} value is
     *         permitted, and indicates that the cause is nonexistent or
     *         unknown.)
     * @since 1.4
     */
    public TokenRefreshFailedException( String message, Throwable cause ) {
        super( message, cause );
    }
}
