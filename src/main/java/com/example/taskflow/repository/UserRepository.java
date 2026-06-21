package com.example.taskflow.repository;

import com.example.taskflow.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    Optional<User> findByUsername(String name);
    Optional<User> findByEmail(String email);
    boolean existsByUserName(String username);
    boolean existsByEmail(String email);


}
