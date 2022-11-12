package com.softserve.itacademy.dto.todo;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.softserve.itacademy.model.ToDo;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class ToDoResponse {

    @NotBlank
    private long id;

    @NotBlank
    private String title;

    @NotBlank
    private LocalDateTime createdAt;

    @NotBlank
    private long ownerId;

    public static ToDoResponse getToDoResponse(ToDo toDo) {
        return new ToDoResponse(toDo.getId(), toDo.getTitle(), toDo.getCreatedAt(), toDo.getOwner().getId());
    }

}
