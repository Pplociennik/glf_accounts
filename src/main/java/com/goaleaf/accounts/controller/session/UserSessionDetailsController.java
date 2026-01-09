package com.goaleaf.accounts.controller.session;

import com.github.pplociennik.commons.dto.ResponseDto;
import com.goaleaf.accounts.data.dto.response.UserSessionResponseDto;
import com.goaleaf.accounts.service.UserSessionDetailsService;
import com.goaleaf.accounts.system.util.AccessTokenUtils;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * Controller class designed as a REST API for managing user session details.
 * <p>
 * This class handles requests related to retrieving user session data, offering endpoints
 * for querying and displaying session information. It interacts with the {@link UserSessionDetailsService}
 * to perform these operations in a secure and structured manner.
 * </p>
 *
 * <p>
 * Example use case: A client sends an HTTP GET request to fetch all existing sessions
 * associated with a specific user's access token.
 * </p>
 *
 * <p>
 * This controller is annotated with Spring's {@code @RestController} for RESTful APIs,
 * {@code @RequestMapping} to define request paths, Lombok's {@code @AllArgsConstructor} for
 * constructor injection, and {@code @Log4j2} for logging capabilities.
 * </p>
 *
 * @author Created by:
 * Pplociennik at 01.04.2025 21:40
 * @see UserSessionDetailsService
 */
@RestController
@RequestMapping( path = "/api/sessions" )
@AllArgsConstructor
@Log4j2
class UserSessionDetailsController {

    /**
     * Service responsible for handling operations related to user session details.
     * <p>
     * This variable is a final instance of the {@code UserSessionDetailsService}, which provides core functionality
     * for managing user sessions, including retrieving session information based on access tokens.
     * It is utilized within the {@code UserSessionDetailsController} and acts as the primary point of interaction
     * with the user session-related logic.
     */
    private final UserSessionDetailsService userSessionDetailsService;

    /**
     * Retrieves a list of all user sessions associated with the provided access token.
     *
     * @return a {@code ResponseEntity} containing a list of {@code UserSessionResponseDto} objects,
     * each representing details of a user session, and an HTTP status of ACCEPTED.
     */
    @SuppressWarnings( "unchecked" )
    @GetMapping( path = "/all" )
    ResponseEntity< ResponseDto< UserSessionResponseDto > > getUserSessions( @RequestAttribute( value = "USER_ACCESS_TOKEN_REFRESHED" ) @NonNull boolean aTokenRefreshed, @NonNull @RequestHeader( value = "User-Token" ) String aUserAccessToken ) {
        log.debug( "Getting user sessions for {}", aUserAccessToken );
        requireNonNull( aUserAccessToken, "User-Token" );
        List< UserSessionResponseDto > allUserSessionDetails = userSessionDetailsService.getAllUserSessionsInfo( aUserAccessToken );
        return ResponseEntity
                .status( HttpStatus.ACCEPTED )
                .body(
                        ResponseDto.< UserSessionResponseDto >builder()
                                .withStatusInfo( "200", "All user sessions retrieved successfully." )
                                .withUserAccessToken( aTokenRefreshed, aUserAccessToken, AccessTokenUtils.getExpiresIn( aUserAccessToken ) )
                                .withResponseData( allUserSessionDetails )
                                .build()
                );
    }
}
