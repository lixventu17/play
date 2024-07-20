package com.play.controller;

import com.play.util.FileHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.*;
import java.util.List;

public class ExerciseDetailsController {
  @FXML
  private Text exerciseTitle;
  @FXML
  private Text exerciseDescription;
  @FXML
  private Text difficultyText;
  @FXML
  private ComboBox<String> difficultyComboBox;
  @FXML
  private Button startExerciseButton;
  @FXML
  private Button startQuizButton;
  @FXML
  private Button startCompletionButton;
  @FXML
  private Button viewResultsButton;

  private boolean completed;
  private String exerciseId;
  private String difficulty;

  @FXML
  public void initialize(String username) {
      List<String> progress = FileHandler.loadUserProgress(username);
      for (String entry : progress) {
          String[] parts = entry.split(";");
          if (parts[0].equals(exerciseId)) {
              this.difficulty = parts[1];
              break;
          }
      }
	  // Inizializza la visibilità dei componenti
	  if (difficultyComboBox != null) {
	      difficultyComboBox.setVisible(false);
	  }
	  if (startExerciseButton != null) {
	      startExerciseButton.setVisible(true);
	  }
  }

  public void setExerciseId(String exerciseId) {
      this.exerciseId = exerciseId;
  }

  public void setDifficulty(String difficulty) {
      this.difficulty = difficulty;
  }

  public void setExerciseDetails(String title, String description, String exerciseId, String difficulty) {
    exerciseTitle.setText(title);
    exerciseDescription.setText(description);
    this.exerciseId = exerciseId;

    // Check if the user has completed the expert level
    if (completed == true) {
      if (difficultyComboBox != null) {
        difficultyComboBox.setVisible(true);
        difficultyComboBox.getItems().addAll("principiante", "intermedio", "esperto");
        difficultyComboBox.setOnAction(event -> {
          String selectedDifficulty = difficultyComboBox.getValue();
          if (selectedDifficulty != null) {
            this.difficulty = selectedDifficulty;
            difficultyText.setText("Difficoltà: " + selectedDifficulty);
            startExerciseButton.setDisable(false);
          }
        });
      }
      if (startExerciseButton != null) {
        startExerciseButton.setVisible(true);
        startExerciseButton.setDisable(true);
      }
    } else {
      this.difficulty = difficulty;
      difficultyText.setText("Difficoltà: " + difficulty);
      if (difficultyComboBox != null) {
        difficultyComboBox.setVisible(false);
      }
      if (startExerciseButton != null) {
        startExerciseButton.setVisible(true);
      }
    }
  }

  @FXML
  private void handleBackToHomepage() {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/play/view/homepage.fxml"));
      Parent root = loader.load();
      Stage stage = (Stage) exerciseTitle.getScene().getWindow();
      Scene scene = new Scene(root);
      stage.setScene(scene);
      stage.setTitle("Homepage");
      stage.show();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @FXML
  private void handleChangeDifficulty() {
    // Change difficulty logic
    if (difficulty.equals("principiante")) {
      difficulty = "intermedio";
    } else if (difficulty.equals("intermedio")) {
      difficulty = "avanzato";
    } else {
      difficulty = "principiante";
    }

    // Update difficulty text
    difficultyText.setText("Difficoltà: " + difficulty);

    // Save progress
    saveUserProgress();
  }

  private void saveUserProgress() {
    // Save the user's progress to a file or database
    try (BufferedWriter writer = new BufferedWriter(new FileWriter("resources/com/play/user_progress.txt", true))) {
      writer.write(exerciseId + "=" + difficulty);
      writer.newLine();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @FXML
  private void handleStartExercise() {
    try {
      if (difficulty.equals("principiante")) {
	    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/play/view/quiz.fxml"));
	    Parent root = loader.load();
	    QuizController quizController = loader.getController();
	    quizController.loadExercise(exerciseId, difficulty);
	    Stage stage = (Stage) startExerciseButton.getScene().getWindow();
	    Scene scene = new Scene(root);
	    stage.setScene(scene);
	    stage.setTitle("Esercizio " + exerciseId + " (Difficoltà: " + difficulty + ")");
	    stage.show();
      }
      else {
    	FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/play/view/completion_exercise.fxml"));
        Parent root = loader.load();
        CompletionExerciseController controller = loader.getController();
        controller.loadExercise(exerciseId, difficulty);
	    Stage stage = (Stage) startExerciseButton.getScene().getWindow();
	    Scene scene = new Scene(root);
	    stage.setScene(scene);
	    stage.setTitle("Esercizio " + exerciseId + " (Difficoltà: " + difficulty + ")");
	    stage.show();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void completeExercise() {
    // Logica per completare l'esercizio
    if (difficulty.equals("principiante")) {
      difficulty = "intermedio";
    } else if (difficulty.equals("intermedio")) {
      difficulty = "avanzato";
    } else {
      // L'utente ha completato tutti i livelli di difficoltà
      difficultyComboBox.setVisible(true);
      startExerciseButton.setVisible(false);
      difficultyComboBox.getItems().addAll("principiante", "intermedio", "avanzato");
      return;
    }

    // Save progress and update the UI
    saveUserProgress();
    difficultyText.setText("Difficoltà: " + difficulty);
  }

  public boolean isExerciseCompleted(boolean answer) {
	completed = answer;
    return completed;
  }

  @FXML
  private void handleDifficultySelection() {
    // Logica per gestire la selezione della difficoltà dal menù a tendina
    String selectedDifficulty = difficultyComboBox.getSelectionModel().getSelectedItem();
    difficultyText.setText("Difficoltà: " + selectedDifficulty);
  }
}
