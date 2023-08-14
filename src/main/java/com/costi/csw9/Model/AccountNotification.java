package com.costi.csw9.Model;

import com.costi.csw9.Model.Temp.AccountNotificationRequest;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

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
    @JsonIgnore
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

    public AccountNotification(AccountNotificationRequest request, User user){
        this.title = request.getTitle();
        this.body = request.getBody();
        this.notificationType = request.getNotificationType();
        this.user = user;
    }

    public Long getDestinationId(){
        return this.user.getId();
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

    public String getTimeSinceCreated() {
        String unit = "";
        LocalDateTime now = LocalDateTime.now();
        long diff;
        if((diff = ChronoUnit.SECONDS.between(dateCreated,now)) < 60){
            unit = "s";
        } else if ((diff = ChronoUnit.MINUTES.between(dateCreated,now)) < 60) {
            unit = "m";
        } else if ((diff = ChronoUnit.HOURS.between(dateCreated,now)) < 24) {
            unit = "h";
        } else if ((diff = ChronoUnit.DAYS.between(dateCreated,now)) < 30) {
            unit = "d";
        } else if ((diff = ChronoUnit.MONTHS.between(dateCreated,now)) < 12) {
            unit = "mo";
        } else{
            diff = ChronoUnit.YEARS.between(dateCreated,now);
        }
        return String.format("%d %s",diff,unit);
    }

    public String getDateCreated(){
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
