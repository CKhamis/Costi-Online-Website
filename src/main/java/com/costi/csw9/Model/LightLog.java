package com.costi.csw9.Model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@EqualsAndHashCode
@Entity
@NoArgsConstructor
public class LightLog {
    @Id
    @SequenceGenerator(name = "user_sequence", sequenceName = "user_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "user_sequence")
    private Long id;
    @Column(nullable = false, columnDefinition="text")
    private String message;
    @Column(nullable = false)
    private LocalDateTime dateCreated;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "light_id", nullable = false)
    private Light light;

    @Transient
    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/y hh:mm a");

    public LightLog(Light light, String message) {
        this.light = light;
        this.message = message;
        this.dateCreated = LocalDateTime.now();
    }

    @Transient
    public String getFormattedDateCreated(){
        return (dateCreated != null) ? dateCreated.format(formatter) : LocalDateTime.MIN.format(formatter);
    }
}
