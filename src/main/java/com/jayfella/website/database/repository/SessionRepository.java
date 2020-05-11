package com.jayfella.website.database.repository;

import com.jayfella.website.database.entity.user.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SessionRepository extends JpaRepository<UserSession, Long> {

    Optional<UserSession> findBySession(String session);

}
