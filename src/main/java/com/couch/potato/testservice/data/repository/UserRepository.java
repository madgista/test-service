package com.couch.potato.testservice.data.repository;

import com.couch.potato.testservice.data.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("select user from User user where lower(user.username) = lower(:username)")
    Optional<User> findByUsernameIgnoreCase(String username);

    @Modifying
    @Transactional
    @Query("update User user set user.active = true where user.username = :username")
    int activate(String username);

    @Modifying
    @Transactional
    @Query("update User user set user.active = false where user.username = :username")
    int deactivate(String username);

    @Transactional
    int deleteUserByUsernameIgnoreCase(String username);
}
