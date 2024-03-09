package com.costi.csw9.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@org.springframework.data.relational.core.mapping.Table
public class Attachment {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

    private String filename;
    private String fileType;
    private LocalDateTime created;
    private boolean isLocked;

    @Lob
    @JsonIgnore
    private byte[] data;

    public Attachment(String filename, String fileType, byte[] data, boolean isLocked) {
        this.filename = filename;
        this.fileType = fileType;
        this.data = data;
        this.isLocked = isLocked;
        created = LocalDateTime.now();
    }
}
