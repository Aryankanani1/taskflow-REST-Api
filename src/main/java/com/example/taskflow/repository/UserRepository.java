package com.example.taskflow.repository;

import com.example.taskflow.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    // email is the unique identity — safe to return a single Optional / check existence
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    // username is NOT unique -> may match many rows, so it returns a List.
    // (An Optional<User> query here would throw when two users share a name.)
    List<User> findByUsername(String username);

}
