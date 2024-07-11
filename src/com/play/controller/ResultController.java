package com.play.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;

public class ResultController {
    @FXML
    private Text resultText;

    @FXML
    private Button nextLevelButton;

    @FXML
    private Button homepageButton;

    private String currentExerciseId;
    private String currentDifficulty;
    private boolean isExerciseCompleted;

//    @FXML
//    private Label resultLabel;
//
//    public void setResults(int score, int totalQuestions) {
//        resultLabel.setText("Hai risposto correttamente a " + score + " domande su " + totalQuestions);
//    }

    public void setResult(boolean isCompleted, String exerciseId, String difficulty) {
        this.currentExerciseId = exerciseId;
        this.currentDifficulty = difficulty;
        this.isExerciseCompleted = isCompleted;

        if (isCompleted) {
            resultText.setText("Complimenti! Hai completato l'esercizio.");
            if ("principiante".equals(difficulty)) {
                nextLevelButton.setText("Passa a Intermedio");
                nextLevelButton.setVisible(true);
            } else if ("intermedio".equals(difficulty)) {
                nextLevelButton.setText("Passa a Esperto");
                nextLevelButton.setVisible(true);
            } else {
                nextLevelButton.setVisible(false);
            }
        } else {
            resultText.setText("Esercizio non completato al 100%. Riprova.");
            nextLevelButton.setVisible(false);
        }
    }

    @FXML
    private void handleNextLevel() {
        String nextDifficulty = null;
        if ("principiante".equals(currentDifficulty)) {
            nextDifficulty = "intermedio";
        } else if ("intermedio".equals(currentDifficulty)) {
            nextDifficulty = "esperto";
        }

        if (nextDifficulty != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/play/view/exercise_details.fxml"));
                Parent root = loader.load();
                ExerciseDetailsController controller = loader.getController();
                controller.setExerciseDetails("Esercizio " + currentExerciseId, "Descrizione dell'esercizio", currentExerciseId, nextDifficulty);
                Stage stage = (Stage) resultText.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleHomepage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/play/view/homepage.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) resultText.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Homepage");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
