package com.costi.csw9.Model.Temp;

import com.costi.csw9.Model.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class AccountNotificationRequest {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String body;

    private String notificationType;

    private LocalDateTime dateCreated = LocalDateTime.now();

    private String destination;

    public AccountNotificationRequest() {
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
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
        //return dateCreated.getMonthValue() + "/" + dateCreated.getDayOfMonth() + "/" + dateCreated.getYear();
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

    public void setDateCreated(LocalDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }
}
