package com.goaleaf.accounts.infrastructure.session.adapter;

import com.goaleaf.accounts.domain.session.model.UserSessionDetails;
import com.goaleaf.accounts.domain.session.port.UserSessionDetailsRepository;
import com.goaleaf.accounts.infrastructure.persistence.dao.UserSessionDetailsDao;
import com.goaleaf.accounts.infrastructure.persistence.entity.UserSessionDetailsEntity;
import lombok.AllArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * An adapter implementing the {@link UserSessionDetailsRepository} port by delegating to {@link UserSessionDetailsDao}.
 *
 * @author Created by: Pplociennik at 20.04.2026 20:20
 */
@Service
@AllArgsConstructor
public class UserSessionDetailsRepositoryImpl implements UserSessionDetailsRepository {

    /**
     * The DAO used for performing persistence operations on {@link UserSessionDetailsEntity} objects.
     */
    private final UserSessionDetailsDao userSessionDetailsDao;

    /**
     * Returns a list of {@link UserSessionDetails} associated with the specified authenticated user identifier.
     *
     * @param aAuthenticatedUserId
     *         the identifier of the authenticated user.
     * @return a list of {@link UserSessionDetails} for the given user, or an empty list if none found.
     */
    @Override
    public List<UserSessionDetails> findByAuthenticatedUserId( @NonNull String aAuthenticatedUserId ) {
        return userSessionDetailsDao.findByAuthenticatedUserId( aAuthenticatedUserId )
                .stream()
                .map( UserSessionDetailsPersistenceMapper::mapToDomain )
                .collect( Collectors.toList() );
    }

    /**
     * Returns the optional {@link UserSessionDetails} with the specified session identifier.
     *
     * @param aSessionId
     *         the unique identifier of the session.
     * @return the {@link Optional} containing the {@link UserSessionDetails} or the empty one if not found.
     */
    @Override
    public Optional<UserSessionDetails> findBySessionId( @NonNull String aSessionId ) {
        return userSessionDetailsDao.findBySessionId( aSessionId )
                .map( UserSessionDetailsPersistenceMapper::mapToDomain );
    }

    /**
     * Saves the specified {@link UserSessionDetails} and returns the persisted result.
     * For new entities (no id set), creation metadata is populated automatically.
     * For existing entities, the current database record is loaded and only the refresh token is updated.
     *
     * @param aUserSessionDetails
     *         the session details to be saved.
     * @return the saved {@link UserSessionDetails}.
     */
    @Override
    public UserSessionDetails save( @NonNull UserSessionDetails aUserSessionDetails ) {
        UserSessionDetailsEntity entity;

        if ( aUserSessionDetails.getId() == null ) {
            entity = UserSessionDetailsEntity.builder()
                    .sessionId( aUserSessionDetails.getSessionId() )
                    .refreshToken( aUserSessionDetails.getRefreshToken() )
                    .authenticatedUserId( aUserSessionDetails.getAuthenticatedUserId() )
                    .location( aUserSessionDetails.getLocation() )
                    .device( aUserSessionDetails.getDevice() )
                    .createdAt( Instant.now() )
                    .createdBy( "SYSTEM" )
                    .build();
        } else {
            UserSessionDetailsEntity existing = userSessionDetailsDao.findById( aUserSessionDetails.getId() )
                    .orElseThrow( () -> new IllegalStateException( "UserSessionDetails not found: " + aUserSessionDetails.getId() ) );
            existing.setRefreshToken( aUserSessionDetails.getRefreshToken() );
            entity = existing;
        }

        return UserSessionDetailsPersistenceMapper.mapToDomain( userSessionDetailsDao.save( entity ) );
    }

    /**
     * Deletes the specified {@link UserSessionDetails}.
     *
     * @param aUserSessionDetails
     *         the session details to be deleted.
     */
    @Override
    public void delete( @NonNull UserSessionDetails aUserSessionDetails ) {
        userSessionDetailsDao.findBySessionId( aUserSessionDetails.getSessionId() )
                .ifPresent( userSessionDetailsDao::delete );
    }
}
