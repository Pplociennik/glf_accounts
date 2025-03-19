package com.goaleaf.accounts.system.lang;

import com.github.pplociennik.commons.lang.TranslationKey;

/**
 * This enum provides keys for exception-related translations used in the accounts module.
 * It serves as a bridge between Java code and the corresponding resource bundles containing
 * user-readable messages for exceptions.
 *
 * @author Created by: Pplociennik at 19.03.2025 19:52
 */
public enum AccountsExcTranslationKey implements TranslationKey {

    /**
     * Username incorrect. Username can be 1-20 characters long and may contain only [a-z] (upper and lower case) characters, '-', '_' characters, and [0-9] numbers.
     */
    INVALID_USERNAME,

    /**
     * Email address is incorrect.
     */
    INVALID_EMAIL_ADDRESS,

    /**
     * Password is incorrect! Password has to:
     * - be 8 - 128 characters long,
     * - contain at least 1 upper case character,
     * - contain at least one lower case character,
     * - contain at least one special character eg. `@`, `#`, `$`, `%` etc.,
     * - contain at least one number 0 - 9,
     * - not contain username.
     */
    INVALID_PASSWORD,

    /**
     * The provided passwords are different.
     */
    PASSWORDS_NOT_MATCH,

    /**
     * Registration could not be finished properly due to system error.
     */
    REGISTRATION_FAILED,

    /**
     * Could not authenticate user: {0}. Reason: {1}.
     */
    AUTHENTICATION_FAILED,

    /**
     * Session expired. Please, sign in again.
     */
    SESSION_EXPIRED,

    ;

    public static final String RESOURCE_BUNDLE_NAME = "lang/AccountsExcTranslationKey";

    /**
     * Returns the BASENAME property being the name of the source file of the implementing keys.
     *
     * @return A string with the source file name.
     */
    @Override
    public String getTranslationsSourcePropertyName() {
        return RESOURCE_BUNDLE_NAME;
    }
}
