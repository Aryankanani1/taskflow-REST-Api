package com.example.taskflow.repository;

import com.example.taskflow.entity.Task;
import com.example.taskflow.entity.User;
import com.example.taskflow.enums.Priority;
import com.example.taskflow.enums.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task,Long> {

    List<Task> findByUser(User user);

    Page<Task> findByUser(User user, Pageable pageable);

    Optional<Task> findByIdAndUser(Long id, User user);

    Page<Task> findByUserAndStatus(User user, TaskStatus taskStatus, Pageable pageable);

    Page<Task> findByUserAndPriority(User user, Priority priority, Pageable pageable);

    Page<Task> findByUserAndTitleContainingIgnoreCase(User user, String title, Pageable pageable);

    long countByUserAndStatus(User user, TaskStatus status);

    long countByUserAndPriority(User user, Priority priority);
}
