package com.goaleaf.accounts.service.system;

import com.github.pplociennik.commons.system.SystemProperty;
import lombok.NonNull;

/**
 * A service providing functionality of reading system properties.
 *
 * @author Created by: Pplociennik at 19.03.2025 19:41
 */
public interface SystemPropertiesReader {

    /**
     * Reads a property with a given name from the system environment.
     *
     * @param aPropertyName
     *         a name of the property to read.
     * @return a value of the property.
     */
    String readProperty( @NonNull String aPropertyName );

    /**
     * Reads a property with a given name from the system environment.
     *
     * @param aProperty
     *         a property to read.
     * @return a value of the property.
     * @throws
     */
    String readProperty( @NonNull SystemProperty aProperty );
}
