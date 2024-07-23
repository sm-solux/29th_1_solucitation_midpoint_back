package com.solucitation.midpoint_backend.domain.favorites.repository;

import com.solucitation.midpoint_backend.domain.favorites.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
