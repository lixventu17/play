package com.play.controller;

import java.io.IOException;
import java.util.Arrays;

import com.play.model.Exercise;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * Controller per la gestione dei dettagli di un esercizio.
 * Gestisce la visualizzazione delle informazioni dettagliate di un esercizio,
 * inclusi titolo, descrizione, istruzioni e livelli di difficoltà.
 * Estende la funzionalità base di un controller FXML.
 */
public class ExerciseDetailsController extends BaseController {
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
     * Se level < 4, la difficoltà è fissa; se level == 4, viene mostrato un ComboBox per scegliere il livello.
     * 
     * @param exercise L'esercizio da visualizzare
     * @param difficulty La difficoltà dell'esercizio
     * @param level Il livello corrente (1-4)
     * @throws IllegalArgumentException se exercise è null o difficulty è null/vuota
     */
    public void setExerciseDetails(Exercise exercise, String difficulty, int level) {
        if (exercise == null) {
            throw new IllegalArgumentException("L'esercizio non può essere null");
        }
        if (difficulty == null || difficulty.trim().isEmpty()) {
            throw new IllegalArgumentException("La difficoltà non può essere null o vuota");
        }
        if (level < 1 || level > 4) {
            throw new IllegalArgumentException("Il livello deve essere compreso tra 1 e 4");
        }

        this.exercise = exercise;
        this.difficulty = difficulty;
        this.level = level;

        exerciseTitle.setText(exercise.getTitle());
        descriptionHeader.setText("Descrizione");
        instructionsHeader.setText("Istruzioni");

        exerciseDescription.setText(exercise.getDescription(difficulty, level));
        exerciseInstructions.setText(exercise.getInstructions());

        updateDifficultyLabel();
        updateLevelComboBox();
    }

    /**
     * Aggiorna l'etichetta della difficoltà in base al livello corrente.
     */
    private void updateDifficultyLabel() {
        String capitalizedDifficulty = capitalize(difficulty);
        if (level == 4) {
            difficultyLabel.setText("Difficoltà: " + capitalizedDifficulty + " (Scegli il livello)");
        } else {
            difficultyLabel.setText("Difficoltà: " + capitalizedDifficulty + " (Livello " + level + ")");
        }
    }

    /**
     * Aggiorna il ComboBox dei livelli in base al livello corrente.
     */
    private void updateLevelComboBox() {
        if (level == 4) {
            levelComboBox.setVisible(true);
            levelComboBox.getItems().setAll(Arrays.asList(null, 1, 2, 3));
            levelComboBox.getSelectionModel().select(0);
            // Disabilita il pulsante di default quando il ComboBox è visibile
            startExerciseButton.setDisable(true);
            // Listener per aggiornare la descrizione quando cambia il livello selezionato
            levelComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                int selectedLevel = (newVal != null) ? newVal : 4;
                exerciseDescription.setText(exercise.getDescription(difficulty, selectedLevel));
                // Disabilita il pulsante se non è selezionato alcun livello
                startExerciseButton.setDisable(newVal == null);
            });
        } else {
            levelComboBox.setVisible(false);
        }
    }

    /**
     * Capitalizza la prima lettera di una stringa.
     * 
     * @param s La stringa da capitalizzare
     * @return La stringa capitalizzata
     * @throws IllegalArgumentException se la stringa è null
     */
    private String capitalize(String s) {
        if (s == null) {
            throw new IllegalArgumentException("La stringa non può essere null");
        }
        if (s.isEmpty()) {
            return s;
        }
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }

    /**
     * Gestisce l'avvio dell'esercizio.
     * Carica l'interfaccia appropriata in base al tipo di esercizio.
     * 
     * @throws IllegalStateException se l'esercizio non è stato inizializzato
     */
    @FXML
    private void handleStartExercise() {
        if (exercise == null) {
            throw new IllegalStateException("L'esercizio non è stato inizializzato");
        }

        int selectedLevel = level;
        if (levelComboBox.isVisible()) {
            Integer chosen = levelComboBox.getSelectionModel().getSelectedItem();
            selectedLevel = (chosen != null) ? chosen : 1;
        }

        try {
            FXMLLoader loader;
            Parent root;
            
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
            stage.setTitle(exercise.getTitle() + " (" + capitalize(difficulty) + " - Livello " + selectedLevel + ")");
            stage.show();
        } catch (IOException e) {
            handleError("Errore di navigazione", "Impossibile avviare l'esercizio: " + e.getMessage());
        }
    }

    /**
     * Gestisce il ritorno alla pagina del progresso.
     * 
     * @throws IllegalStateException se l'esercizio non è stato inizializzato
     */
    @FXML
    private void handleBack() {
        if (exercise == null) {
            throw new IllegalStateException("L'esercizio non è stato inizializzato");
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/play/view/exercise_progress.fxml"));
            Parent root = loader.load();
            ExerciseProgressController progressController = loader.getController();
            progressController.setExercise(exercise);
            
            Stage stage = (Stage) startExerciseButton.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/com/play/application.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Progresso - " + exercise.getTitle());
            stage.show();
        } catch (IOException e) {
            handleError("Errore di navigazione", "Impossibile tornare alla pagina del progresso: " + e.getMessage());
        }
    }
}
