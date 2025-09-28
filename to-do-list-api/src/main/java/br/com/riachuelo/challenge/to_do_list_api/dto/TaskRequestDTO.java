//.../dto/TaskRequestDTO.java

package br.com.riachuelo.challenge.to_do_list_api.dto;

import br.com.riachuelo.challenge.to_do_list_api.model.TaskStatus;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class TaskRequestDTO {

    @NotBlank(message = "O título é obrigatório")
    private String title;

    private String description;

    @NotNull(message = "O status é obrigatório")
    private TaskStatus status;

    @Future(message = "A data de vencimento deve ser uma data futura")
    private LocalDateTime dueDate;
}