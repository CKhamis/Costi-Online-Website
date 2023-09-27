package com.costi.csw9.Model.DTO;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ResponseMessage {
    private LocalDateTime dateCreated;
    private Severity severity;
    private String message;
    private String title;

    public ResponseMessage(String title, Severity severity, String message) {
        dateCreated = LocalDateTime.now();
        this.severity = severity;
        this.message = message;
        this.title = title;
    }

    public enum Severity {
        INFORMATIONAL,
        LOW,
        MEDIUM,
        HIGH,
        INSANE
    }
}