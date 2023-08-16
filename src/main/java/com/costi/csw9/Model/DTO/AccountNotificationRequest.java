package com.costi.csw9.Model.DTO;

import java.time.LocalDateTime;

public class AccountNotificationRequest {
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
}
