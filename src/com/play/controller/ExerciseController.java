package com.play.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Button;

public class ExerciseController {

    @FXML
    private Label exerciseTitle;
    @FXML
    private Label exerciseDifficulty;

    @FXML
    private Button startExerciseButton;

    public void initialize() {
        // Questo metodo viene chiamato automaticamente dopo che i componenti FXML sono stati caricati
        if (exerciseTitle == null || exerciseDifficulty == null || startExerciseButton == null) {
            System.out.println("Componenti FXML non correttamente inizializzati.");
        }
    }

    public void startExercise(String exerciseId, String difficulty) {
        // Imposta i dettagli dell'esercizio
        exerciseTitle.setText("Esercizio " + exerciseId);
        exerciseDifficulty.setText("Difficoltà: " + difficulty);
    }

    @FXML
    private void startExercise() {
        // Logica per iniziare l'esercizio
    }
}
