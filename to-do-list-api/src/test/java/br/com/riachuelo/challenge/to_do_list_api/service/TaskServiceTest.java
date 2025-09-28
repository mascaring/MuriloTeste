package br.com.riachuelo.challenge.to_do_list_api.service;

import br.com.riachuelo.challenge.to_do_list_api.dto.TaskRequestDTO;
import br.com.riachuelo.challenge.to_do_list_api.dto.TaskResponseDTO;
import br.com.riachuelo.challenge.to_do_list_api.model.Task;
import br.com.riachuelo.challenge.to_do_list_api.model.TaskStatus;
import br.com.riachuelo.challenge.to_do_list_api.model.User;
import br.com.riachuelo.challenge.to_do_list_api.repository.TaskRepository;
import br.com.riachuelo.challenge.to_do_list_api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TaskService taskService;

    private User ownerUser;
    private User anotherUser;
    private Task task;

    @BeforeEach
    void setUp() {
        ownerUser = new User("Owner User", "owner@email.com", "password");
        ownerUser.setId(1L);

        anotherUser = new User("Another User", "another@email.com", "password");
        anotherUser.setId(2L);

        task = new Task();
        task.setId(10L);
        task.setTitle("Test Task");
        task.setUser(ownerUser);
    }

    private void mockSecurityContext(User user) {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getPrincipal()).thenReturn(user);
    }

    @Test
    void ShouldCreateTaskWithSuccess() {
        mockSecurityContext(ownerUser);
        TaskRequestDTO requestDTO = new TaskRequestDTO();
        requestDTO.setTitle("New Task");
        requestDTO.setStatus(TaskStatus.TO_DO);

        when(userRepository.findByEmail("owner@email.com")).thenReturn(Optional.of(ownerUser));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Task createdTask = taskService.createTask(requestDTO);

        assertNotNull(createdTask);
        assertEquals("New Task", createdTask.getTitle());
        assertEquals(ownerUser, createdTask.getUser());
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void ShouldListTasksThatOwns() {
        mockSecurityContext(ownerUser);
        when(taskRepository.findByUserEmail("owner@email.com")).thenReturn(List.of(task));

        List<TaskResponseDTO> tasks = taskService.findAllTasksByAuthenticatedUser();

        assertEquals(1, tasks.size());
        assertEquals(10L, tasks.get(0).getId());
        verify(taskRepository, times(1)).findByUserEmail("owner@email.com");
    }

    @Test
    void ShouldUpdateTheTaskIfYouAreTheOwner() {
        mockSecurityContext(ownerUser);
        TaskRequestDTO requestDTO = new TaskRequestDTO();
        requestDTO.setTitle("Updated Title");
        requestDTO.setStatus(TaskStatus.DONE);

        when(taskRepository.findById(10L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        Task updatedTask = taskService.updateTask(10L, requestDTO);

        assertEquals("Updated Title", updatedTask.getTitle());
        assertEquals(TaskStatus.DONE, updatedTask.getStatus());
        verify(taskRepository, times(1)).save(task);
    }

    @Test
    void ShouldDeleteTheTaskIfYouAreTheOwner() {
        // Arrange
        mockSecurityContext(ownerUser);
        when(taskRepository.findById(10L)).thenReturn(Optional.of(task));
        doNothing().when(taskRepository).deleteById(10L);

        taskService.deleteTask(10L);

        verify(taskRepository, times(1)).deleteById(10L);
    }

    @Test
    void ShouldNotDeleteTheTaskIfYouAreNotTheOwner() {
        mockSecurityContext(anotherUser);
        when(taskRepository.findById(10L)).thenReturn(Optional.of(task));

        assertThrows(SecurityException.class, () -> {
            taskService.deleteTask(10L);
        });

        verify(taskRepository, never()).deleteById(anyLong());
    }
}