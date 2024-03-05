package com.costi.csw9.Model.DTO;

import com.costi.csw9.Model.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
public class UserAccountRequest {
    private Long id;
    @NotNull
    private String firstName, lastName, email;

    @NotNull
    private int profilePicture;

    private String password;
}
