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
@NoArgsConstructor
@Entity
public class Announcement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition="text")
    private String body;

    @Column(nullable = false)
    private LocalDateTime date;

    @Column(nullable = false)
    private boolean enable;

    public Announcement(String title, String body) {
        this.title = title;
        this.body = body;
    }

    public String getDateEdited(){
        return date.getMonthValue() + "/" + date.getDayOfMonth() + "/" + date.getYear();
    }
}
