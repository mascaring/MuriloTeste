//.../repository/specification/TaskSpecification.java
package br.com.riachuelo.challenge.to_do_list_api.repository.specification;

import br.com.riachuelo.challenge.to_do_list_api.model.Task;
import br.com.riachuelo.challenge.to_do_list_api.model.TaskStatus;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TaskSpecification {

    public static Specification<Task> titleContains(String title) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), "%" + title.toLowerCase() + "%");
    }

    public static Specification<Task> hasStatus(TaskStatus status) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("status"), status);
    }

    public static Specification<Task> createdAtAfter(LocalDate  date) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), date);
    }

    public static Specification<Task> dueDateBefore(LocalDate date) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.lessThanOrEqualTo(root.get("dueDate"), date);
    }

    public static Specification<Task> belongsToUser(String userEmail) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("user").get("email"), userEmail);
    }
}