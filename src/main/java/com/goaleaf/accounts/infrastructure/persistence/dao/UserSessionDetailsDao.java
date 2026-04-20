package com.goaleaf.accounts.infrastructure.persistence.dao;

import com.goaleaf.accounts.infrastructure.persistence.entity.UserSessionDetailsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * A repository interface for managing user session details entities in the persistence layer.
 * Provides CRUD operations and additional queries specific to user session details.
 *
 * @author Created by: Pplociennik at 01.04.2025 20:33
 * @see JpaRepository
 * @see UserSessionDetailsEntity
 */
public interface UserSessionDetailsDao extends JpaRepository< UserSessionDetailsEntity, UUID > {

    /**
     * Retrieves a list of user session details associated with the given authenticated user ID.
     *
     * @param authenticatedUserId
     *         the identifier of the authenticated user whose active session details are to be retrieved.
     *         This parameter must not be null.
     * @return a list of {@code UserSessionDetails} representing active sessions for the specified authenticated user ID.
     * If no active sessions are found, the returned list will be empty.
     */
    List< UserSessionDetailsEntity > findByAuthenticatedUserId( @NonNull String authenticatedUserId );

    /**
     * Retrieves the details of a user session based on the provided session ID.
     *
     * @param aSessionId
     *         the unique identifier of the session to be retrieved. This parameter must not be null.
     * @return an {@code Optional} containing the {@code UserSessionDetails} if a session with the given ID exists,
     * or an empty {@code Optional} if no such session is found.
     */
    Optional< UserSessionDetailsEntity > findBySessionId( @NonNull String aSessionId );


}
