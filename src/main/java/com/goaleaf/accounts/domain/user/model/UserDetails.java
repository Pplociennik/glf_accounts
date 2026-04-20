package com.goaleaf.accounts.domain.user.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * A domain model representing user details.
 *
 * @author Created by: Pplociennik at 20.04.2026 20:17
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDetails {

    /**
     * The unique database identifier.
     */
    private UUID id;

    /**
     * The unique identifier of the user within the identity provider.
     */
    private String userId;

    /**
     * The username.
     */
    private String userName;

    /**
     * The email address of the user.
     */
    private String emailAddress;

    /**
     * The description of the user.
     */
    private String description;
}
