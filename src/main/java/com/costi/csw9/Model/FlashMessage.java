package com.costi.csw9.Model;

public class FlashMessage {
  private String title;
  private String message;
  private Status status;

  public FlashMessage(String title, String message, Status status) {
    this.title = title;
    this.message = message;
    this.status = status;
  }

  public String getTitle() {
    return title;
  }

  public String getMessage() {
    return message;
  }

  public Status getStatus() {
    return status;
  }

  public static enum Status {
    SUCCESS, FAILURE
  }
}