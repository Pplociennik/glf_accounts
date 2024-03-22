package com.goaleaf.accounts.repository;

import com.goaleaf.accounts.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * A repository for {@link User} entity.
 *
 * @author Created by: Pplociennik at 14.03.2024 19:17
 */
@Repository
public interface UserRepository extends JpaRepository< User, Long > {

    Optional< User > findByEmailAddress( String aEmailAddress );

}
