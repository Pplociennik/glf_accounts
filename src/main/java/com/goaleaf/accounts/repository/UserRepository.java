package com.goaleaf.accounts.repository;

import com.goaleaf.accounts.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * A repository for {@link User} entity.
 *
 * @author Created by: Pplociennik at 14.03.2024 19:17
 */
@Repository
public interface UserRepository extends JpaRepository< User, Long > {

    /**
     * Returns the optional {@link User} with the specified email address.
     *
     * @param aEmailAddress
     *         an email address of the user being searched.
     * @return the {@link Optional} containing the {@link User} typed object with the specified email address or the empty one otherwise.
     */
    Optional< User > findByEmailAddress( String aEmailAddress );

    /**
     * Returns the optional {@link User} with the specified identifier.
     *
     * @param aUserId
     *         an identifier of the user being searched.
     * @return the {@link Optional} containing the {@link User} typed object with the specified identifier or the empty one otherwise.
     */
    Optional< User > findByUserId( String aUserId );

    /**
     * Deletes the specified {@link User} from database.
     *
     * @param aUser
     *         the user to be deleted.
     */
    @Override
    void delete( @NonNull User aUser );
}
