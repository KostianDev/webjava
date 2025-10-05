package com.webjava.lab1.controller;

public class Violation {
  private String field;
  private String message;

  public Violation() {}

  public Violation(String field, String message) {
    this.field = field;
    this.message = message;
  }

  public String getField() {
    return field;
  }

  public void setField(String field) {
    this.field = field;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  // compatibility aliases
  public String getName() {
    return field;
  }

  public String getReason() {
    return message;
  }
}
