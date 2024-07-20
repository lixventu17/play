package com.play.controller;

import com.play.util.FileHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.util.List;
import java.io.IOException;

public class ResultController {
    @FXML
    private Label resultText;
    @FXML
    private Label resultLabel;
    @FXML
    private Button nextLevelButton;
    @FXML
    private Button retryButton;

    private String exerciseId;
    private String difficulty;

    public void setResult(boolean isCompleted, String exerciseId, String difficulty, int score, int secondsElapsed) {
        this.exerciseId = exerciseId;
        this.difficulty = difficulty;
        int minutes = secondsElapsed / 60;
        int seconds = secondsElapsed % 60;

        if (isCompleted) {
            resultText.setText("Esercizio completato!\nPunteggio ottenuto: " + score + " su 10\n" + String.format("Tempo impiegato: %02d:%02d", minutes, seconds));
            if (difficulty.equals("principiante")) {
            	retryButton.setVisible(false);
                nextLevelButton.setVisible(true);
                nextLevelButton.setText("Livello successivo: Intermedio");
            } else if (difficulty.equals("intermedio")) {
            	retryButton.setVisible(false);
                nextLevelButton.setVisible(true);
                nextLevelButton.setText("Livello successivo: Esperto");
            } else {
                nextLevelButton.setVisible(false);
            }
        } else {
            resultText.setText("Esercizio non completato!\nPunteggio ottenuto: " + score + " su 10\n" + String.format("Tempo impiegato: %02d:%02d", minutes, seconds));
            nextLevelButton.setVisible(false);
        	retryButton.setVisible(true);
        }
    }

    public void loadResults(String username) {
        List<String> progress = FileHandler.loadUserProgress(username);
        StringBuilder results = new StringBuilder();
        for (String entry : progress) {
            String[] parts = entry.split(";");
            String exerciseId = parts[0];
            String difficulty = parts[1];
            String score = parts[2];
            results.append("Esercizio: ").append(exerciseId)
                    .append(", Difficoltà: ").append(difficulty)
                    .append(", Punteggio: ").append(score).append("\n");
        }
        resultText.setText(results.toString());
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
    }

    @FXML
    private void handleRetryLevel() {
        String title = null;
        String description = null;

        if (exerciseId.equals("exercise1")) {
        	title = "Esercizio 1: Concetti Base";
        	description = "Questo esercizio ti insegnerà i concetti base della programmazione in java.";
        }

        if (difficulty != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/play/view/exercise_details.fxml"));
                Parent root = loader.load();
                ExerciseDetailsController controller = loader.getController();
                // Assume che il titolo e la descrizione per il livello successivo siano già noti o recuperabili
                controller.setExerciseDetails(title, description, exerciseId, difficulty);
                Stage stage = (Stage) retryButton.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
