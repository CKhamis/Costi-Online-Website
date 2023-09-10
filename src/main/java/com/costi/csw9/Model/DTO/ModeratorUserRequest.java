package com.costi.csw9.Model.DTO;

import com.costi.csw9.Model.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
public class ModeratorUserRequest {
    private Long id;

    @NotNull
    private String firstName, lastName, email;

    @NotNull
    private UserRole role;

    @NotNull
    private boolean isLocked, enabled;

    @NotNull
    private int profilePicture;

    private String password;
}
