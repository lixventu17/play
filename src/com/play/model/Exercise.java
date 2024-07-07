package com.play.model;

public class Exercise {
  private String id;
  private String description;
  private String solution;

  public Exercise(String id, String description, String solution) {
    this.id = id;
    this.description = description;
    this.solution = solution;
  }

  public String getId() {
    return id;
  }

  public String getDescription() {
    return description;
  }

  public String getSolution() {
    return solution;
  }
}