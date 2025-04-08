package com.play.controller;

import com.play.model.Exercise;
import com.play.model.User;
import com.play.util.EmailSender;
import com.play.util.FileHandler;
import com.play.util.TelegramSender;
import com.play.util.UserSession;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ResultController {
    @FXML private Label resultTitle;
    @FXML private Label resultScore;
    @FXML private Label resultTime;
    @FXML private Label nextText;
    @FXML private Button nextLevelButton;
    @FXML private Button retryButton;
    @FXML private Label medalText;
    @FXML private ImageView imageMedal;
    @FXML private ImageView imageGmail;
    @FXML private ImageView imageTelegram;
    @FXML private Button emailSummaryButton;
    @FXML private Button telegramSummaryButton;
    @FXML private Button backToProgressButton;

    private Exercise exercise;
    private String difficulty;
    private int level;
    private int score;
    private int secondsElapsed;
    private int totalQuestions = 7; // Per ogni tentativo ci sono 7 domande
    private String attemptOutcome;   // "completato", "non completato", "abbandonato"

    /**
     * Imposta il risultato del tentativo.
     * Vengono registrati anche il livello e la difficoltà.
     */
    public void setResult(boolean isCompleted, Exercise exercise, String difficulty, int level, int score, int secondsElapsed) {
        this.exercise = exercise;
        this.difficulty = difficulty;
        this.level = level;
        this.score = score;
        this.secondsElapsed = secondsElapsed;
        // Il tentativo è completato se il punteggio raggiunge il totale (7)
        attemptOutcome = (score == totalQuestions) ? "completato" : "non completato";
        
        int minutes = secondsElapsed / 60;
        int seconds = secondsElapsed % 60;

        Image gmail = new Image(getClass().getResource("/com/play/images/gmail.png").toExternalForm());
        Image telegram = new Image(getClass().getResource("/com/play/images/telegram.png").toExternalForm());
        imageGmail.setImage(gmail);
        imageTelegram.setImage(telegram);
        
        // Costruisce il testo del risultato
        if (isCompleted) {
        	resultTitle.setText("Esercizio completato!");
        	resultScore.setText("Punteggio ottenuto: " + score + " su " + totalQuestions);
        	resultTime.setText("Tempo impiegato: " + String.format("%02d:%02d", minutes, seconds));
            // Imposta il pulsante per passare al livello successivo in base alla difficoltà e al livello attuale
            if (difficulty.equalsIgnoreCase("principiante")) {
                if (level == 3) {
                    // Se il livello 3 è stato completato, passa a Intermedio livello 1
                    retryButton.setVisible(false);
                    medalText.setVisible(false);
                    imageMedal.setVisible(false);
                    nextText.setText("Prossimo: Intermedio (Livello 1)");
                    nextLevelButton.setVisible(true);
                } else {
                    // Se non è ancora completato il livello 3, il prossimo tentativo è lo stesso livello + 1
                    retryButton.setVisible(false);
                    medalText.setVisible(false);
                    imageMedal.setVisible(false);
                    nextText.setText("Prossimo: Principiante (Livello " + (level + 1) + ")");
                    nextLevelButton.setVisible(true);
                }
            } else if (difficulty.equalsIgnoreCase("intermedio")) {
                if (level == 3) {
                    retryButton.setVisible(false);
                    medalText.setVisible(false);
                    imageMedal.setVisible(false);
                    nextText.setText("Prossimo: Esperto (Livello 1)");
                    nextLevelButton.setVisible(true);
                } else {
                    retryButton.setVisible(false);
                    medalText.setVisible(false);
                    imageMedal.setVisible(false);
                    nextText.setText("Prossimo: Intermedio (Livello " + (level + 1) + ")");
                    nextLevelButton.setVisible(true);
                }
            } else if (difficulty.equalsIgnoreCase("esperto")) {
                if (level == 3) {
                    retryButton.setVisible(false);
                    nextLevelButton.setVisible(false);
                    nextText.setText("Hai completato tutti i livelli! Complimenti!");
                    medalText.setText("Hai ottenuto il badge dell'esercizio: " + exercise.getTitle() + "!");
                    if (exercise.getId().equalsIgnoreCase("exercise1")) {
                    	Image medal = new Image(getClass().getResource("/com/play/images/guarantee1.png").toExternalForm());
                    	imageMedal.setImage(medal);
                    }
                    else {
                    	Image medal = new Image(getClass().getResource("/com/play/images/guarantee2.png").toExternalForm());
                    	imageMedal.setImage(medal);
                    }
                } else {
                    retryButton.setVisible(false);
                    medalText.setVisible(false);
                    imageMedal.setVisible(false);
                    nextText.setText("Prossimo: Esperto (Livello " + (level + 1) + ")");
                    nextLevelButton.setVisible(true);
                }
            }
        } else {
        	resultTitle.setText("Esercizio non completato!");
        	resultScore.setText("Punteggio ottenuto: " + score + " su " + totalQuestions);
        	resultTime.setText("Tempo impiegato: " + String.format("%02d:%02d", minutes, seconds));
            nextText.setText("Riprova: " + (Character.toUpperCase(difficulty.charAt(0)) + difficulty.substring(1)) + " (Livello " + level + ")");
            nextLevelButton.setVisible(false);
            medalText.setVisible(false);
            imageMedal.setVisible(false);
            retryButton.setVisible(true);
        }
        
        // Gestione dell'invio automatico del riepilogo (via email/Telegram)
        User currentUser = FileHandler.loadUser(UserSession.getInstance().getUsername());
        if (currentUser != null) {
            if (currentUser.isAutoSendEnabled()) {
            	if (!currentUser.getEmail().isEmpty()) {
            		sendEmailSummary();
            		emailSummaryButton.setVisible(false);
            		telegramSummaryButton.setVisible(false);
            	}
            	if (!currentUser.getPhoneNumber().isEmpty()) {
            		sendTelegramSummary();
            		emailSummaryButton.setVisible(false);
            		telegramSummaryButton.setVisible(false);
            	}
            } else {
            	if (currentUser.getEmail().isEmpty()) {
            		emailSummaryButton.setDisable(true);
            	}
            	if (currentUser.getPhoneNumber().isEmpty()) {
            		telegramSummaryButton.setDisable(true);
            	}
            }
        }
    }
    
    @FXML
    private void sendEmailSummary() {
    	User currentUser = FileHandler.loadUser(UserSession.getInstance().getUsername());
        if (currentUser != null) {
            if (currentUser.getEmail() != null && !currentUser.getEmail().isEmpty()) {
            	String summary = buildAttemptSummary();
                EmailSender.sendEmail(currentUser.getEmail(), "Riepilogo tentativo - " + exercise.getTitle(), summary);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Riepilogo Inviato");
                alert.setContentText("Il riepilogo del tentativo è stato inviato correttamente.");
                alert.showAndWait();
            }
        }
    }
    
    @FXML
    private void sendTelegramSummary() {
    	User currentUser = FileHandler.loadUser(UserSession.getInstance().getUsername());
        if (currentUser != null) {
            if (currentUser.getPhoneNumber() != null && !currentUser.getPhoneNumber().isEmpty()) {
            	String summary = buildAttemptSummary();
                TelegramSender.sendTelegram(currentUser.getPhoneNumber(), summary);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Riepilogo Inviato");
                alert.setContentText("Il riepilogo del tentativo è stato inviato correttamente.");
                alert.showAndWait();
            }
        }
    }
    
    private String buildAttemptSummary() {
        StringBuilder sb = new StringBuilder();
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm:ss");
        String formattedDate = now.format(dtf);
        sb.append("Riepilogo tentativo\n");
        sb.append("Titolo: ").append(exercise.getTitle()).append("\n");
        sb.append("Difficoltà: ").append(difficulty).append("\n");
        sb.append("Livello: ").append(level).append("\n");
        sb.append("Data/Ora: ").append(formattedDate).append("\n");
        sb.append("Tempo impiegato: ").append(secondsElapsed).append(" secondi\n");
        sb.append("Risposte esatte: ").append(score).append(" su ").append(totalQuestions).append("\n");
        sb.append("Esito: ").append(attemptOutcome).append("\n");
        return sb.toString();
    }
    
    @FXML
    private void handleBackToProgress() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/play/view/exercise_progress.fxml"));
            Parent root = loader.load();
            com.play.controller.ExerciseProgressController progressController = loader.getController();
            progressController.setExercise(exercise);
            Stage stage = (Stage) backToProgressButton.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/com/play/application.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Progresso - " + exercise.getTitle());
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleNextLevel() {
        // Determina la prossima difficoltà e livello in base all'attuale
        String nextDifficulty = null;
        int nextLevel = 0;
        
        if (difficulty.equalsIgnoreCase("principiante")) {
            // Se il livello corrente è 3, passa a "intermedio" livello 1; altrimenti, rimane in "principiante"
            if (level == 3) {
                nextDifficulty = "intermedio";
                nextLevel = 1;
            } else {
                nextDifficulty = "principiante";
                nextLevel = level + 1;
            }
        } else if (difficulty.equalsIgnoreCase("intermedio")) {
            if (level == 3) {
                nextDifficulty = "esperto";
                nextLevel = 1;
            } else {
                nextDifficulty = "intermedio";
                nextLevel = level + 1;
            }
        } else if (difficulty.equalsIgnoreCase("esperto")) {
            if (level == 3) {
                nextDifficulty = null;
                nextLevel = 4;
            } else {
                nextDifficulty = "esperto";
                nextLevel = level + 1;
            }
        }
        
        if (nextDifficulty != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/play/view/exercise_details.fxml"));
                Parent root = loader.load();
                ExerciseDetailsController controller = loader.getController();
                // Passa i nuovi parametri (nextDifficulty e nextLevel) al controller dei dettagli
                controller.setExerciseDetails(exercise, nextDifficulty, nextLevel);
                Stage stage = (Stage) nextLevelButton.getScene().getWindow();
                Scene scene = new Scene(root);
                scene.getStylesheets().add(getClass().getResource("/com/play/application.css").toExternalForm());
                stage.setScene(scene);
                stage.setTitle("Dettaglio Esercizio");
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // Se non esiste un livello successivo, non fare nulla o mostra un messaggio
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Nessun Livello Successivo");
            alert.setContentText("Hai completato tutti i livelli disponibili per questo esercizio.");
            alert.showAndWait();
        }
    }

    @FXML
    private void handleRetryLevel() {
        if (difficulty != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/play/view/exercise_details.fxml"));
                Parent root = loader.load();
                ExerciseDetailsController controller = loader.getController();
                // Passa la stessa difficoltà e lo stesso livello per riprovare lo stesso tentativo
                controller.setExerciseDetails(exercise, difficulty, level);
                Stage stage = (Stage) retryButton.getScene().getWindow();
                Scene scene = new Scene(root);
                scene.getStylesheets().add(getClass().getResource("/com/play/application.css").toExternalForm());
                stage.setScene(scene);
                stage.setTitle("Dettaglio Esercizio");
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
