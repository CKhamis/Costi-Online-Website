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
public class WikiPage {
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

    private boolean enabled = false;
    private String category;
    @Column(columnDefinition="text")
    private String body;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User author;

    public WikiPage(String title, String subtitle, User author, String category, String body) {
        this.title = title;
        this.subtitle = subtitle;
        this.author = author;
        this.category = category;
        this.body = body;
    }

    public WikiPage(User author){
        this.author = author;
    }

    public String getDateEdited(){
        return lastEdited.getMonthValue() + "/" + lastEdited.getDayOfMonth() + "/" + lastEdited.getYear();
    }

    public boolean isEnabled() {
        return enabled;
    }
}
