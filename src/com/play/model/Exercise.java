package com.play.model;

public class Exercise {
  private String id;
  private String description;

  public Exercise(String id, String description) {
    this.id = id;
    this.description = description;
  }

  public String getId() {
    return id;
  }

  public String getDescription() {
    return description;
  }
}