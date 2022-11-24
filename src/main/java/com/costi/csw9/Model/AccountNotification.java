package com.costi.csw9.Model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="Account_notifications")
public class AccountNotification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String body;

    private String notificationType;

    private LocalDateTime dateCreated = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public AccountNotification() {
    }

    public AccountNotification(String title, String body, User user) {
        this.title = title;
        this.body = body;
        notificationType = "primary";
        this.user = user;
    }

    public AccountNotification(String title, String body, String notificationType, User user) {
        this.title = title;
        this.body = body;
        this.notificationType = notificationType;
        this.user = user;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
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

    public void setDateCreated(LocalDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
