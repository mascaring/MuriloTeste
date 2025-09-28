package br.com.riachuelo.challenge.to_do_list_api.controller;

import br.com.riachuelo.challenge.to_do_list_api.dto.TaskRequestDTO;
import br.com.riachuelo.challenge.to_do_list_api.dto.TaskResponseDTO;
import br.com.riachuelo.challenge.to_do_list_api.model.Task;
import br.com.riachuelo.challenge.to_do_list_api.model.TaskStatus;
import br.com.riachuelo.challenge.to_do_list_api.service.JwtService; // Importar
import br.com.riachuelo.challenge.to_do_list_api.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService; // Importar
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TaskService taskService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    @WithMockUser
    void ShouldListAllTasks() throws Exception {
        TaskResponseDTO taskDTO = new TaskResponseDTO();
        taskDTO.setId(1L);
        taskDTO.setTitle("Test Task");
        when(taskService.findAllTasksByAuthenticatedUser()).thenReturn(List.of(taskDTO));

        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void ShouldCreateNewTask() throws Exception {
        TaskRequestDTO requestDTO = new TaskRequestDTO();
        requestDTO.setTitle("New Task");
        requestDTO.setStatus(TaskStatus.TO_DO);

        Task createdTask = new Task();
        createdTask.setId(1L);
        createdTask.setTitle("New Task");

        when(taskService.createTask(any(TaskRequestDTO.class))).thenReturn(createdTask);

        mockMvc.perform(post("/tasks")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("New Task"));
    }

    @Test
    @WithMockUser
    void ShouldUpdateExistingTask() throws Exception {
        TaskRequestDTO requestDTO = new TaskRequestDTO();
        requestDTO.setTitle("Updated Task");
        requestDTO.setStatus(TaskStatus.DONE);

        Task updatedTask = new Task();
        updatedTask.setId(1L);
        updatedTask.setTitle("Updated Task");

        when(taskService.updateTask(eq(1L), any(TaskRequestDTO.class))).thenReturn(updatedTask);

        mockMvc.perform(put("/tasks/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Task"));
    }

    @Test
    @WithMockUser
    void ShouldDeleteTask() throws Exception {
        doNothing().when(taskService).deleteTask(1L);

        mockMvc.perform(delete("/tasks/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }
}