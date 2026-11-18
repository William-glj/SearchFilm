package com.checkticket.backend.repository;

import com.checkticket.backend.entity.Role;
import com.checkticket.backend.entity.User;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByRole(Role role);
}
