package com.play.model;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class User implements Serializable {
  private String username;
  private String password;
  private String firstName;
  private String lastName;
  private Map<String, Integer> exerciseProgress;

  public User(String username, String password, String firstName, String lastName) {
    this.username = username;
    this.password = password;
    this.firstName = firstName;
    this.lastName = lastName;
    this.exerciseProgress = new HashMap<>();
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public int getExerciseProgress(String exerciseId) {
      return exerciseProgress.getOrDefault(exerciseId, 0);
  }

  public void setUsername(String username) {
      this.username = username;
  }

  public void setPassword(String password) {
      this.password = password;
  }

  public void setFirstName(String firstName) {
      this.firstName = firstName;
  }

  public void setLastName(String lastName) {
      this.lastName = lastName;
  }

  public void setExerciseProgress(String exerciseId, int progress) {
      exerciseProgress.put(exerciseId, progress);
  }

  @Override
  public String toString() {
      return username + ":" + password + ":" + firstName + ":" + lastName;
  }

  public void saveUserData() {
      try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("resources/com/play/users/" + username + ".dat"))) {
          oos.writeObject(this);
      } catch (IOException e) {
          e.printStackTrace();
      }
  }

  public static User loadUserData(String username) {
      try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("resources/com/play/users/" + username + ".dat"))) {
          return (User) ois.readObject();
      } catch (IOException | ClassNotFoundException e) {
          e.printStackTrace();
      }
      return null;
  }
}