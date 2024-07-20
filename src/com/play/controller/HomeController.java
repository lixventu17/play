package com.play.controller;

import com.play.util.FileHandler;
import com.play.util.Util;
import com.play.util.UserSession;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import java.io.*;
import java.util.List;

public class HomeController {
  @FXML
  private ImageView exerciseImage1;
  @FXML
  private ImageView exerciseImage2;
  @FXML
  private Label usernameLabel;
  @FXML
  private Label nameLabel;

  private List<String> userProgress;
  private boolean completed;

  public void initialize() {
    // Initialize the images (example)
    exerciseImage1.setImage(new Image(getClass().getResourceAsStream("/com/play/images/1.png")));
    exerciseImage2.setImage(new Image(getClass().getResourceAsStream("/com/play/images/2.png")));

    // Set user details from the session
    UserSession session = UserSession.getInstance();
    if (usernameLabel != null) {
      usernameLabel.setText("Username: " + session.getUsername());
    }
    if (nameLabel != null) {
      nameLabel.setText("Nome: " + session.getFirstName() + " " + session.getLastName());
    }

    // Load user progress
    userProgress = FileHandler.loadDifficult(session.getUsername());
    completed = FileHandler.exerciseCompleted();
  }

  @FXML
  private void handleExercise1Click() {
    showExerciseDetails("Esercizio 1: Concetti Base", "Questo esercizio ti insegnerà i concetti base della programmazione in java.", "exercise1");
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
      String difficulty = "principiante";
      if (userProgress.isEmpty() == false) {
          String[] parts = userProgress.getLast().split(";");
          if (parts[1].equals("esperto") && completed == true) {
        	  controller.isExerciseCompleted(true);
          }
    	  difficulty = parts[1];
      }
      controller.setExerciseDetails(title, description, exerciseId, difficulty);

      Stage stage = (Stage) exerciseImage1.getScene().getWindow();
      Scene scene = new Scene(root);
      stage.setScene(scene);
      stage.setTitle("Dettaglio Esercizio");
      stage.show();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @FXML
  private void handleLogout() {
	// Clear the user session
	UserSession.getInstance().clearSession();
    Stage stage = (Stage) exerciseImage1.getScene().getWindow();
    Util.changeScene(stage, "/com/play/view/login.fxml", "Login");
  }
}
