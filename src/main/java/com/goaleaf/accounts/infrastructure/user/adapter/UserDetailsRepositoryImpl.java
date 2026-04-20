package com.goaleaf.accounts.infrastructure.user.adapter;

import com.goaleaf.accounts.domain.user.model.UserDetails;
import com.goaleaf.accounts.domain.user.port.UserDetailsRepository;
import com.goaleaf.accounts.infrastructure.persistence.dao.UserDetailsDao;
import com.goaleaf.accounts.infrastructure.persistence.entity.UserDetailsEntity;
import lombok.AllArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * An adapter implementing the {@link UserDetailsRepository} port by delegating to {@link UserDetailsDao}.
 *
 * @author Created by: Pplociennik at 20.04.2026 20:20
 */
@Service
@AllArgsConstructor
public class UserDetailsRepositoryImpl implements UserDetailsRepository {

    /**
     * The DAO used for performing persistence operations on {@link UserDetailsEntity} objects.
     */
    private final UserDetailsDao userDetailsDao;

    /**
     * Returns the optional {@link UserDetails} with the specified email address.
     *
     * @param aEmailAddress
     *         an email address of the user being searched.
     * @return the {@link Optional} containing the {@link UserDetails} with the specified email address or the empty one otherwise.
     */
    @Override
    public Optional<UserDetails> findByEmailAddress( String aEmailAddress ) {
        return userDetailsDao.findByEmailAddress( aEmailAddress )
                .map( UserDetailsRepositoryImpl::mapToDomain );
    }

    /**
     * Returns the optional {@link UserDetails} with the specified identifier.
     *
     * @param aUserId
     *         an identifier of the user being searched.
     * @return the {@link Optional} containing the {@link UserDetails} with the specified identifier or the empty one otherwise.
     */
    @Override
    public Optional<UserDetails> findByUserId( String aUserId ) {
        return userDetailsDao.findByUserId( aUserId )
                .map( UserDetailsRepositoryImpl::mapToDomain );
    }

    /**
     * Saves the specified {@link UserDetails} and returns the persisted result.
     *
     * @param aUserDetails
     *         the user details to be saved.
     * @return the saved {@link UserDetails}.
     */
    @Override
    public UserDetails save( @NonNull UserDetails aUserDetails ) {
        UserDetailsEntity entity = mapToEntity( aUserDetails );
        UserDetailsEntity saved = userDetailsDao.save( entity );
        return mapToDomain( saved );
    }

    /**
     * Deletes the specified {@link UserDetails}.
     *
     * @param aUserDetails
     *         the user details to be deleted.
     */
    @Override
    public void delete( @NonNull UserDetails aUserDetails ) {
        userDetailsDao.delete( mapToEntity( aUserDetails ) );
    }

    /**
     * Deletes the user details associated with the specified user identifier.
     *
     * @param aUserId
     *         the identifier of the user whose details are to be deleted.
     */
    @Override
    public void deleteByUserId( String aUserId ) {
        userDetailsDao.deleteUserDetailsByUserId( aUserId );
    }

    private static UserDetails mapToDomain( UserDetailsEntity aEntity ) {
        if ( aEntity == null ) {
            return null;
        }
        return UserDetails.builder()
                .id( aEntity.getId() )
                .userId( aEntity.getUserId() )
                .userName( aEntity.getUserName() )
                .emailAddress( aEntity.getEmailAddress() )
                .description( aEntity.getDescription() )
                .build();
    }

    private static UserDetailsEntity mapToEntity( UserDetails aUserDetails ) {
        if ( aUserDetails == null ) {
            return null;
        }
        return UserDetailsEntity.builder()
                .id( aUserDetails.getId() )
                .userId( aUserDetails.getUserId() )
                .userName( aUserDetails.getUserName() )
                .emailAddress( aUserDetails.getEmailAddress() )
                .description( aUserDetails.getDescription() )
                .build();
    }
}
