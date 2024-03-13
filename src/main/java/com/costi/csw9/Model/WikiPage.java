package com.costi.csw9.Model;

import com.costi.csw9.Model.DTO.ModeratorWikiRequest;
import com.costi.csw9.Model.DTO.WikiRequest;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.relational.core.mapping.Table;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@Entity
@Table(name = "wiki_page")
public class WikiPage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(nullable = false, unique = true)
    private String title;
    @Column(nullable = false)
    private LocalDateTime lastEdited;
    private LocalDateTime dateCreated;
    @Column(nullable = false)
    private String subtitle;

    private boolean enabled;
    private String category;
    @Column(columnDefinition="text")
    private String body;
    @Column(nullable = false)
    private int views;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User author;

    public WikiPage(String title, String subtitle, User author, String category, String body) {
        this.title = title;
        this.subtitle = subtitle;
        this.author = author;
        this.category = category;
        this.body = body;
        views = 0;
        this.enabled = false;
        this.lastEdited = LocalDateTime.now();
        this.dateCreated = LocalDateTime.now();
    }

    public WikiPage(User author){
        this.author = author;
        this.enabled = false;
        views = 0;
        this.lastEdited = LocalDateTime.now();
        this.dateCreated = LocalDateTime.now();
    }

    public String getDateEdited(){
        return lastEdited.getMonthValue() + "/" + lastEdited.getDayOfMonth() + "/" + lastEdited.getYear();
    }

    public String getAuthorName(){
        return this.getAuthor().getFirstName() + " " + this.getAuthor().getLastName();
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void userEditValues(WikiRequest request){
        this.lastEdited = LocalDateTime.now();
        this.title = request.getTitle();
        this.subtitle = request.getSubtitle();
        this.body = request.getBody();
        this.category = request.getCategory();
        this.enabled = false;
    }

    public void moderatorEditValues(ModeratorWikiRequest request){
        this.lastEdited = LocalDateTime.now();
        this.title = request.getTitle();
        this.subtitle = request.getSubtitle();
        this.body = request.getBody();
        this.category = request.getCategory();
        this.enabled = request.isEnabled();
    }
}
