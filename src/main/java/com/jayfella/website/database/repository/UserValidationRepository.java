package com.jayfella.website.database.repository;

import com.jayfella.website.database.entity.user.UserValidation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserValidationRepository extends JpaRepository<UserValidation, String> {

    Optional<UserValidation> findByUserId(long userId);

}
