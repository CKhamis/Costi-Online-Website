package com.costi.csw9.Model.DTO;

import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Getter
@Setter
public class ModeratorWikiRequest extends WikiRequest{
    private boolean enabled;

    public ModeratorWikiRequest(boolean enabled, Long id, @NotBlank @NotNull String title, @NotBlank @NotNull String subtitle, @NotBlank @NotNull String category, @NotBlank @NotNull String body) {
        super(id, title, subtitle, category, body);
        this.enabled = enabled;
    }
}
