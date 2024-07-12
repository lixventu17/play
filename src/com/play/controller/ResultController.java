package com.play.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;

public class ResultController {
    @FXML
    private Label resultText;

    @FXML
    private Button nextLevelButton;

    private String exerciseId;
    private String difficulty;

    public void setResult(boolean isCompleted, String exerciseId, String difficulty) {
        this.exerciseId = exerciseId;
        this.difficulty = difficulty;

        if (isCompleted) {
            resultText.setText("Esercizio completato al 100%!");

            if (difficulty.equals("principiante")) {
                nextLevelButton.setVisible(true);
                nextLevelButton.setText("Next Level: Intermedio");
            } else if (difficulty.equals("intermedio")) {
                nextLevelButton.setVisible(true);
                nextLevelButton.setText("Next Level: Esperto");
            } else {
                nextLevelButton.setVisible(false);
            }
        } else {
            resultText.setText("Esercizio completato!");
            nextLevelButton.setVisible(false);
        }
    }

    @FXML
    private void handleNextLevel() {
        String nextDifficulty = null;
        String title = null;
        String description = null;
        if (difficulty.equals("principiante")) {
            nextDifficulty = "intermedio";
        } else if (difficulty.equals("intermedio")) {
            nextDifficulty = "esperto";
        }

        if (exerciseId.equals("exercise1")) {
        	title = "Esercizio 1: Concetti Base";
        	description = "Questo esercizio ti insegnerà i concetti base della programmazione in java.";
        }

        if (nextDifficulty != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/play/view/exercise_details.fxml"));
                Parent root = loader.load();
                ExerciseDetailsController controller = loader.getController();
                // Assume che il titolo e la descrizione per il livello successivo siano già noti o recuperabili
                controller.setExerciseDetails(title, description, exerciseId, nextDifficulty);
                Stage stage = (Stage) nextLevelButton.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

//        if (nextDifficulty != null) {
//            try {
//                FXMLLoader loader = null;
//                if (nextDifficulty.equals("intermedio") || nextDifficulty.equals("esperto")) {
//                    loader = new FXMLLoader(getClass().getResource("/com/play/view/completion_exercise.fxml"));
//                } else {
//                    loader = new FXMLLoader(getClass().getResource("/com/play/view/quiz.fxml"));
//                }
//                Parent root = loader.load();
//                if (nextDifficulty.equals("intermedio") || nextDifficulty.equals("esperto")) {
//                	CompletionExerciseController controller = loader.getController();
//                    controller.loadExercise(exerciseId, nextDifficulty);
//                } else {
//                    QuizController controller = loader.getController();
//                    controller.loadExercise(exerciseId, nextDifficulty);
//                }
//                Stage stage = (Stage) nextLevelButton.getScene().getWindow();
//                stage.setScene(new Scene(root));
//                stage.show();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
    }

    @FXML
    private void handleBackToHomepage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/play/view/homepage.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) nextLevelButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Homepage");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
