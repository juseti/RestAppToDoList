package com.softserve.itacademy.dto.task;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.softserve.itacademy.model.Task;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;


@Data
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class TaskResponse {

    @NotBlank
    private long id;

    @NotBlank
    private String name;

    @NotBlank
    private String priority;

    @NotBlank
    private long todoId;

    @NotBlank
    private String state;

    public static TaskResponse getTaskResponse(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getName(),
                task.getPriority().name().toUpperCase(),
                task.getTodo().getId(),
                task.getState().getName()
        );
    }
}
