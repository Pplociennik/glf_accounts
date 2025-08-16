package com.goaleaf.accounts.controller;

import com.github.pplociennik.commons.dto.ErrorResponseDto;
import com.github.pplociennik.commons.exc.GlobalExceptionHandler;
import com.github.pplociennik.commons.exc.validation.ValidationException;
import com.github.pplociennik.commons.service.TimeService;
import com.goaleaf.accounts.system.exc.auth.AuthenticationFailedException;
import com.goaleaf.accounts.system.exc.auth.RegistrationFailedException;
import com.goaleaf.accounts.system.exc.request.KeycloakActionRequestFailedException;
import com.goaleaf.accounts.system.exc.request.KeycloakResourceRequestFailedException;
import com.goaleaf.accounts.system.exc.request.TokenRefreshFailedException;
import jakarta.ws.rs.Produces;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;

/**
 * Provides centralized exception handling.
 * <p>
 * This class is annotated with {@link ControllerAdvice} to intercept and handle exceptions thrown by the methods
 * within the {@code AuthenticationController}. It extends the {@link GlobalExceptionHandler} for consistent
 * exception handling throughout the application and customizes exception responses specific to authentication
 * workflows.
 * </p>
 * <p>
 * Example: If a {@link RegistrationFailedException} is thrown during user registration, this advice converts
 * it into an {@link ErrorResponseDto} with meaningful details about the error.
 * </p>
 *
 * <p><b>Created by:</b> Pplociennik<br>
 * <b>Date:</b> 24.03.2025 22:05</p>
 */
@ControllerAdvice
@AllArgsConstructor
@Log4j2
class GlobalControllerAdvice extends GlobalExceptionHandler {

    /**
     * A time service for system time management.
     */
    private final TimeService timeService;

    /**
     * Handles exceptions of type {@link RegistrationFailedException} during authentication processes.
     * <p>
     * This method captures {@link RegistrationFailedException} instances thrown by the application, extracts error
     * details, and encapsulates them in an {@link ErrorResponseDto}. The response contains critical details such as
     * the problematic request, the HTTP status code, a human-friendly error message, and the timestamp of the occurrence.
     * </p>
     *
     * @param aException
     *         The exception representing an error that occurred during user registration. It contains details
     *         about the cause of failure, such as invalid input or issues with the external authentication service.
     * @param aWebRequest
     *         The web request object representing the request during which the exception occurred. It provides
     *         extra context about the request and helps in diagnosing issues.
     * @return A {@link ResponseEntity} containing the {@link ErrorResponseDto} and an HTTP status of 500 (Internal Server Error).
     */
    @ExceptionHandler( RegistrationFailedException.class )
    @ResponseBody
    @Produces( MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity< ErrorResponseDto > handleRegistrationFailedException( RegistrationFailedException aException,
                                                                                 WebRequest aWebRequest ) {
        ErrorResponseDto errorResponseDTO = new ErrorResponseDto(
                aWebRequest.getDescription( false ),
                HttpStatus.INTERNAL_SERVER_ERROR,
                aException.getLocalizedMessage(),
                timeService.getCurrentSystemDateTime()
        );

        log.error( aException.getLocalizedMessage(), aException );

        return new ResponseEntity<>( errorResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR );
    }

    /**
     * Handles exceptions of type {@link AuthenticationFailedException} during authentication processes.
     * <p>
     * This method captures {@link AuthenticationFailedException} instances that occur in the application and constructs
     * an {@link ErrorResponseDto} containing details about the error. The response includes the web request description,
     * a localized error message, the HTTP status code, and the timestamp of the error occurrence.
     *
     * @param aException
     *         The exception representing an error that occurred during the authentication process.
     *         It provides details about the cause of failure, such as invalid input or issues related
     *         to authentication mechanisms.
     * @param aWebRequest
     *         The web request associated with the occurrence of the exception. It contains contextual
     *         information about the request, used to diagnose and describe the error.
     * @return A {@link ResponseEntity} containing an {@link ErrorResponseDto} with detailed information
     * about the error and an HTTP status of 500 (Internal Server Error).
     */
    @ExceptionHandler( AuthenticationFailedException.class )
    public ResponseEntity< ErrorResponseDto > handleAuthenticationFailedException( AuthenticationFailedException aException,
                                                                                   WebRequest aWebRequest ) {
        ErrorResponseDto errorResponseDTO = new ErrorResponseDto(
                aWebRequest.getDescription( false ),
                HttpStatus.INTERNAL_SERVER_ERROR,
                aException.getLocalizedMessage(),
                timeService.getCurrentSystemDateTime()
        );

        log.error( aException.getLocalizedMessage(), aException );

        return new ResponseEntity<>( errorResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR );
    }

    /**
     * Handles exceptions of type {@code ValidationException} occurring during the registration process.
     * <p>
     * This method intercepts {@code ValidationException} instances, extracts relevant details about the
     * error, and constructs an {@code ErrorResponseDto} containing the description of the failed request,
     * the HTTP status code, a user-friendly localized error message, and the timestamp of the error occurrence.
     * <p>
     * The exception details are logged for diagnostic purposes.
     *
     * @param aException
     *         the {@link ValidationException} representing the validation error that occurred during the
     *         registration process. It provides specifics about the cause of failure, such as invalid input
     *         or other validation issues.
     * @param aWebRequest
     *         the {@link WebRequest} associated with the request during which the exception occurred.
     *         It provides additional context about the request to aid in debugging and error reporting.
     * @return a {@link ResponseEntity} containing an {@link ErrorResponseDto} with the error details
     * and an HTTP status code of {@code BAD_REQUEST (400)}.
     */
    @ExceptionHandler( ValidationException.class )
    public ResponseEntity< ErrorResponseDto > handleValidationException( ValidationException aException,
                                                                         WebRequest aWebRequest ) {
        ErrorResponseDto errorResponseDTO = new ErrorResponseDto(
                aWebRequest.getDescription( false ),
                HttpStatus.BAD_REQUEST,
                aException.getLocalizedMessage(),
                timeService.getCurrentSystemDateTime()
        );

        log.error( aException.getLocalizedMessage(), aException );

        return new ResponseEntity<>( errorResponseDTO, HttpStatus.BAD_REQUEST );
    }

    /**
     * Handles exceptions of type {@link KeycloakActionRequestFailedException} during registration processes.
     * <p>
     * This method is invoked when a {@code KeycloakActionRequestFailedException} is thrown, usually indicating
     * an issue with Keycloak while processing an action request. It extracts details about the error,
     * logs the exception for diagnostic purposes, and constructs a standardized {@link ErrorResponseDto} to be
     * returned in the response. The response contains the request description, an HTTP status code of
     * {@code INTERNAL_SERVER_ERROR (500)}, a localized error message, and a timestamp of the occurrence.
     *
     * @param aException
     *         The exception representing a failure during the registration process related to Keycloak.
     *         This exception typically contains information about the root cause of the failure.
     * @param aWebRequest
     *         The web request during which the exception occurred, providing context about the failed request
     *         that helps in diagnosing and resolving the issue.
     * @return A {@link ResponseEntity} containing an {@link ErrorResponseDto} with the error details and an
     * HTTP status code of {@code INTERNAL_SERVER_ERROR (500)}.
     */
    @ExceptionHandler( KeycloakActionRequestFailedException.class )
    public ResponseEntity< ErrorResponseDto > handleKeycloakActionRequestFailedException( KeycloakActionRequestFailedException aException,
                                                                                          WebRequest aWebRequest ) {
        ErrorResponseDto errorResponseDTO = new ErrorResponseDto(
                aWebRequest.getDescription( false ),
                HttpStatus.INTERNAL_SERVER_ERROR,
                aException.getLocalizedMessage(),
                timeService.getCurrentSystemDateTime()
        );

        log.error( aException.getLocalizedMessage(), aException );

        return new ResponseEntity<>( errorResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR );
    }

    /**
     * Handles exceptions of type {@link KeycloakResourceRequestFailedException} during Keycloak resource requests.
     * <p>
     * This method captures {@link KeycloakResourceRequestFailedException} instances thrown during the processing
     * of requests related to Keycloak resources. It logs the exception and constructs an {@link ErrorResponseDto}
     * containing the details of the error, including the web request description, a localized error message,
     * the HTTP status code, and the timestamp of the occurrence. The constructed response is returned with an
     * HTTP status of 500 (Internal Server Error).
     *
     * @param aException
     *         The exception representing a failure that occurred during a Keycloak resource request.
     *         It carries detailed information about the root cause of the failure.
     * @param aWebRequest
     *         The web request that was active when the exception occurred, providing context about the request,
     *         aiding in debugging and reporting the error.
     * @return A {@link ResponseEntity} containing the {@link ErrorResponseDto} with details about the error
     * and an HTTP status code of 500 (Internal Server Error).
     */
    @ExceptionHandler( KeycloakResourceRequestFailedException.class )
    public ResponseEntity< ErrorResponseDto > handleKeycloakResourceRequestFailedException( KeycloakResourceRequestFailedException aException,
                                                                                            WebRequest aWebRequest ) {
        ErrorResponseDto errorResponseDTO = new ErrorResponseDto(
                aWebRequest.getDescription( false ),
                HttpStatus.INTERNAL_SERVER_ERROR,
                aException.getLocalizedMessage(),
                timeService.getCurrentSystemDateTime()
        );

        log.error( aException.getLocalizedMessage(), aException );

        return new ResponseEntity<>( errorResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR );
    }

    /**
     * Handles exceptions of type {@link TokenRefreshFailedException} occurring during the token refresh process.
     * <p>
     * This method captures {@link TokenRefreshFailedException} instances, extracts the necessary details, logs the error,
     * and constructs an {@link ErrorResponseDto} with the appropriate information. The response contains the request description,
     * a localized error message, the HTTP status code, and a timestamp indicating when the error occurred.
     * The response is returned to the client with an HTTP status of 500 (Internal Server Error).
     *
     * @param aException
     *         The exception representing the failure that occurred during the token refresh process. It provides
     *         information about the nature of the issue that prevented the token from being refreshed successfully.
     * @param aWebRequest
     *         The web request associated with the occurrence of the exception. This includes details about the
     *         HTTP request at the time of failure, which aids in diagnosing and debugging the issue.
     * @return A {@link ResponseEntity} containing an {@link ErrorResponseDto} with detailed error information and an
     * HTTP status code of 500 (Internal Server Error).
     */
    @ExceptionHandler( TokenRefreshFailedException.class )
    public ResponseEntity< ErrorResponseDto > handleTokenRefreshFailedException( TokenRefreshFailedException aException,
                                                                                 WebRequest aWebRequest ) {
        ErrorResponseDto errorResponseDTO = new ErrorResponseDto(
                aWebRequest.getDescription( false ),
                HttpStatus.INTERNAL_SERVER_ERROR,
                aException.getLocalizedMessage(),
                timeService.getCurrentSystemDateTime()
        );

        log.error( aException.getLocalizedMessage(), aException );

        return new ResponseEntity<>( errorResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR );
    }

    // #################################################################################################################

    private String getValidationLocalizedMessageParameter( ValidationException aException ) {
        Throwable suppressedException = aException.getSuppressed()[ 0 ];
        return suppressedException.getLocalizedMessage();
    }
}
