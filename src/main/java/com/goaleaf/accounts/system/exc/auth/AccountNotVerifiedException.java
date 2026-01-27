package com.goaleaf.accounts.system.exc.auth;

import com.github.pplociennik.commons.exc.BaseRuntimeException;
import com.github.pplociennik.commons.lang.TranslationKey;
import lombok.Getter;
import lombok.NonNull;

import java.io.Serializable;

import static java.util.Objects.requireNonNull;

/**
 * An exception being thrown when the unverified user tries to log into the system.
 *
 * @author Created by: Pplociennik at 26.01.2026 20:34
 */
@Getter
public class AccountNotVerifiedException extends BaseRuntimeException {

    /**
     * Stores the email address of the user associated with the exception.
     * This field is used to provide additional context about the specific account
     * involved in the exception, typically when an unverified account attempts
     * to access the system.
     */
    private String emailAddress;

    /**
     * Constructs a new {@code BaseRuntimeException} with a specified translation key and parameters.
     * The localized message is generated using the provided translation key and parameters.
     *
     * @param aTranslationKey
     *         the translation key used to retrieve the localized message
     * @param aParams
     *         optional parameters for constructing the localized message
     */
    public AccountNotVerifiedException( @NonNull String aEmailAddress, TranslationKey aTranslationKey, Serializable... aParams ) {
        super( aTranslationKey, aParams );
        requireNonNull( aEmailAddress );
        emailAddress = aEmailAddress;
    }
}
