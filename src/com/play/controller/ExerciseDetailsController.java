package com.play.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.*;

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

  private String exerciseId;
  private String difficulty;

  @FXML
  public void initialize() {
    // Inizializza la visibilità dei componenti
    if (difficultyComboBox != null) {
      difficultyComboBox.setVisible(false);
    }
    if (startExerciseButton != null) {
      startExerciseButton.setVisible(true);
    }
  }

  public void setExerciseDetails(String title, String description, String exerciseId, String difficulty) {
    exerciseTitle.setText(title);
    exerciseDescription.setText(description);
    this.exerciseId = exerciseId;
    this.difficulty = difficulty;
    difficultyText.setText("Difficoltà: " + difficulty);

    // Check if the user has completed the expert level
    if (difficulty.equals("esperto") && isExerciseCompleted()) {
      if (difficultyComboBox != null) {
        difficultyComboBox.setVisible(true);
        difficultyComboBox.getItems().addAll("principiante", "intermedio", "esperto");
      }
      if (startExerciseButton != null) {
        startExerciseButton.setVisible(false);
      }
    } else {
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
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/play/view/exercise_view.fxml"));
      Parent root = loader.load();
      
      // Imposta i dettagli dell'esercizio nel nuovo controller
      ExerciseController controller = loader.getController();
      controller.startExercise(exerciseId, difficulty);
      
      Stage stage = (Stage) exerciseTitle.getScene().getWindow();
      Scene scene = new Scene(root);
      stage.setScene(scene);
      stage.setTitle("Exercise");
      stage.show();
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

  private boolean isExerciseCompleted() {
    // Logica per verificare se l'esercizio è stato completato
    // Potrebbe leggere da un file o database
    // Per ora simuliamo che l'esercizio avanzato è completato
    return true;
  }

  @FXML
  private void handleDifficultySelection() {
    // Logica per gestire la selezione della difficoltà dal menù a tendina
    String selectedDifficulty = difficultyComboBox.getSelectionModel().getSelectedItem();
    difficultyText.setText("Difficoltà: " + selectedDifficulty);
  }
}
