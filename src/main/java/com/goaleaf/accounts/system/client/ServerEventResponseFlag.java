package com.goaleaf.accounts.system.client;

import com.github.pplociennik.commons.system.client.ServerEventFlag;

/**
 * Flags representing server-side events included in the response so the client can react to them accordingly.
 *
 * @author Created by: Pplociennik at 26.01.2026 21:05
 * @see ServerEventFlag
 * @since 1.0
 */
public enum ServerEventResponseFlag implements ServerEventFlag {

    /**
     * Indicates that the user's email address has not been verified yet.
     */
    USER_EMAIL_NOT_VERIFIED,

    /**
     * Indicates that the user's current session has been closed implicitly as a side effect of the request.
     */
    CURRENT_SESSION_CLOSED_BY_USER_IMPLICITLY,

    /**
     * Indicates that the user's email address has already been verified and no further verification is needed.
     */
    USER_EMAIL_ALREADY_VERIFIED,

    /**
     * Indicates that another session of the user has been closed as a result of the request.
     */
    OTHER_USER_SESSION_CLOSED_BY_USER,

    /**
     * Indicates that the user's current session has been closed explicitly by the user's request.
     */
    CURRENT_SESSION_CLOSED_BY_USER_EXPLICITLY
}
