package com.goaleaf.accounts.persistence.repository;

import com.goaleaf.accounts.persistence.entity.UserDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * A repository for {@link UserDetails} entity.
 *
 * @author Created by: Pplociennik at 14.03.2024 19:17
 */
@Repository
public interface UserDetailsRepository extends JpaRepository< UserDetails, UUID > {

    /**
     * Returns the optional {@link UserDetails} with the specified email address.
     *
     * @param aEmailAddress
     *         an email address of the user being searched.
     * @return the {@link Optional} containing the {@link UserDetails} typed object with the specified email address or the empty one otherwise.
     */
    Optional< UserDetails > findByEmailAddress( String aEmailAddress );

    /**
     * Returns the optional {@link UserDetails} with the specified identifier.
     *
     * @param aUserId
     *         an identifier of the user being searched.
     * @return the {@link Optional} containing the {@link UserDetails} typed object with the specified identifier or the empty one otherwise.
     */
    Optional< UserDetails > findByUserId( String aUserId );

    /**
     * Deletes the specified {@link UserDetails} from database.
     *
     * @param aUserDetails
     *         the user to be deleted.
     */
    @Override
    void delete( @NonNull UserDetails aUserDetails );

    void deleteUserDetailsByUserId( String userId );
}
