package com.softserve.itacademy.dto.todo;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;


@Data
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class CollaboratorResponse {

    @NotBlank
    private long collaboratorId;

    public static CollaboratorResponse getCollaboratorResponse(long id) {
        return new CollaboratorResponse(id);
    }

}
