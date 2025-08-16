package com.goaleaf.accounts.system.util;

import lombok.extern.log4j.Log4j2;
import org.springframework.lang.NonNull;

import java.util.Set;

import static java.util.Objects.requireNonNull;

/**
 * Utility class for resolving URL templates by substituting placeholders with provided parameters.
 * <p>
 * This class provides methods to verify and format URL templates, ensuring the correct number of parameters
 * are provided and placeholders are replaced as expected.
 * </p>
 *
 * @author Created by: Pplociennik
 * @since 01.04.2025 23:35
 */
@Log4j2
public final class UrlTemplatesResolverUtil {

    /**
     * Resolves a URL template using the provided parameters.
     * Validates the parameters against the template's requirements and substitutes the placeholders in
     * the template with the provided values.
     *
     * @param aKeycloakUrlTemplate
     *         The URL template enum containing the template string and parameter details.
     * @param aParameters
     *         The set of parameters to replace placeholders in the template.
     * @return The resolved URL as a string.
     *
     * @throws IllegalArgumentException
     *         if the number of provided parameters does not match the number required.
     */
    public static String resolveUrlTemplate( @NonNull KeycloakUrlTemplates aKeycloakUrlTemplate, @NonNull Object... aParameters ) {
        log.debug( "Resolving url template for {}", aKeycloakUrlTemplate );
        requireNonNull( aKeycloakUrlTemplate );
        requireNonNull( aParameters );

        verifyParameters( aKeycloakUrlTemplate, aParameters );
        return String.format( aKeycloakUrlTemplate.getKeycloakUrlTemplate(), aParameters );
    }

    /**
     * Verifies if the provided parameters match the required parameters of the specified URL template.
     *
     * @param aKeycloakUrlTemplate
     *         The URL template specifying the required parameters.
     * @param aParameters
     *         The parameters provided to match the template's requirements.
     * @throws IllegalArgumentException
     *         if the number of provided parameters does not match the number required.
     */
    private static void verifyParameters( KeycloakUrlTemplates aKeycloakUrlTemplate, Object... aParameters ) {
        Set< String > requiredParametersNames = aKeycloakUrlTemplate.getRequiredParametersNames();

        if ( requiredParametersNames.size() != aParameters.length ) {
            log.error( "Required parameters count mismatch" );
            throw new IllegalArgumentException( "Wrong number of parameters provided! Expected " + requiredParametersNames.size() + " but got " + aParameters.length + "! Expected parameters are: " + requiredParametersNames );
        }
    }
}
