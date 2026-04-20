package com.goaleaf.accounts.domain.session.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * A domain model representing the publicly visible information about a user session.
 *
 * @author Created by: Pplociennik at 20.04.2026 20:17
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserSessionInfo {

    /**
     * The unique identifier of the session.
     */
    private String id;

    /**
     * The IP address from which the session was initiated.
     */
    private String ipAddress;

    /**
     * The timestamp in milliseconds marking the start of the session.
     */
    private Long start;

    /**
     * The timestamp in milliseconds of the last access within the session.
     */
    private Long lastAccess;

    /**
     * The approximate geographical location of the user during the session.
     */
    private String location;

    /**
     * The device used during the session.
     */
    private String device;
}
