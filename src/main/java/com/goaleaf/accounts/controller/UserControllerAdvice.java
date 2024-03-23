package com.goaleaf.accounts.controller;

import com.github.pplociennik.commons.exc.GlobalExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;

/**
 * A controller advice being the global exception handler for the {@link UserController}.
 *
 * @author Created by: Pplociennik at 20.03.2024 17:37
 */
@ControllerAdvice( assignableTypes = { UserController.class } )
class UserControllerAdvice extends GlobalExceptionHandler {

}
