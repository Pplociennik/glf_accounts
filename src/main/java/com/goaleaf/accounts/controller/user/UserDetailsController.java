package com.goaleaf.accounts.controller.user;

import com.github.pplociennik.commons.dto.ResponseDto;
import com.goaleaf.accounts.data.dto.user.UserDetailsDto;
import com.goaleaf.accounts.service.UserDetailsService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
