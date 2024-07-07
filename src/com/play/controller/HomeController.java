package com.play.controller;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class HomeController {
  @FXML
  private ImageView exerciseImage1;
  @FXML
  private ImageView exerciseImage2;

  private Map<String, String> userProgress;

  public void initialize() {
    // Initialize the images (example)
    exerciseImage1.setImage(new Image(getClass().getResourceAsStream("/com/play/images/1.png")));
    exerciseImage2.setImage(new Image(getClass().getResourceAsStream("/com/play/images/2.png")));

    // Load user progress
    userProgress = loadUserProgress();
  }

  private Map<String, String> loadUserProgress() {
    Map<String, String> progress = new HashMap<>();
    try (BufferedReader reader = new BufferedReader(new FileReader("resources/com/play/user_progress.txt"))) {
      String line;
      while ((line = reader.readLine()) != null) {
        String[] parts = line.split("=");
        progress.put(parts[0], parts[1]);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return progress;
  }

  @FXML
  private void handleExercise1Click() {
    showExerciseDetails("Exercise 1: Basic Loops", "This exercise will teach you the basics of loops in programming.", "exercise1");
  }

  @FXML
  private void handleExercise2Click() {
    showExerciseDetails("Exercise 2: Conditionals", "This exercise will teach you about conditionals in programming.", "exercise2");
  }

  @FXML
  private void showExerciseDetails(String title, String description, String exerciseId) {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/play/view/exercise_details.fxml"));
      Parent root = loader.load();

      ExerciseDetailsController controller = loader.getController();
      String difficulty = userProgress.getOrDefault(exerciseId, "principiante");
      controller.setExerciseDetails(title, description, exerciseId, difficulty);

      Stage stage = (Stage) exerciseImage1.getScene().getWindow();
      Scene scene = new Scene(root);
      stage.setScene(scene);
      stage.setTitle("Exercise Details");
      stage.show();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
