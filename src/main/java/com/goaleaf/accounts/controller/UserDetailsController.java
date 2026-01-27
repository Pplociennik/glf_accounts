package com.goaleaf.accounts.controller;

import com.goaleaf.accounts.service.UserDetailsService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static java.util.Objects.requireNonNull;

/**
 * TODO: Describe this class.
 *
 * @author Created by: Pplociennik at 11.08.2025 17:34
 */
@RestController
@RequestMapping( path = "/api/users" )
@AllArgsConstructor
@Log4j2
class UserDetailsController {

    private UserDetailsService userDetailsService;

}
