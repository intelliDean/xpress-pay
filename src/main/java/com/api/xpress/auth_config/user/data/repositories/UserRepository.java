package com.api.xpress.auth_config.user.data.repositories;


import com.api.xpress.auth_config.user.data.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findUserByEmailAddress(String emailAddress);

    boolean existsByEmailAddress(String email);
}
