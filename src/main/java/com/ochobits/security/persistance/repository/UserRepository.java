package com.ochobits.security.persistance.repository;

import com.ochobits.security.persistance.entity.PermissionEntity;
import com.ochobits.security.persistance.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findUserEntityByUsername(String username);

}
