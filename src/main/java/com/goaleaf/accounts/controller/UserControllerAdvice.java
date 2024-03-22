package com.goaleaf.accounts.controller;

import com.github.pplociennik.commons.dto.ErrorResponseDto;
import com.github.pplociennik.commons.exc.GlobalExceptionHandler;
import com.github.pplociennik.commons.exc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.ZonedDateTime;

/**
 * A controller advice being the global exception handler for the {@link UserController}.
 *
 * @author Created by: Pplociennik at 20.03.2024 17:37
 */
@ControllerAdvice( assignableTypes = { UserController.class } )
class UserControllerAdvice extends GlobalExceptionHandler {

    /**
     * Handles the {@link ResourceNotFoundException}.
     *
     * @param aException
     *         the exception being thrown during the system work.
     * @param aWebRequest
     *         the web request which execution was interrupted by the exception.
     * @return {@link ErrorResponseDto}.
     */
    @ExceptionHandler( ResourceNotFoundException.class )
    public ResponseEntity< ErrorResponseDto > handleResourceNotFoundException( ResourceNotFoundException aException,
                                                                               WebRequest aWebRequest ) {
        ErrorResponseDto errorResponseDTO = new ErrorResponseDto(
                aWebRequest.getDescription( false ),
                HttpStatus.NOT_FOUND,
                aException.getMessage(),
                ZonedDateTime.now()
        );
        return new ResponseEntity<>( errorResponseDTO, HttpStatus.NOT_FOUND );
    }

}
