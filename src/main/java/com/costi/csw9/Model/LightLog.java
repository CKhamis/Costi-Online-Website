package com.costi.csw9.Model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

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
    @Column(nullable = false, unique = true)
    private String ip;
    @Column(nullable = false, unique = true)
    private String color;
    @Column(nullable = false, unique = true)
    private String pattern;
    @Column(nullable = false)
    private LocalDateTime dateCreated;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "light_id", nullable = false)
    private Light light;

    public LightLog(Light light, String ip, String color, String pattern) {
        this.light = light;
        this.ip = ip;
        this.color = color;
        this.pattern = pattern;
        this.dateCreated = LocalDateTime.now();
    }

    public String getlastModified(){
        return dateCreated.getMonthValue() + "/" + dateCreated.getDayOfMonth() + "/" + dateCreated.getYear();
    }
}
