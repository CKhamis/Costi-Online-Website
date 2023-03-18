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

    private String imageName;

    public Post(String title, String subtitle, String category, String body) {
        this.title = title;
        this.subtitle = subtitle;
        this.category = category;
        this.body = body;
    }

    public String getDateEdited(){
        return lastEdited.getMonthValue() + "/" + lastEdited.getDayOfMonth() + "/" + lastEdited.getYear();
    }

    public boolean isEnabled() {
        return enabled;
    }

    @Transient
    public String getPhotosImagePath() {
        if (imageName == null || id == null) return null;
        String path = "/post-images/" + id + "/" + imageName;
        return path;
    }
}
