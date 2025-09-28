package br.com.riachuelo.challenge.to_do_list_api.dto;

import br.com.riachuelo.challenge.to_do_list_api.model.Task;

public class TaskMapper {

    public static TaskResponseDTO toDTO(Task task) {
        if (task == null) {
            return null;
        }

        TaskResponseDTO dto = new TaskResponseDTO();
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setDueDate(task.getDueDate());
        dto.setDescription(task.getDescription());
        dto.setStatus(task.getStatus());
        dto.setCreatedAt(task.getCreatedAt());
        dto.setUpdatedAt(task.getUpdatedAt());

        if (task.getUser()!= null) {
            dto.setUserId(task.getUser().getId());
            dto.setUserName(task.getUser().getName());
        }

        return dto;
    }
}