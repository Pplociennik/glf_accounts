package com.goaleaf.accounts.domain.session.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * A domain model representing the details of a user session.
 *
 * @author Created by: Pplociennik at 20.04.2026 20:17
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserSessionDetails {

    /**
     * The unique database identifier.
     */
    private UUID id;

    /**
     * The unique identifier of the session.
     */
    private String sessionId;

    /**
     * The refresh token associated with the session.
     */
    private String refreshToken;

    /**
     * The unique identifier of the authenticated user.
     */
    private String authenticatedUserId;

    /**
     * The location from which the session was initiated.
     */
    private String location;

    /**
     * The device from which the session was initiated.
     */
    private String device;
}
