package com.goaleaf.accounts.system.client;

import com.github.pplociennik.commons.system.client.ClientActionFlag;

/**
 * Values being the action flags determining action to be executed on the client's side after receiving the response.
 *
 * @author Created by: Pplociennik at 26.01.2026 21:05
 */
public enum AuthClientActionFlags implements ClientActionFlag {

    /**
     * Represents a client action flag that specifies the action of verifying a user's email.
     * This action is typically triggered after the client receives a response instructing
     * it to verify the email address associated with the user account.
     */
    VERIFY_USER_EMAIL,

    /**
     * Represents a client action flag that specifies the action of clearing the user's session.
     * This action is typically used to log out the user or reset the session data on the client side
     * after receiving a corresponding response from the server.
     */
    CLEAR_USER_SESSION_DATA,
}
