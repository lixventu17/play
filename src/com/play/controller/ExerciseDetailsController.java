package com.play.controller;

import com.play.model.Exercise;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Arrays;

public class ExerciseDetailsController {
    @FXML private Label exerciseTitle;
    @FXML private Label descriptionHeader;  // "Descrizione"
    @FXML private Label exerciseDescription;  // Testo della descrizione
    @FXML private Label instructionsHeader;   // "Istruzioni"
    @FXML private Label exerciseInstructions; // Testo delle istruzioni
    @FXML private Label difficultyLabel;
    @FXML private ComboBox<Integer> levelComboBox;  // Visibile solo se level==4
    @FXML private Button startExerciseButton;
    @FXML private Button backToHomepageButton;

    // Parametri passati dalla pagina di progress
    private Exercise exercise;
    private String difficulty;
    private int level; // Valore ottenuto dal progress (1,2,3 oppure 4 se completato)

    /**
     * Imposta i dettagli dell'esercizio.
     * Ora il metodo accetta anche il testo delle istruzioni separatamente.
     * Se level < 4, la difficoltà è fissa; se level == 4, viene mostrato un ComboBox per scegliere il livello.
     */
    public void setExerciseDetails(Exercise exercise, String difficulty, int level) {
        this.exercise = exercise;
        this.difficulty = difficulty;
        this.level = level;
        
        exerciseTitle.setText(exercise.getTitle());
        
        // Imposta i titoletto per le sezioni
        descriptionHeader.setText("Descrizione");
        instructionsHeader.setText("Istruzioni");
        
        exerciseDescription.setText(exercise.getDescription(difficulty, level));
        exerciseInstructions.setText(exercise.getInstructions());
        
        if (level == 4) {
            levelComboBox.setVisible(true);
            levelComboBox.getItems().setAll(Arrays.asList(1, 2, 3));
            levelComboBox.getSelectionModel().select(0);
            difficultyLabel.setText("Difficoltà: " + (Character.toUpperCase(difficulty.charAt(0)) + difficulty.substring(1)) + " (Scegli il livello)");
        } else {
            levelComboBox.setVisible(false);
            difficultyLabel.setText("Difficoltà: " + (Character.toUpperCase(difficulty.charAt(0)) + difficulty.substring(1)) + " (Livello " + level + ")");
        }
    }
    
    @FXML
    private void handleStartExercise() {
        int selectedLevel = level;
        if (levelComboBox.isVisible()) {
            Integer chosen = levelComboBox.getSelectionModel().getSelectedItem();
            selectedLevel = (chosen != null) ? chosen : 1;
        }
        try {
            FXMLLoader loader;
            Parent root;
            // Se la difficoltà è "principiante" usa il Quiz, altrimenti il CompletionExercise
            if (exercise.getId().equalsIgnoreCase("exercise1")) {
                loader = new FXMLLoader(getClass().getResource("/com/play/view/quiz.fxml"));
                root = loader.load();
                QuizController quizController = loader.getController();
                quizController.loadExercise(exercise, difficulty, selectedLevel);
            } else {
                loader = new FXMLLoader(getClass().getResource("/com/play/view/completion_exercise.fxml"));
                root = loader.load();
                CompletionExerciseController compController = loader.getController();
                compController.loadExercise(exercise, difficulty, selectedLevel);
            }
            Stage stage = (Stage) startExerciseButton.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/com/play/application.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle(exercise.getTitle() + " (" + (Character.toUpperCase(difficulty.charAt(0)) + difficulty.substring(1)) + " - Livello " + selectedLevel + ")");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/play/view/exercise_progress.fxml"));
            Parent root = loader.load();
            com.play.controller.ExerciseProgressController progressController = loader.getController();
            progressController.setExercise(exercise);
            Stage stage = (Stage) startExerciseButton.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/com/play/application.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Progresso - " + exercise.getTitle());
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
