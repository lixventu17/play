package com.play.util;

import com.play.model.User;
import com.play.model.Question;
import com.play.model.CompletionQuestion;

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

  public static List<Question> loadQuestions(String exerciseId, String difficulty) {
      List<Question> questions = new ArrayList<>();
      String filePath = "resources/com/play/questions/" + exerciseId + "_" + difficulty + ".txt";
      try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
          String line;
          while ((line = reader.readLine()) != null) {
              String questionText = line;
              String option1 = reader.readLine();
              String option2 = reader.readLine();
              String option3 = reader.readLine();
              String option4 = reader.readLine();
              String correctAnswer = reader.readLine();
              reader.readLine(); // per saltare la riga vuota tra le domande
              
              Question question = new Question(questionText, option1, option2, option3, option4, correctAnswer);
              questions.add(question);
          }
      } catch (IOException e) {
          e.printStackTrace();
      }
      
      return questions;
  }

  public static List<CompletionQuestion> loadCompletionQuestions(String filePath) {
      List<CompletionQuestion> questions = new ArrayList<>();
      try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
          String line;
          while ((line = reader.readLine()) != null) {
              String[] parts = line.split(";");
              if (parts.length == 2) {
                  questions.add(new CompletionQuestion(parts[0], parts[1]));
              }
          }
      } catch (IOException e) {
          e.printStackTrace();
      }
      return questions;
  }
}
