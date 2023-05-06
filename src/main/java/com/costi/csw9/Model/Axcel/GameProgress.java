package com.costi.csw9.Model.Axcel;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
public class GameProgress implements Serializable {
    private List<String> spriteNamesFound;
    private boolean dialogueCompleted;
    private LocalDateTime timeStart;
    private LocalDateTime timeEnd;

    public GameProgress(){
        dialogueCompleted = false;
        timeStart = LocalDateTime.now();
    }
}
