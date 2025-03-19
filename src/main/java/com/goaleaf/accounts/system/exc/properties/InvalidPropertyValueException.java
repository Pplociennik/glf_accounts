package com.goaleaf.accounts.system.exc.properties;

import com.github.pplociennik.commons.exc.BaseRuntimeException;
import com.github.pplociennik.commons.lang.TranslationKey;

import java.io.Serializable;

/**
 * An exception being thrown when a property value is invalid.
 *
 * @author Created by: Pplociennik at 19.03.2025 20:10
 */
public class InvalidPropertyValueException extends BaseRuntimeException {

    /**
     * Constructs a new exception with a given translation key and parameters.
     *
     * @param aTranslationKey
     *         a translation key.
     * @param aParams
     *         parameters for the translation key.
     */
    public InvalidPropertyValueException( TranslationKey aTranslationKey, Serializable... aParams ) {
        super( aTranslationKey, aParams );
    }
}
