package br.com.riachuelo.challenge.to_do_list_api.controller;

import br.com.riachuelo.challenge.to_do_list_api.dto.TaskMapper;
import br.com.riachuelo.challenge.to_do_list_api.dto.TaskRequestDTO;
import br.com.riachuelo.challenge.to_do_list_api.dto.TaskResponseDTO;
import br.com.riachuelo.challenge.to_do_list_api.model.Task;
import br.com.riachuelo.challenge.to_do_list_api.model.TaskStatus;
import br.com.riachuelo.challenge.to_do_list_api.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public List<TaskResponseDTO> findAll() {
        return taskService.findAllTasksByAuthenticatedUser();
    }

    @PostMapping
    public ResponseEntity<TaskResponseDTO> save(@Valid @RequestBody TaskRequestDTO taskRequestDTO) {
        Task createdTask = taskService.createTask(taskRequestDTO);
        return new ResponseEntity<>(TaskMapper.toDTO(createdTask), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> updateTask(@PathVariable Long id, @Valid @RequestBody TaskRequestDTO taskDetails) {
        Task updatedTask = taskService.updateTask(id, taskDetails);
        return ResponseEntity.ok(TaskMapper.toDTO(updatedTask));
    }

    @GetMapping("/search")
    public ResponseEntity<List<TaskResponseDTO>> searchTasks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate createdAt,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate  dueDate
    ) {
        List<TaskResponseDTO> tasks = taskService.searchTasks(title, status, createdAt, dueDate);
        return ResponseEntity.ok(tasks);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> getTaskById(@PathVariable Long id) {
        Task task = taskService.findTaskById(id);
        return ResponseEntity.ok(TaskMapper.toDTO(task));
    }
}