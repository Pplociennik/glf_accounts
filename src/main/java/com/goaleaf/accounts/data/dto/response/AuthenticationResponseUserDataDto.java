package com.goaleaf.accounts.data.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * Data of the user logged in.
 *
 * @author Created by: Pplociennik at 13.01.2026 20:10
 */
@AllArgsConstructor
@Getter
@Setter
public class AuthenticationResponseUserDataDto implements Serializable {

    /**
     * A username.
     */
    private String username;
}
