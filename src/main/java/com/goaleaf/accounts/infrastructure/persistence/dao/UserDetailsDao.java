package com.goaleaf.accounts.infrastructure.persistence.dao;

import com.goaleaf.accounts.infrastructure.persistence.entity.UserDetailsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * A repository for {@link UserDetailsEntity} entity.
 *
 * @author Created by: Pplociennik at 14.03.2024 19:17
 */
@Repository
public interface UserDetailsDao extends JpaRepository< UserDetailsEntity, UUID > {

    /**
     * Returns the optional {@link UserDetailsEntity} with the specified email address.
     *
     * @param aEmailAddress
     *         an email address of the user being searched.
     * @return the {@link Optional} containing the {@link UserDetailsEntity} typed object with the specified email address or the empty one otherwise.
     */
    Optional< UserDetailsEntity > findByEmailAddress( String aEmailAddress );

    /**
     * Returns the optional {@link UserDetailsEntity} with the specified identifier.
     *
     * @param aUserId
     *         an identifier of the user being searched.
     * @return the {@link Optional} containing the {@link UserDetailsEntity} typed object with the specified identifier or the empty one otherwise.
     */
    Optional< UserDetailsEntity > findByUserId( String aUserId );

    /**
     * Deletes the specified {@link UserDetailsEntity} from database.
     *
     * @param aUserDetailsEntity
     *         the user to be deleted.
     */
    @Override
    void delete( @NonNull UserDetailsEntity aUserDetailsEntity );

    void deleteUserDetailsByUserId( String userId );
}
