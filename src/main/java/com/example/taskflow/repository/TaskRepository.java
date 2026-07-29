package com.example.taskflow.repository;

import com.example.taskflow.entity.Task;
import com.example.taskflow.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task,Long>, JpaSpecificationExecutor<Task> {

    Optional<Task> findByIdAndUser(Long id, User user);

    // Filtering/searching for the task list is composed from TaskSpecifications and run
    // via JpaSpecificationExecutor.findAll(spec, pageable) — see TaskService.getAll.
}
