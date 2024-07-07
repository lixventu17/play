package com.play.util;

import com.play.model.User;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileHandler {
  private static final String USER_DIRECTORY = "resources/com/play/users";

  public static List<User> readUsers() throws IOException {
    List<User> users = new ArrayList<>();
    File dir = new File(USER_DIRECTORY);
    File[] files = dir.listFiles((d, name) -> name.endsWith(".dat"));

    if (files != null) {
      for (File file : files) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
          users.add((User) ois.readObject());
        } catch (ClassNotFoundException e) {
          e.printStackTrace();
        }
      }
    }
    return users;
  }

  public static void writeUser(User user) throws IOException {
    try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(USER_DIRECTORY + "/" + user.getUsername() + ".dat"))) {
      oos.writeObject(user);
    }
  }

  public static User loadUser(String username) {
    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(USER_DIRECTORY + "/" + username + ".dat"))) {
      return (User) ois.readObject();
    } catch (IOException | ClassNotFoundException e) {
      e.printStackTrace();
    }
    return null;
  }
}
