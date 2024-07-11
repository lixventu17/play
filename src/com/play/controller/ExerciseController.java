package com.play.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import java.io.IOException;

public class ExerciseController {
    @FXML
    private Label exerciseTitle;
    @FXML
    private Label exerciseDifficulty;
    @FXML
    private Button startExerciseButton;

    private String exerciseId;
    private String difficulty;

    public void initialize() {
    	// Assicurati che i componenti siano correttamente inizializzati
        if (exerciseTitle == null || exerciseDifficulty == null || startExerciseButton == null) {
            System.out.println("Componenti FXML non correttamente inizializzati.");
        }
    }

    public void startExercise(String exerciseId, String difficulty) {
        this.exerciseId = exerciseId;
        this.difficulty = difficulty;
        exerciseTitle.setText("Esercizio " + exerciseId);
        exerciseDifficulty.setText("Difficoltà: " + difficulty);
    }

    @FXML
    private void startExercise() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/play/view/quiz.fxml"));
            Parent root = loader.load();

            QuizController quizController = loader.getController();
            quizController.loadExercise(exerciseId, difficulty);

            Stage stage = (Stage) startExerciseButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Quiz");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
