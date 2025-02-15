package com.costi.csw9.Model;

import com.costi.csw9.Model.DTO.ModeratorAccountLogResponse;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.relational.core.mapping.Table;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="Account_Logs")
public class AccountLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    @Column(name = "id", nullable = false)
    private Long id;

    private String title;

    private String body;

    private LocalDateTime dateCreated = LocalDateTime.now();

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public AccountLog() {
    }

    public AccountLog(String title, String body, User user) {
        this.title = title;
        this.body = body;
        this.user = user;
        this.dateCreated = LocalDateTime.now();
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getDateCreated() {
        return dateCreated.getMonthValue() + "/" + dateCreated.getDayOfMonth() + "/" + dateCreated.getYear();
    }

    @JsonIgnore
    public ModeratorAccountLogResponse getModeratorView(){
        return new ModeratorAccountLogResponse(this.id, this.title, this.body, this.dateCreated, dateCreated.getMonthValue() + "/" + dateCreated.getDayOfMonth() + "/" + dateCreated.getYear());
    }
}
