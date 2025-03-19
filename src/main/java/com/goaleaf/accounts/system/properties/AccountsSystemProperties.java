package com.goaleaf.accounts.system.properties;

import com.github.pplociennik.commons.system.SystemProperty;

import java.util.Set;

/**
 * System properties related to the accounts service.
 *
 * @author Created by: Pplociennik at 19.03.2025 19:25
 */
public enum AccountsSystemProperties implements SystemProperty {

    AUTH_SERVICE_URL( "com.goaleaf.accounts.auth.service.url" );

    // #################################################################################################################

    /**
     * A name of the system property.
     */
    private final String name;

    /**
     * Possible values of the system property.
     */
    private final Set< String > possibleValues;

    /**
     * Creates a new instance of the system property.
     *
     * @param name
     *         a name of the system property.
     */
    AccountsSystemProperties( String name ) {
        this.name = name;
        this.possibleValues = Set.of();
    }

    /**
     * Creates a new instance of the system property.
     *
     * @param name
     *         a name of the system property.
     * @param possibleValue
     *         a possible values of the system property.
     */
    AccountsSystemProperties( String name, String... possibleValue ) {
        this.name = name;
        this.possibleValues = Set.of( possibleValue );
    }

    /**
     * Returns the name of the system property.
     *
     * @return a {@link String} being a name of the property.
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Returns possible values of the system property.
     *
     * @return a {@link Set} of the property's possible values. Empty one if there is no restriction on values and all are possible.
     */
    @Override
    public Set< String > getPossibleValues() {
        return Set.of();
    }

}
