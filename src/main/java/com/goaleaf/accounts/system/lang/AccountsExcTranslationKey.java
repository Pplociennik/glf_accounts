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

    ;

    public static final String RESOURCE_BUNDLE_NAME = "accounts-exc";

    /**
     * Returns the BASENAME property being the name of the source file of the implementing keys.
     *
     * @return A string with the source file name.
     */
    @Override
    public String getTranslationsSourcePropertyName() {
        return "lang\\";
    }
}
