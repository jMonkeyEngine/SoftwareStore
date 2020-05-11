package com.jayfella.website.database.repository;

import com.jayfella.website.database.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);
    Optional<User> findByUsernameIgnoreCase(String username);

    Iterable<User> findByUsernameContaining(String username);
    Iterable<User> findByUsernameLike(String username);

    Optional<User> findByName(String name);
    // Optional<User> findByEmail(String email);
    Optional<User> findByEmailIgnoreCase(String email);

    Optional<User> findByNameAndPassword(String name, String password);
    Optional<User> findByUsernameAndPassword(String name, String password);

    Iterable<User> findByRegisterDateGreaterThan(Date date);

    // get all staff
    List<User> findByAdministratorTrueOrModeratorTrue();

}
