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
    @NotNull
    @NotBlank
    private Long id;

    @NotNull
    @NotBlank
    private String firstName, lastName, email;

    @NotNull
    @NotBlank
    private UserRole role;

    @NotNull
    @NotBlank
    private boolean isLocked, enabled;

    @NotNull
    @NotBlank
    private int profilePicture;
}
