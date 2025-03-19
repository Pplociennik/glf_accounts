package com.goaleaf.accounts.system.exc.properties;

import com.github.pplociennik.commons.exc.BaseRuntimeException;
import com.github.pplociennik.commons.lang.TranslationKey;

import java.io.Serializable;

/**
 * An exception being thrown when no system property with the name provided has been found.
 *
 * @author Created by: Pplociennik at 19.03.2025 19:48
 */
public class NoSuchPropertyExistsException extends BaseRuntimeException {

    /**
     * Creates a new instance.
     *
     * @param aTranslationKey
     *         a translation key
     * @param aParams
     *         parameters.
     */
    public NoSuchPropertyExistsException( TranslationKey aTranslationKey, Serializable... aParams ) {
        super( aTranslationKey, aParams );
    }
}
