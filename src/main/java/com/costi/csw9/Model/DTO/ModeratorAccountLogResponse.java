package com.costi.csw9.Model.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class ModeratorAccountLogResponse {
    private Long id;
    private String title, body;
    private LocalDateTime dateCreated;
}
