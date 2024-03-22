package com.goaleaf.accounts.map;

import com.goaleaf.accounts.dto.UserDto;
import com.goaleaf.accounts.entity.User;

/**
 * A mapper for the {@link com.goaleaf.accounts.entity.User} entity object.
 *
 * @author Created by: Pplociennik at 14.03.2024 19:07
 */
public class UserMapper {

    /**
     * Returns an object mapped to the {@link UserDto} type.
     *
     * @param aUser
     *         an entity to be mapped.
     * @return an object of the type {@link UserDto} or null if th parameter is null.
     */
    public static UserDto mapToDto( User aUser ) {
        return aUser == null
                ? null
                : new UserDto( aUser.getUserId(), aUser.getUserName(), aUser.getEmailAddress(), aUser.getDescription() );
    }

    /**
     * Returns an object mapped to the {@link User} type.
     *
     * @param aUserDto
     *         a dto to be mapped.
     * @return an object of the type {@link User} or null if th parameter is null.
     */
    public static User mapToEntity( UserDto aUserDto ) {
        return aUserDto == null
                ? null
                : new User( aUserDto.getUserId(), aUserDto.getUserName(), aUserDto.getEmailAddress(), aUserDto.getDescription() );
    }
}
