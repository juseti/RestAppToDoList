package com.softserve.itacademy.dto.user;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.softserve.itacademy.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;


@Data
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class UserResponse {

    @NotBlank
    private long id;

    @NotBlank
    private String firstName;

    @NotBlank
    private String email;


    public static UserResponse getUserResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getFirstName(),
                user.getEmail()
        );
    }
}
