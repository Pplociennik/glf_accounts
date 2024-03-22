package com.goaleaf.accounts.controller;

import com.github.pplociennik.commons.dto.ResponseDto;
import com.goaleaf.accounts.dto.UserDto;
import com.goaleaf.accounts.service.UserService;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * A controller being the API for managing users.
 *
 * @author Created by: Pplociennik at 19.03.2024 18:00
 */
@RestController
@RequestMapping( path = "/api/users" )
@AllArgsConstructor
class UserController {

    private UserService userService;

    @PostMapping( "/create" )
    ResponseEntity< ResponseDto > createUser( @RequestBody UserDto aUserDto ) {
        userService.createUser( aUserDto );
        return ResponseEntity
                .status( HttpStatus.CREATED )
                .body( new ResponseDto( "201", "User created successfully" ) );
    }

    @GetMapping( path = "/fetch" )
    ResponseEntity< UserDto > fetchUser( @RequestParam String aEmailAddress ) {
        UserDto fetchedUser = userService.fetchUser( aEmailAddress );
        return ResponseEntity.status( HttpStatus.OK ).body( fetchedUser );
    }
}
