package com.costi.csw9.email;

public interface EmailSender {
    void send(String to, String subject, String content);
}
