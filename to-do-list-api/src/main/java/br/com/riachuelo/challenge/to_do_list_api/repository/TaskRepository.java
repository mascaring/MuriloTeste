package br.com.riachuelo.challenge.to_do_list_api.repository;

import br.com.riachuelo.challenge.to_do_list_api.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByUserEmail(String userEmail);
}
