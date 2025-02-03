package com.goaleaf.accounts.controller;

import com.github.pplociennik.commons.dto.ResponseDto;
import com.goaleaf.accounts.dto.UserDto;
import com.goaleaf.accounts.service.UserService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger( UserController.class );

    private UserService userService;

    @PostMapping( "/create" )
    ResponseEntity< ResponseDto > createUser( @RequestBody UserDto aUserDto ) {
        logger.debug( "Started creating a new account." );
        userService.createUser( aUserDto );
        return ResponseEntity
                .status( HttpStatus.CREATED )
                .body( new ResponseDto( "201", "User created successfully" ) );
    }

    @GetMapping( path = "/user" )
    ResponseEntity< UserDto > fetchUser( @RequestParam String aEmailAddress ) {
        UserDto fetchedUser = userService.getUserByEmailAddress( aEmailAddress );
        return ResponseEntity.status( HttpStatus.OK ).body( fetchedUser );
    }

    @PutMapping( "/update" )
    ResponseEntity< ResponseDto > updateUser( @RequestBody UserDto aUserDto ) {
        userService.updateUser( aUserDto );
        return ResponseEntity
                .status( HttpStatus.OK )
                .body( new ResponseDto( "200", "User updated successfully." ) );
    }

    @DeleteMapping( path = "/delete" )
    ResponseEntity< ResponseDto > deleteUser( @RequestParam String aIdentifier ) {
        userService.deleteUser( aIdentifier );
        return ResponseEntity
                .status( HttpStatus.OK )
                .body( new ResponseDto( "200", "User deleted successfully." ) );
    }
}
