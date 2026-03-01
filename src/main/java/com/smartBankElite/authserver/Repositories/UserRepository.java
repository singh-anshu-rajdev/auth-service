package com.smartBankElite.authserver.Repositories;

import com.smartBankElite.authserver.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

@EnableJpaRepositories
public interface UserRepository extends JpaRepository<User,Long> {

    @Query("""
       SELECT u FROM User u
       WHERE (u.emailId = :value OR u.userName = :value)
       AND u.deletedFlag = false
       """)
    Optional<User> findActiveUserByEmailOrUsername(@Param("value") String value);
}
