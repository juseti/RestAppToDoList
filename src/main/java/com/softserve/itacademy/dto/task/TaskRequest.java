package com.softserve.itacademy.dto.task;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class TaskRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String priority;

    @NotBlank
    private String state;

}
