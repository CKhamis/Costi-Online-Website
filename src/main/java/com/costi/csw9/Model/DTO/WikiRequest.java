package com.costi.csw9.Model.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
public class WikiRequest {
    private Long id;
    @NotBlank
    @NotNull
    private String title;
    @NotBlank
    @NotNull
    private String subtitle;
    @NotBlank
    @NotNull
    private String category;
    @NotBlank
    @NotNull
    private String body;
}
