package com.goaleaf.accounts.system.util.token;

import com.github.pplociennik.commons.service.TimeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link OfflineValidationStrategy} class.
 * Tests the validation of JWT access tokens based on their expiration time
 * against current system time through offline validation strategy.
 *
 * @author Created by: Pplociennik at 28.05.2025 23:27
 */
class OfflineValidationStrategyTest {

    /**
     * A test token with expiration date time: '2025-05-27T18:49:45Z'.
     */
    private static final String TEST_ACCESS_TOKEN = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJXWXZpRWo5N3NZQmFvWVQ3bnN5Y2w1MnNWdm9oRU1pTzBVUXU1LTlFNHM4In0.eyJleHAiOjE3NDgzNzE3ODUsImlhdCI6MTc0ODM3MTQ4NSwianRpIjoib25ydHJvOjUwMDZmZDU2LTIzMGUtNGMzYi04NzRlLTgyOTY4MmMzYmI3YiIsImlzcyI6Imh0dHA6Ly9sb2NhbGhvc3Q6NzA4MC9yZWFsbXMvZ29hbGVhZiIsImF1ZCI6ImFjY291bnQiLCJzdWIiOiI0NTY4NjUxYi0xNWQ1LTQ0YzItOTQ4ZC0xMmEwMzQ4M2FjMWEiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJnb2FsZWFmLW1zcy1zZXJ2ZXIiLCJzaWQiOiI4NjU2Y2ViNC02YWEwLTRlNTctOTdlZi05MTk4NTkzNThiMTgiLCJhY3IiOiIxIiwiYWxsb3dlZC1vcmlnaW5zIjpbIi8qIl0sInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJvZmZsaW5lX2FjY2VzcyIsInVtYV9hdXRob3JpemF0aW9uIiwiZGVmYXVsdC1yb2xlcy1nb2FsZWFmIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19fSwic2NvcGUiOiJwcm9maWxlIGVtYWlsIiwiZW1haWxfdmVyaWZpZWQiOnRydWUsInByZWZlcnJlZF91c2VybmFtZSI6InBwbG9jaWVubmlrMiIsImVtYWlsIjoicHJ6ZW14Ljg0QGdtYWlsLmNvbSJ9.fjVGhjvObZ6LmnOUVc7ek6Cm9wxFJC2a6ce1GWlc_o10wGTZZNCU53Ja3r_ki2eF2p_MSUwmKGzGGZYeAY6iwr9GfrIqtuJVyVJpABv-ds-jtPQoKvkos5tH0SPy4mRIlH__TnAcCKmBWtdmqXKyzXposRHcxsdEFjvuqZCQk24tlwfGQzW2gx5fJqF8QAPmNzv0bOuC3c8WaF67rAxfrZCSVhRI7wws6uZFZuOkrE1Y6LfqWxCQB2jfJ-jBcvET7vH_6ZzysizynQSEfhQSOvD3UtnDol5avi4bDgaetJtVZAJ4gShn_MNi8KVMqA08ZZKW1mdMvGYB21Uj_SoDtQ";

    /**
     * A mocked instance of the {@link TimeService}.
     */
    private TimeService timeService;

    /**
     * An instance of the object being tested.
     */
    private OfflineValidationStrategy underTest;

    /**
     * Sets up the test environment before each test method is executed.
     * <p>
     * This method initializes the required objects and dependencies necessary for
     * testing the {@code OfflineValidationStrategy} class. Specifically, it:
     * <p>
     * 1. Creates a mocked instance of {@code TimeService} using Mockito.
     * 2. Instantiates the {@code OfflineValidationStrategy} under test
     * with the mocked {@code TimeService} as its dependency.
     * <p>
     * The setup ensures that each test runs in isolation with a consistent
     * and controlled environment.
     */
    @BeforeEach
    void setUp() {
        timeService = Mockito.mock( TimeService.class );
        underTest = new OfflineValidationStrategy( timeService );
    }

    /**
     * Tests the scenario where the access token expiration time is after the current system time.
     * <p>
     * This test verifies that the {@code validateAccessToken} method correctly returns {@code true}
     * when the access token is valid (i.e., its expiration time is later than the current system time).
     * <p>
     * Test Steps:
     * 1. Configure the mocked {@code TimeService} to return a specific current system time.
     * 2. Call the {@code validateAccessToken} method with a predefined test access token.
     * 3. Assert that the method returns {@code true}, indicating that the token is still valid.
     */
    @Test
    void shouldReturnTrue_whenTheTokenExpirationTimeIsAfterCurrentTime() {

        // GIVEN
        ZonedDateTime dateTime = ZonedDateTime.parse( "2025-05-27T18:45:45Z" );
        Mockito.when( timeService.getCurrentSystemDateTime() ).thenReturn( dateTime );

        // WHEN
        boolean result = underTest.validateAccessToken( TEST_ACCESS_TOKEN );

        // THEN
        assertTrue( result );
    }

    /**
     * Tests the scenario where the access token expiration time is before the current system time.
     * <p>
     * This test verifies that the {@code validateAccessToken} method correctly returns {@code false}
     * when the access token has expired (i.e., its expiration time is earlier than the current system time).
     * <p>
     * Test Steps:
     * 1. Configure the mocked {@code TimeService} to return a specific current system time.
     * 2. Call the {@code validateAccessToken} method with a predefined test access token.
     * 3. Assert that the method returns {@code false}, indicating that the token has expired.
     */
    @Test
    void shouldReturnFalse_whenTheTokenExpirationTimeIsBeforeCurrentTime() {

        // GIVEN
        ZonedDateTime dateTime = ZonedDateTime.parse( "2025-05-27T18:55:45Z" );
        Mockito.when( timeService.getCurrentSystemDateTime() ).thenReturn( dateTime );

        // WHEN
        boolean result = underTest.validateAccessToken( TEST_ACCESS_TOKEN );

        // THEN
        assertFalse( result );
    }

    /**
     * Tests the behavior of the {@code validateAccessToken} method when provided with a {@code null} parameter.
     * <p>
     * This test ensures that a {@link NullPointerException} is thrown when the {@code validateAccessToken}
     * method is called with a {@code null} access token. This validation reflects the method's strict
     * requirement to handle only non-null inputs.
     * <p>
     * Test Steps:
     * 1. Set up the mocked {@code TimeService} to provide a specific system time.
     * 2. Call the {@code validateAccessToken} method with {@code null} as the parameter.
     * 3. Assert that a {@code NullPointerException} is thrown.
     */
    @Test
    void shouldThrowNullPointerException_whenNullGivenAsParameter() {

        // WHEN
        ZonedDateTime dateTime = ZonedDateTime.parse( "2025-05-27T18:55:45Z" );
        Mockito.when( timeService.getCurrentSystemDateTime() ).thenReturn( dateTime );

        // THEN
        assertThrows( NullPointerException.class, () -> underTest.validateAccessToken( null ) );
    }

}