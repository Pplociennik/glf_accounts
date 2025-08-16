package com.goaleaf.accounts.system.exc.auth;

import com.github.pplociennik.commons.exc.BaseRuntimeException;
import com.github.pplociennik.commons.lang.TranslationKey;
import com.goaleaf.accounts.system.lang.AccountsExcTranslationKey;

import java.io.Serializable;

/**
 * Exception thrown when a user session has expired, typically as a result of timeout or inactivity.
 * This exception is used to indicate that the user needs to log in again to continue accessing the service.
 * <p>
 * It extends {@link BaseRuntimeException}, providing a base class for runtime exceptions
 * that include support for translations and additional properties.
 * </p>
 *
 * @author Created
 * by: Pplociennik at 16.04.2025 18:46
 * @see BaseRuntimeException
 * @see TranslationKey
 * @see Serializable
 */
public class SessionExpiredException extends BaseRuntimeException {

    public SessionExpiredException() {
        super( AccountsExcTranslationKey.SESSION_EXPIRED );
    }
}
