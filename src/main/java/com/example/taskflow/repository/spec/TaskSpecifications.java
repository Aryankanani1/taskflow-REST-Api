package com.example.taskflow.repository.spec;

import com.example.taskflow.entity.Task;
import com.example.taskflow.entity.User;
import com.example.taskflow.enums.Priority;
import com.example.taskflow.enums.TaskStatus;
import org.springframework.data.jpa.domain.Specification;

/**
 * Composable predicates for querying tasks. Each returns a {@link Specification}
 * that constrains one dimension; the service ANDs together only the ones the
 * client actually supplied. This replaces the 2^n derived-query branch explosion
 * (see TaskService.getAll) so a new optional filter is one method, not a doubling
 * of if-branches.
 */
public final class TaskSpecifications {

    private TaskSpecifications() {
    }

    public static Specification<Task> ownedBy(User user) {
        return (root, query, cb) -> cb.equal(root.get("user"), user);
    }

    public static Specification<Task> hasStatus(TaskStatus status) {
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }

    public static Specification<Task> hasPriority(Priority priority) {
        return (root, query, cb) -> cb.equal(root.get("priority"), priority);
    }

    public static Specification<Task> inCategory(Long categoryId) {
        return (root, query, cb) -> cb.equal(root.get("category").get("id"), categoryId);
    }

    // Case-insensitive substring match on the title. Escapes LIKE wildcards so a
    // literal % or _ in the search term matches itself instead of acting as a pattern.
    public static Specification<Task> titleContains(String term) {
        String escaped = term.toLowerCase()
                .replace("!", "!!")
                .replace("%", "!%")
                .replace("_", "!_");
        String pattern = "%" + escaped + "%";
        return (root, query, cb) -> cb.like(cb.lower(root.get("title")), pattern, '!');
    }
}
