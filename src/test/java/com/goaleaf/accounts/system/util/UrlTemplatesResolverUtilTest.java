package com.goaleaf.accounts.system.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link UrlTemplatesResolverUtil} which handles URL template resolution.
 * Contains test cases verifying template parameter validation and substitution functionality.
 *
 * @author Created by: Pplociennik at 28.05.2025 19:38
 */
class UrlTemplatesResolverUtilTest {

    /**
     * Tests the resolution of a URL template when exactly one required parameter is provided.
     * Verifies that the template is correctly filled and matches the expected outcome.
     * <p>
     * The test performs the following steps:
     * 1. Sets up the required parameter.
     * 2. Resolves the URL template by substituting the parameter.
     * 3. Asserts that the resolved URL matches the expected format.
     * <p>
     * Expected behavior:
     * - The resolved URL should include the provided parameter correctly substituted
     * at the corresponding placeholder in the template.
     */
    @Test
    void shouldReturnCorrectlyFilledTemplate_whenThereIsOneRequiredParameter() {

        // GIVEN
        final String param = "TEST_TEMPLATE_PARAM";

        // WHEN
        String result = UrlTemplatesResolverUtil.resolveUrlTemplate( KeycloakUrlTemplates.REGISTRATION_URL_TEMPLATE, param );

        // THEN
        String expected = "/admin/realms/TEST_TEMPLATE_PARAM/users";
        assertEquals( expected, result );
    }

    /**
     * Verifies that the method {@link UrlTemplatesResolverUtil#resolveUrlTemplate(KeycloakUrlTemplates, Object...)}
     * throws an {@link IllegalArgumentException} when more parameters than required are provided.
     * <p>
     * The method under test is expected to validate the number of parameters passed against the required number
     * specified by the URL template. If more parameters are provided than required, an exception should be thrown.
     * <p>
     * Test steps:
     * 1. Prepare two parameters to pass into the method, exceeding the expected parameter count for the specified template.
     * 2. Call the method and assert that it throws an {@link IllegalArgumentException}.
     * <p>
     * Expected behavior:
     * - The method should throw an {@link IllegalArgumentException} indicating that the number of parameters
     * provided exceeds the number required by the template.
     */
    @Test
    void shouldThrowIllegalArgumentException_whenThereIsTooManyParameters() {

        // WHEN
        final String param = "TEST_TEMPLATE_PARAM";
        final String param_2 = "TEST_TEMPLATE_PARAM_2";

        // THEN
        assertThrows( IllegalArgumentException.class, () -> UrlTemplatesResolverUtil.resolveUrlTemplate( KeycloakUrlTemplates.REGISTRATION_URL_TEMPLATE, param, param_2 ) );
    }

    /**
     * Verifies that an {@link IllegalArgumentException} is thrown when the method
     * {@link UrlTemplatesResolverUtil#resolveUrlTemplate(KeycloakUrlTemplates, Object...)}
     * is called with fewer parameters than required by the specified URL template.
     * <p>
     * This test ensures that the validation logic correctly detects and reports the
     * mismatch between the provided and expected number of parameters.
     * <p>
     * Test steps:
     * 1. Define a parameter to pass into the method, which is less than the required number for the template.
     * 2. Call the method and assert that it throws an {@link IllegalArgumentException}.
     * <p>
     * Expected behavior:
     * - The method should throw an {@link IllegalArgumentException} indicating that the number of parameters
     * provided is insufficient for the template.
     */
    @Test
    void shouldThrowIllegalArgumentException_whenThereIsTooLessParameters() {

        // WHEN
        final String param = "TEST_TEMPLATE_PARAM";

        // THEN
        assertThrows( IllegalArgumentException.class, () -> UrlTemplatesResolverUtil.resolveUrlTemplate( KeycloakUrlTemplates.GET_ALL_SESSIONS_URL_TEMPLATE, param ) );
    }

    /**
     * Tests that an {@link IllegalArgumentException} is thrown when the method
     * {@link UrlTemplatesResolverUtil#resolveUrlTemplate(KeycloakUrlTemplates, Object...)}
     * is called with no parameters.
     * <p>
     * This test ensures that the validation logic correctly detects and reports the
     * absence of parameters required for the specified URL template.
     * <p>
     * Test steps:
     * 1. Call the method {@link UrlTemplatesResolverUtil#resolveUrlTemplate(KeycloakUrlTemplates, Object...)}
     * with no parameters.
     * 2. Assert that an {@link IllegalArgumentException} is thrown.
     * <p>
     * Expected behavior:
     * - The method should throw an {@link IllegalArgumentException} indicating
     * that the required parameters are missing.
     */
    @Test
    void shouldThrowIllegalArgumentException_whenThereAreNoParameters() {
        assertThrows( IllegalArgumentException.class, () -> UrlTemplatesResolverUtil.resolveUrlTemplate( KeycloakUrlTemplates.REGISTRATION_URL_TEMPLATE ) );
    }

    /**
     * Verifies that the exception message thrown by the method
     * {@link UrlTemplatesResolverUtil#resolveUrlTemplate(KeycloakUrlTemplates, Object...)}
     * when an incorrect number of parameters is provided contains the names of
     * the required parameters.
     * <p>
     * This test ensures that when the number of supplied parameters does not match
     * the number required by the URL template, the resulting exception:
     * 1. Includes a clear message about the incorrect number of provided parameters.
     * 2. Lists the names of the required parameters to assist in identifying the missing ones.
     * <p>
     * Test steps:
     * 1. Prepare a single parameter that is fewer than the required number for the template.
     * 2. Call the method and handle the {@link IllegalArgumentException}.
     * 3. Validate that the exception message contains the names of all required parameters.
     * <p>
     * Expected behavior:
     * - An {@link IllegalArgumentException} is thrown due to insufficient parameters.
     * - The exception message lists the missing or required parameter names.
     */
    @Test
    void shouldContainNamesOfRequiredParametersInExceptionMessage_whenThereIsIncorrectNumberOfParameters() {

        // WHEN
        final String param = "TEST_TEMPLATE_PARAM";

        // THEN
        try {
            UrlTemplatesResolverUtil.resolveUrlTemplate( KeycloakUrlTemplates.GET_ALL_SESSIONS_URL_TEMPLATE, param );
        } catch ( IllegalArgumentException aE ) {
            String message = aE.getMessage();
            assertTrue( message.contains( "Realm name" ) );
            assertTrue( message.contains( "User ID" ) );
        }
    }


}