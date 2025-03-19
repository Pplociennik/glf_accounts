package com.goaleaf.accounts.service.system.impl;

import com.github.pplociennik.commons.system.SystemProperty;
import com.goaleaf.accounts.service.system.SystemPropertiesReader;
import com.goaleaf.accounts.system.exc.properties.InvalidPropertyValueException;
import com.goaleaf.accounts.system.exc.properties.NoSuchPropertyExistsException;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

/**
 * A service providing functionality of reading system properties.
 *
 * @author Created by: Pplociennik at 19.03.2025 20:04
 */
@Service
@AllArgsConstructor
public class SystemPropertiesReaderImpl implements SystemPropertiesReader {

    private Environment environment;

    /**
     * Reads a property with a given name from the system environment.
     *
     * @param aPropertyName
     *         a name of the property to read.
     * @return a value of the property.
     */
    @Override
    public String readProperty( @NonNull String aPropertyName ) {
        return "";
    }

    /**
     * Reads a property with a given name from the system environment.
     *
     * @param aProperty
     *         a property to read.
     * @return a value of the property.
     *
     * @throws NoSuchPropertyExistsException
     *         when the property does not exist.
     * @throws InvalidPropertyValueException
     *         when the property value is invalid.
     */
    @Override
    public String readProperty( @NonNull SystemProperty aProperty ) {
        return "";
    }
}
