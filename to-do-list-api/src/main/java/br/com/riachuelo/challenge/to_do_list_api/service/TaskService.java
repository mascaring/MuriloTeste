package br.com.riachuelo.challenge.to_do_list_api.service;

import br.com.riachuelo.challenge.to_do_list_api.dto.TaskMapper;
import br.com.riachuelo.challenge.to_do_list_api.dto.TaskRequestDTO;
import br.com.riachuelo.challenge.to_do_list_api.dto.TaskResponseDTO;
import br.com.riachuelo.challenge.to_do_list_api.model.Task;
import br.com.riachuelo.challenge.to_do_list_api.model.TaskStatus;
import br.com.riachuelo.challenge.to_do_list_api.model.User;
import br.com.riachuelo.challenge.to_do_list_api.repository.TaskRepository;
import br.com.riachuelo.challenge.to_do_list_api.repository.UserRepository;
import br.com.riachuelo.challenge.to_do_list_api.repository.specification.TaskSpecification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import br.com.riachuelo.challenge.to_do_list_api.repository.specification.TaskSpecification;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskService(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Task createTask(TaskRequestDTO taskRequestDTO) {
        String userEmail = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Usuário autenticado não encontrado no banco de dados"));

        Task newTask = new Task();
        newTask.setTitle(taskRequestDTO.getTitle());
        newTask.setDescription(taskRequestDTO.getDescription());
        newTask.setStatus(taskRequestDTO.getStatus());
        newTask.setDueDate(taskRequestDTO.getDueDate());

        newTask.setUser(user);

        return taskRepository.save(newTask);
    }

    @Transactional(readOnly = true)
    public List<TaskResponseDTO> findAllTasksByAuthenticatedUser() {
        String userEmail = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();

        log.info("Buscando todas as tarefas para o usuário: {}", userEmail);

        return taskRepository.findByUserEmail(userEmail)
                .stream()
                .map(TaskMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TaskResponseDTO> searchTasks(String title, TaskStatus status, LocalDate createdAt, LocalDate  dueDate) {
        String userEmail = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();

        log.info("Iniciando busca de tarefas para o usuário: {}", userEmail);

        Specification<Task> spec = Specification.where(TaskSpecification.belongsToUser(userEmail));

        if (title!= null &&!title.isBlank()) {
            spec = spec.and(TaskSpecification.titleContains(title));
            log.info("Adicionando filtro por título: {}", title);
        }
        if (status!= null) {
            spec = spec.and(TaskSpecification.hasStatus(status));
            log.info("Adicionando filtro por status: {}", status);
        }
        if (createdAt!= null) {
            spec = spec.and(TaskSpecification.createdAtAfter(createdAt));
            log.info("Adicionando filtro por data de criação a partir de: {}", createdAt);
        }
        if (dueDate!= null) {
            spec = spec.and(TaskSpecification.dueDateBefore(dueDate));
            log.info("Adicionando filtro por data de vencimento até: {}", dueDate);
        }

        return taskRepository.findAll(spec)
                .stream()
                .map(TaskMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public Task updateTask(Long taskId, TaskRequestDTO taskDetails) {
        String userEmail = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Tarefa não encontrada com o id: " + taskId));

        task.setTitle(taskDetails.getTitle());
        task.setDescription(taskDetails.getDescription());
        task.setStatus(taskDetails.getStatus());
        task.setDueDate(taskDetails.getDueDate());
        return taskRepository.save(task);
    }

    @Transactional
    public void deleteTask(Long taskId) {
        String userEmail = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Tarefa não encontrada com o id: " + taskId));

        log.info("--- Verificação de Segurança (DELETE) ---");
        log.info("E-mail do usuário logado (do token): {}", userEmail);
        log.info("E-mail do dono da tarefa (do banco): {}", task.getUser().getEmail());
        log.info("---------------------------------------");

        if (!task.getUser().getEmail().equals(userEmail)) {
            throw new SecurityException("Acesso negado: Você não tem permissão para deletar esta tarefa.");
        }

        taskRepository.deleteById(taskId);
    }

    @Transactional(readOnly = true)
    public List<TaskResponseDTO> findAllTasks() {
        return taskRepository.findAll()
                .stream()
                .map(TaskMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Task findTaskById(Long taskId) {
        String userEmail = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Tarefa não encontrada com o id: " + taskId));

        log.info("--- Verificação de Segurança (DELETE) ---");
        log.info("E-mail do usuário logado (do token): {}", userEmail);
        log.info("E-mail do dono da tarefa (do banco): {}", task.getUser().getEmail());
        log.info("---------------------------------------");

        return task;
    }
}