package com.goaleaf.accounts.system.exc.auth;

import com.github.pplociennik.commons.exc.BaseRuntimeException;
import com.goaleaf.accounts.system.lang.AccountsExcTranslationKey;

/**
 * Thrown when a verification attempt is made on an account that has already been verified.
 *
 * @author Pplociennik
 * @since 1.0
 * @see BaseRuntimeException
 */
public class AccountAlreadyVerifiedException extends BaseRuntimeException {
    /**
     * Constructs a new {@code AccountAlreadyVerifiedException} with the
     * {@link AccountsExcTranslationKey#ACCOUNT_ALREADY_VERIFIED} translation key.
     */
    public AccountAlreadyVerifiedException() {
        super( AccountsExcTranslationKey.ACCOUNT_ALREADY_VERIFIED );
    }
}
