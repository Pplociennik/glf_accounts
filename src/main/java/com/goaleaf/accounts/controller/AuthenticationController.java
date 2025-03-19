package com.goaleaf.accounts.controller;

import com.goaleaf.accounts.service.AuthenticationService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * A controller being a REST API for authentication operations.
 *
 * @author Created by: Pplociennik at 19.03.2025 18:58
 */
@RestController
@RequestMapping( path = "/api/auth" )
@AllArgsConstructor
class AuthenticationController {

    private AuthenticationService authenticationService;


}
