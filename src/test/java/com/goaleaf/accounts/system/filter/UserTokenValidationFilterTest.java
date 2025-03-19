package com.goaleaf.accounts.system.filter;

import com.github.pplociennik.commons.system.registry.CollectingSystemRegistry;
import com.github.pplociennik.commons.system.registry.impl.HashSetBasedSystemRegistry;
import com.goaleaf.accounts.data.dto.response.AuthenticationTokenDto;
import com.goaleaf.accounts.data.dto.user.UserSessionDetailsDto;
import com.goaleaf.accounts.service.AuthenticationService;
import com.goaleaf.accounts.service.UserSessionDetailsService;
import com.goaleaf.accounts.system.exc.auth.SessionExpiredException;
import com.goaleaf.accounts.system.util.AccessTokenUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.springframework.mock.web.MockHttpServletRequest;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link UserTokenValidationFilter} class validating tokens used for session management.
 *
 * @author Created by: Pplociennik at 28.05.2025 17:37
 */
class UserTokenValidationFilterTest {

    private static final String TEST_PATH = "/api/test";
    private static final String TEST_PATH_1 = "/api/test_1";
    private static final String TEST_PATH_2 = "/api/test_2";
    private static final String TEST_PATH_3 = "/api/test_3";

    private static final String TEST_ACCESS_TOKEN = "testAccessToken";
    private static final String TEST_ACCESS_TOKEN_2 = "testAccessToken_2";
    private static final String TEST_SESSION_ID = "1";
    private static final String TEST_REFRESH_TOKEN = "testRefreshToken";
    private static final String TEST_REFRESH_TOKEN_2 = "testRefreshToken_2";
    /**
     * A mocked static instance of the {@code AccessTokenUtils} class.
     * <p>
     * This field is used to simulate and control the behavior of the {@code AccessTokenUtils}
     * utility methods in a testing environment. Mocking enables the definition of
     * predefined responses or behaviors for static methods in {@code AccessTokenUtils},
     * allowing for isolated and predictable unit tests within the {@code UserTokenValidationFilterTest}.
     * <p>
     * The mock is typically initialized, configured, and manipulated using the relevant
     * mocking framework capabilities. It is primarily utilized to verify interactions
     * and responses suited to the testing scenarios outlined in the associated test methods.
     */
    private static MockedStatic< AccessTokenUtils > accessTokenUtilsMock;
    /**
     * The {@code underTest} variable is an instance of {@link UserTokenValidationFilter}
     * used in the test class to validate and filter user token inputs during test cases.
     * It is the main component under test in the {@code UserTokenValidationFilterTest} class.
     */
    private UserTokenValidationFilter underTest;
    /**
     * Represents a dependency on the {@code UserSessionDetailsService}, used for managing
     * user session details such as creation, retrieval, validation, and deletion of sessions.
     * This field is utilized in various tests to verify session-related functionalities
     * in the {@code UserTokenValidationFilterTest} class.
     */
    private UserSessionDetailsService userSessionDetailsService;
    /**
     * A reference to the {@code AuthenticationService}, which provides authentication-related operations
     * such as user account registration, user authentication, session management, and token refreshing.
     * This variable is used in the context of testing the functionalities within {@code UserTokenValidationFilterTest}.
     */
    private AuthenticationService authenticationService;
    /**
     * A private instance of CollectingSystemRegistry used for storing and managing a
     * collection of registries related to the system's configuration or paths.
     * <p>
     * This variable is utilized in various test methods within the class
     * to validate behavior based on the presence or absence of paths in the registry.
     */
    private CollectingSystemRegistry< String > systemRegistry;

    /**
     * Prepares static mocks for use in the test class.
     * <p>
     * This method sets up a mock for the static methods of the {@link AccessTokenUtils} class.
     * It is annotated with {@code @BeforeAll}, ensuring it runs once before any tests in the
     * class. The mocked static methods are used to isolate and test behaviors without invoking
     * the actual static methods.
     */
    @BeforeAll
    static void prepareStaticMocks() {
        accessTokenUtilsMock = mockStatic( AccessTokenUtils.class );
    }

    /**
     * Prepares the test environment.
     */
    @BeforeEach
    void setUp() {
        userSessionDetailsService = prepareUserSessionDetailsService();
        authenticationService = prepareAuthenticationService();
        systemRegistry = new HashSetBasedSystemRegistry<>();

        underTest = new UserTokenValidationFilter( userSessionDetailsService, authenticationService, systemRegistry );
    }

    /**
     * Prepares the {@link AuthenticationService}.
     */
    private AuthenticationService prepareAuthenticationService() {
        AuthenticationService mock = mock( AuthenticationService.class );
        return mock;
    }

    /**
     * Prepares the {@link UserSessionDetailsService}.
     */
    private UserSessionDetailsService prepareUserSessionDetailsService() {
        UserSessionDetailsService mock = mock( UserSessionDetailsService.class );
        return mock;
    }

    // #################################################################################################################

    /**
     * Verifies that the {@code shouldNotFilter} method of the {@code underTest} instance
     * accurately returns {@code true} when the request URI does not match any path
     * in the system's registry.
     * <p>
     * The system registry is pre-populated with specific test paths prior to testing.
     * A mock HTTP request is then configured with a URI distinct from those in the registry.
     * Finally, the method asserts that the given request should not be filtered.
     * <p>
     * Test Scenario:
     * - A registry containing paths is initialized.
     * - A request with a URI not present in the registry is checked.
     * - The expectation is that the method will return {@code true}.
     */
    @Test
    void methodShouldNotFilter_shouldReturnTrue_whenPathNotInRegistry() {

        // GIVEN
        systemRegistry.add( TEST_PATH_1, TEST_PATH_2 );
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI( TEST_PATH_3 );

        // WHEN
        boolean result = underTest.shouldNotFilter( request );

        // THEN
        assertTrue( result );
    }

    /**
     * Verifies that the `shouldNotFilter` method of the `underTest` instance correctly returns {@code false}
     * when the request URI matches a path present in the system's registry.
     * <p>
     * During this test:
     * - The system registry is initialized with predefined test paths.
     * - A mock HTTP request is created with a URI matching one of the paths in the registry.
     * - The method's response is checked to confirm that the URI should be filtered, indicating the method returns {@code false}.
     * <p>
     * Test Scenario:
     * - A registry containing specific paths is populated.
     * - A mock request is configured with a URI that matches one of the registry paths.
     * - The expectation is that the method will return {@code false}.
     */
    @Test
    void methodShouldNotFilter_shouldReturnFalse_whenPathInRegistry() {

        // GIVEN
        systemRegistry.add( TEST_PATH_1, TEST_PATH_2 );
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI( TEST_PATH_1 );

        // WHEN
        boolean result = underTest.shouldNotFilter( request );

        // THEN
        assertFalse( result );
    }

    /**
     * Verifies that the `shouldNotFilter` method of the `underTest` instance correctly
     * returns {@code false} when the request URI starts with a prefix that matches
     * a path present in the system's registry but isn't an exact match but starts similarly.
     * <p>
     * During this test:
     * - The system registry is initialized with predefined test paths.
     * - A mock HTTP request is created with a URI that starts with the same prefix as
     * one of the paths in the registry but does not match exactly.
     * - The method's response is checked to confirm that the URI should be filtered,
     * indicating the method returns {@code false}.
     * <p>
     * Test Scenario:
     * - A registry containing specific paths is populated.
     * - A mock request is configured with a URI starting similarly to a registry path.
     * - The expectation is that the method will return {@code false}.
     */
    @Test
    void methodShouldNotFilter_shouldReturnFalse_whenPathStartingSimilarlyInRegistry() {

        // GIVEN
        systemRegistry.add( TEST_PATH, TEST_PATH_1, TEST_PATH_2 );
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI( TEST_PATH_3 );

        // WHEN
        boolean result = underTest.shouldNotFilter( request );

        // THEN
        assertFalse( result );
    }

    /**
     * Verifies that the `shouldNotFilter` method of the `underTest` instance
     * correctly returns {@code true} when the system registry is empty.
     * <p>
     * During this test:
     * - An empty registry is set up.
     * - A mock HTTP request is created with a test URI.
     * - The method is invoked to determine if the request should not be filtered.
     * - The result is asserted to confirm that the method returns {@code true}.
     * <p>
     * Test Scenario:
     * - The system registry contains no paths.
     * - A mock request is created with a URI.
     * - The expectation is that the method will return {@code true}.
     */
    @Test
    void methodShouldNotFilter_shouldReturnTrue_whenRegistryIsEmpty() {

        // GIVEN
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI( TEST_PATH_3 );

        // WHEN
        boolean result = underTest.shouldNotFilter( request );

        // THEN
        assertTrue( result );
    }

    /**
     * Verifies that a {@link SessionExpiredException} is thrown when the provided access token
     * is invalid and cannot be refreshed using the associated refresh token.
     * <p>
     * During this test:
     * - A mock HTTP request is set up with an invalid access token in the "User-Token" header.
     * - The access token is checked and determined to be invalid.
     * - The related session ID is retrieved from the invalid access token.
     * - The user session details are fetched for the session ID.
     * - The refresh token associated with the session is also checked and determined to be invalid.
     * - The method asserts that attempting to filter this request results in a {@code SessionExpiredException}.
     */
    @Test
    void shouldThrowSessionExpiredException_whenAccessTokenInvalidAndCannotBeRefreshed() {

        // WHEN
        HttpServletRequest request = mock( HttpServletRequest.class );
        when( request.getHeader( "User-Token" ) ).thenReturn( TEST_ACCESS_TOKEN );

        when( userSessionDetailsService.checkAccessToken( TEST_ACCESS_TOKEN ) ).thenReturn( false );

        accessTokenUtilsMock.when( () -> AccessTokenUtils.getSessionId( TEST_ACCESS_TOKEN ) ).thenReturn( TEST_SESSION_ID );

        UserSessionDetailsDto details = getUserSessionDetails();
        when( userSessionDetailsService.getUserSessionDetails( TEST_SESSION_ID ) ).thenReturn( Optional.of( details ) );

        when( userSessionDetailsService.checkAccessToken( TEST_REFRESH_TOKEN ) ).thenReturn( false );

        // THEN
        assertThrows( SessionExpiredException.class, () -> underTest.doFilterInternal( request, null, null ) );
    }

    /**
     * Verifies that an {@link IllegalStateException} is thrown when the provided access token
     * is invalid and an error occurs during the token refresh process.
     * <p>
     * Test Setup:
     * - A mock HTTP request is created with an invalid "User-Token" header containing a test access token.
     * - The associated session ID is retrieved from the invalid access token.
     * - User session details are successfully fetched for the session ID.
     * - A valid refresh token for the session is used to successfully generate a new access token.
     * - The refreshed access token is checked and determined to be invalid.
     * <p>
     * Test Execution:
     * - The method under test is invoked with a mocked request, response, and filter chain.
     * <p>
     * Test Assertions:
     * - Confirms that the method throws an {@code IllegalStateException} due to the error in validating the refreshed access token.
     */
    @Test
    void shouldThrowIllegalStateException_whenAccessTokenInvalidAndThereWasAnErrorDuringRefresh() {

        // WHEN
        HttpServletRequest request = mock( HttpServletRequest.class );
        HttpServletResponse response = mock( HttpServletResponse.class );
        FilterChain filterChain = mock( FilterChain.class );

        // get user access token from header
        when( request.getHeader( "User-Token" ) ).thenReturn( TEST_ACCESS_TOKEN );

        // get session id from the user access token
        accessTokenUtilsMock.when( () -> AccessTokenUtils.getSessionId( TEST_ACCESS_TOKEN ) ).thenReturn( TEST_SESSION_ID );

        // get session details by session id
        UserSessionDetailsDto details = getUserSessionDetails();
        when( userSessionDetailsService.getUserSessionDetails( TEST_SESSION_ID ) ).thenReturn( Optional.of( details ) );

        // first check of user access token = false
        when( userSessionDetailsService.checkAccessToken( TEST_ACCESS_TOKEN ) ).thenReturn( false );

        // check of refresh token = true
        when( userSessionDetailsService.checkAccessToken( TEST_REFRESH_TOKEN ) ).thenReturn( true );

        // refresh user access token
        AuthenticationTokenDto refreshedToken = new AuthenticationTokenDto();
        refreshedToken.setAccessToken( TEST_ACCESS_TOKEN_2 );
        refreshedToken.setRefreshToken( TEST_REFRESH_TOKEN_2 );
        when( authenticationService.refreshUserSession( TEST_ACCESS_TOKEN ) ).thenReturn( refreshedToken );

        // refreshed access token invalid or error during the process
        when( userSessionDetailsService.checkAccessToken( TEST_ACCESS_TOKEN_2 ) ).thenReturn( false );

        // THEN
        assertThrows( IllegalStateException.class, () -> underTest.doFilterInternal( request, response, filterChain ) );
    }

    /**
     * Validates that the method under test does not modify the "User-Token" header
     * in the request when the access token provided is still valid. Additionally,
     * it ensures no access token refresh operation is flagged during this process.
     * <p>
     * Test Setup:
     * - A mock HTTP request is created with a valid "User-Token" header containing a test access token.
     * - The `userSessionDetailsService` mock is configured to return `true` for validating the access token.
     * - Mock objects for `HttpServletResponse` and `FilterChain` are initialized.
     * <p>
     * Test Execution:
     * - The method under test is invoked with the mocked request, response, and filter chain.
     * <p>
     * Test Assertions:
     * - Verifies that the "User-Token" header remains unchanged in the request.
     * - Confirms that the "USER_ACCESS_TOKEN_REFRESHED" attribute in the modified request is set to `false`.
     * - Ensures that the filter chain proceeds with the modified request input.
     *
     * @throws ServletException
     *         if an error occurs while invoking the filter method.
     * @throws IOException
     *         if an I/O exception occurs during the test.
     */
    @Test
    void shouldNotModifyUserAccessTokenHeader_whenAccessTokenIsStillValid() throws ServletException, IOException {

        // GIVEN
        HttpServletRequest request = mock( HttpServletRequest.class );
        HttpServletResponse response = mock( HttpServletResponse.class );
        FilterChain filterChain = mock( FilterChain.class );
        ArgumentCaptor< UserTokenValidationFilter.ModifiedHeaderRequestWrapper > wrapperCaptor = ArgumentCaptor.forClass( UserTokenValidationFilter.ModifiedHeaderRequestWrapper.class );

        when( request.getHeader( "User-Token" ) ).thenReturn( TEST_ACCESS_TOKEN );

        when( userSessionDetailsService.checkAccessToken( TEST_ACCESS_TOKEN ) ).thenReturn( true );

        // WHEN
        underTest.doFilterInternal( request, response, filterChain );

        // THEN
        verify( filterChain ).doFilter( wrapperCaptor.capture(), any() );
        UserTokenValidationFilter.ModifiedHeaderRequestWrapper newWrapper = wrapperCaptor.getValue();

        assertEquals( TEST_ACCESS_TOKEN, newWrapper.getHeader( "User-Token" ) );
        assertFalse( ( Boolean ) newWrapper.getAttribute( "USER_ACCESS_TOKEN_REFRESHED" ) );

    }

    /**
     * Test method to verify that the "User-Token" header is modified by the filter
     * when the access token is successfully refreshed. This includes a series
     * of conditions and scenarios where the initial access token is invalid,
     * the refresh process is triggered, and a new access token is applied to the
     * "User-Token" header.
     * <p>
     * The test ensures that the following steps occur:
     * 1. The initial "User-Token" header contains a stale access token.
     * 2. The service identifies the access token as invalid and fetches the session details.
     * 3. A refresh token is validated, and a new access token is generated.
     * 4. The filter modifies the request to include the new access token in the "User-Token" header.
     * 5. The wrapped request used in the filter chain contains the updated "User-Token" header
     * and the appropriate attribute indicating that the access token has been refreshed.
     *
     * @throws ServletException
     *         if a servlet error occurs during filter processing
     * @throws IOException
     *         if an I/O error occurs during filter processing
     */
    @Test
    void shouldModifyUserAccessTokenHeader_whenAccessTokenRefreshedSuccessfully() throws ServletException, IOException {

        // GIVEN
        HttpServletRequest request = mock( HttpServletRequest.class );
        HttpServletResponse response = mock( HttpServletResponse.class );
        FilterChain filterChain = mock( FilterChain.class );
        ArgumentCaptor< UserTokenValidationFilter.ModifiedHeaderRequestWrapper > wrapperCaptor = ArgumentCaptor.forClass( UserTokenValidationFilter.ModifiedHeaderRequestWrapper.class );

        when( request.getHeader( "User-Token" ) ).thenReturn( TEST_ACCESS_TOKEN );

        // first access token check = false, refreshing...
        when( userSessionDetailsService.checkAccessToken( TEST_ACCESS_TOKEN ) ).thenReturn( false );

        // get session id from a user access token
        accessTokenUtilsMock.when( () -> AccessTokenUtils.getSessionId( TEST_ACCESS_TOKEN ) ).thenReturn( TEST_SESSION_ID );

        // get user session details by session id
        UserSessionDetailsDto details = getUserSessionDetails();
        when( userSessionDetailsService.getUserSessionDetails( TEST_SESSION_ID ) ).thenReturn( Optional.of( details ) );

        // check refresh token = true
        when( userSessionDetailsService.checkAccessToken( TEST_REFRESH_TOKEN ) ).thenReturn( true );

        // refresh token
        AuthenticationTokenDto refreshedToken = new AuthenticationTokenDto();
        refreshedToken.setAccessToken( TEST_ACCESS_TOKEN_2 );
        refreshedToken.setRefreshToken( TEST_REFRESH_TOKEN_2 );
        when( authenticationService.refreshUserSession( TEST_ACCESS_TOKEN ) ).thenReturn( refreshedToken );

        // check refreshed access token = true
        when( userSessionDetailsService.checkAccessToken( TEST_ACCESS_TOKEN_2 ) ).thenReturn( true );

        // WHEN
        underTest.doFilterInternal( request, response, filterChain );

        // THEN
        verify( filterChain ).doFilter( wrapperCaptor.capture(), any() );
        UserTokenValidationFilter.ModifiedHeaderRequestWrapper newWrapper = wrapperCaptor.getValue();

        assertEquals( TEST_ACCESS_TOKEN_2, newWrapper.getHeader( "User-Token" ) );
        assertTrue( ( Boolean ) newWrapper.getAttribute( "USER_ACCESS_TOKEN_REFRESHED" ) );

    }

    /**
     * Retrieves the details of a user's session.
     * <p>
     * This method initializes a {@link UserSessionDetailsDto} instance
     * and sets its session ID and refresh token using predefined values.
     * The populated object is then returned, representing session-specific information.
     *
     * @return a {@link UserSessionDetailsDto} containing the session ID and refresh token.
     */
    private UserSessionDetailsDto getUserSessionDetails() {
        UserSessionDetailsDto details = new UserSessionDetailsDto();
        details.setSessionId( TEST_SESSION_ID );
        details.setRefreshToken( TEST_REFRESH_TOKEN );

        return details;
    }
}