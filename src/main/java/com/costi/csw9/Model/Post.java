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
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(nullable = false, unique = true)
    private String title;
    @Column(nullable = false)
    private LocalDateTime lastEdited;
    @Column(nullable = false)
    private String subtitle;

    @Column(nullable = false)
    private boolean enabled = false;
    private String category;
    @Column(columnDefinition="text")
    private String body;
    @Column(nullable = false)
    private int likes;
    @Column(nullable = false)
    private int dislikes;

    public Post(String title, String subtitle, String category, String body) {
        this.title = title;
        this.subtitle = subtitle;
        this.category = category;
        this.body = body;
        this.likes = 0;
        this.dislikes = 0;
    }

    public Post(){
        this.likes = 0;
        this.dislikes = 0;
    }

    public String getDateEdited(){
        return lastEdited.getMonthValue() + "/" + lastEdited.getDayOfMonth() + "/" + lastEdited.getYear();
    }

    public boolean isEnabled() {
        return enabled;
    }
}
