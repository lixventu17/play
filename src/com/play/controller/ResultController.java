package com.play.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.play.model.Exercise;
import com.play.model.User;
import com.play.util.EmailSender;
import com.play.util.FileHandler;
import com.play.util.TelegramSender;
import com.play.util.UserSession;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Controller per la gestione dei risultati degli esercizi.
 * Gestisce la visualizzazione dei risultati, l'invio di riepiloghi e la navigazione tra i livelli.
 * Estende la funzionalità base di un controller FXML.
 */
public class ResultController extends BaseController {
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
    @FXML private Label confirmationLabel;
    @FXML private VBox mainContent;
    @FXML private VBox loadingContent;
    @FXML private ProgressIndicator loadingSpinner;
    @FXML private Label loadingLabel;

    private User currentUser;
    private Exercise exercise;
    private String difficulty;
    private int level;
    private int score;
    private int secondsElapsed;
    private final int totalQuestions = 7; // Per ogni tentativo ci sono 7 domande
    private String attemptOutcome;   // "completato", "non completato", "abbandonato"

    @FXML
    public void initialize() {
    	currentUser = FileHandler.loadUser(UserSession.getInstance().getUsername());
        if (exercise != null && difficulty != null) {
            // Mostra la rotellina di caricamento
            showLoading(true);
            // Avvia l'invio automatico in un thread separato
            startAutoSendInBackground();
        }
    }

    /**
     * Imposta il risultato del tentativo.
     * Vengono registrati anche il livello e la difficoltà.
     * 
     * @param isCompleted Indica se l'esercizio è stato completato
     * @param exercise L'esercizio completato
     * @param difficulty La difficoltà dell'esercizio
     * @param level Il livello dell'esercizio
     * @param score Il punteggio ottenuto
     * @param secondsElapsed Il tempo impiegato in secondi
     * @throws IllegalArgumentException se i parametri non sono validi
     */
    public void setResult(boolean isCompleted, Exercise exercise, String difficulty, int level, int score, int secondsElapsed) {
        try {
            validateParameters(exercise, difficulty, level, score, secondsElapsed);
            this.exercise = exercise;
            this.difficulty = difficulty;
            this.level = level;
            this.score = score;
            this.secondsElapsed = secondsElapsed;
            attemptOutcome = (score == totalQuestions) ? "completato" : "non completato";
            loadImages();
            updateResultDisplay(isCompleted);
            
            // Se l'invio automatico è attivo, mostra la rotellina e avvia l'invio in background
            if (currentUser != null && currentUser.isAutoSendEnabled()) {
                showLoading(true);
                startAutoSendInBackground();
            } else {
                // Altrimenti, mostra direttamente il contenuto principale
                showLoading(false);
                handleSendSummary();
            }
        } catch (Exception e) {
            handleError("Errore di impostazione risultato", "Impossibile impostare il risultato: " + e.getMessage());
        }
    }

    /**
     * Valida i parametri di input.
     */
    private void validateParameters(Exercise exercise, String difficulty, int level, int score, int secondsElapsed) {
        if (exercise == null) {
            throw new IllegalArgumentException("L'esercizio non può essere null");
        }
        if (difficulty == null || difficulty.trim().isEmpty()) {
            throw new IllegalArgumentException("La difficoltà non può essere null o vuota");
        }
        if (level < 1 || level > 4) {
            throw new IllegalArgumentException("Il livello deve essere compreso tra 1 e 4");
        }
        if (score < 0 || score > totalQuestions) {
            throw new IllegalArgumentException("Il punteggio deve essere compreso tra 0 e " + totalQuestions);
        }
        if (secondsElapsed < 0) {
            throw new IllegalArgumentException("Il tempo non può essere negativo");
        }
    }

    /**
     * Carica le immagini per le icone.
     */
    private void loadImages() {
        try {
            Image gmail = new Image(getClass().getResource("/com/play/images/gmail.png").toExternalForm());
            Image telegram = new Image(getClass().getResource("/com/play/images/telegram.png").toExternalForm());
            imageGmail.setImage(gmail);
            imageTelegram.setImage(telegram);
        } catch (Exception e) {
            handleError("Errore di caricamento immagini", "Impossibile caricare le icone: " + e.getMessage());
        }
    }

    /**
     * Aggiorna la visualizzazione del risultato.
     */
    private void updateResultDisplay(boolean isCompleted) {
        int minutes = secondsElapsed / 60;
        int seconds = secondsElapsed % 60;

        if (isCompleted) {
            displayCompletedResult(minutes, seconds);
        } else {
            displayIncompleteResult(minutes, seconds);
        }
    }

    /**
     * Visualizza il risultato per un esercizio completato.
     */
    private void displayCompletedResult(int minutes, int seconds) {
        resultTitle.setText("Esercizio completato!");
        resultScore.setText("Punteggio ottenuto: " + score + " su " + totalQuestions);
        resultTime.setText("Tempo impiegato: " + String.format("%02d:%02d", minutes, seconds));
        
        updateNextLevelDisplay();
    }

    /**
     * Visualizza il risultato per un esercizio non completato.
     */
    private void displayIncompleteResult(int minutes, int seconds) {
        resultTitle.setText("Esercizio non completato!");
        resultScore.setText("Punteggio ottenuto: " + score + " su " + totalQuestions);
        resultTime.setText("Tempo impiegato: " + String.format("%02d:%02d", minutes, seconds));
        
        nextText.setText("Riprova: " + capitalize(difficulty) + " (Livello " + level + ")");
        nextLevelButton.setVisible(false);
        medalText.setVisible(false);
        imageMedal.setVisible(false);
        retryButton.setVisible(true);
    }

    /**
     * Aggiorna la visualizzazione del prossimo livello.
     */
    private void updateNextLevelDisplay() {
        retryButton.setVisible(false);
        medalText.setVisible(false);
        imageMedal.setVisible(false);

        if (difficulty.equalsIgnoreCase("principiante")) {
            if (level == 3) {
                nextText.setText("Prossimo: Intermedio (Livello 1)");
            } else {
                nextText.setText("Prossimo: Principiante (Livello " + (level + 1) + ")");
            }
            nextLevelButton.setVisible(true);
        } else if (difficulty.equalsIgnoreCase("intermedio")) {
            if (level == 3) {
                nextText.setText("Prossimo: Esperto (Livello 1)");
            } else {
                nextText.setText("Prossimo: Intermedio (Livello " + (level + 1) + ")");
            }
            nextLevelButton.setVisible(true);
        } else if (difficulty.equalsIgnoreCase("esperto")) {
            if (level == 3) {
                displayFinalLevelCompletion();
            } else {
                nextText.setText("Prossimo: Esperto (Livello " + (level + 1) + ")");
                nextLevelButton.setVisible(true);
            }
        }
    }

    /**
     * Visualizza il completamento dell'ultimo livello.
     */
    private void displayFinalLevelCompletion() {
        nextLevelButton.setVisible(false);
        nextText.setText("Hai completato tutti i livelli! Complimenti!");
        medalText.setText("Hai ottenuto il badge dell'esercizio: " + exercise.getTitle() + "!");
        medalText.setVisible(true);
        imageMedal.setVisible(true);
        
        try {
            Image medal = new Image(getClass().getResource(
                exercise.getId().equalsIgnoreCase("exercise1") ? 
                "/com/play/images/guarantee1.png" : 
                "/com/play/images/guarantee2.png"
            ).toExternalForm());
            imageMedal.setImage(medal);
        } catch (Exception e) {
            handleError("Errore di caricamento medaglia", "Impossibile caricare l'immagine della medaglia: " + e.getMessage());
        }
    }

    /**
     * Mostra o nasconde la rotellina di caricamento.
     * 
     * @param show true per mostrare la rotellina, false per nasconderla
     */
    private void showLoading(boolean show) {
        Platform.runLater(() -> {
            loadingContent.setVisible(show);
            mainContent.setVisible(!show);
        });
    }
    
    /**
     * Avvia l'invio automatico in un thread separato.
     */
    private void startAutoSendInBackground() {
        Thread autoSendThread = new Thread(() -> {
            try {
                // Simula un piccolo delay per mostrare la rotellina
                Thread.sleep(500);
                
                final boolean[] emailSent = {false};
                final boolean[] telegramSent = {false};
                
                if (currentUser != null && currentUser.isAutoSendEnabled()) {
                    // Se l'invio automatico è attivo, invia i recap
                    if (!currentUser.getEmail().isEmpty()) {
                        sendEmailSummaryInBackground(false);
                        emailSent[0] = true;
                    }
                    if (currentUser.hasTelegramConfigured()) {
                        sendTelegramSummaryInBackground(false);
                        telegramSent[0] = true;
                    }
                }
                
                // Aggiorna l'interfaccia nel thread principale
                Platform.runLater(() -> {
                    if (currentUser != null && currentUser.isAutoSendEnabled()) {
                        emailSummaryButton.setVisible(false);
                        telegramSummaryButton.setVisible(false);
                        if (emailSent[0] || telegramSent[0]) {
                            showConfirmationLabel("Riepilogo inviato automaticamente " +
                                (emailSent[0] && telegramSent[0] ? "via Email e Telegram." : (emailSent[0] ? "via Email." : "via Telegram.")));
                        }
                    } else {
                        // Se l'invio automatico è disattivo, mostra i pulsanti
                        emailSummaryButton.setVisible(true);
                        telegramSummaryButton.setVisible(true);
                        emailSummaryButton.setDisable(currentUser.getEmail().isEmpty());
                        telegramSummaryButton.setDisable(!currentUser.hasTelegramConfigured());
                    }
                    
                    // Nascondi la rotellina e mostra il contenuto principale
                    showLoading(false);
                });
                
            } catch (Exception e) {
                Platform.runLater(() -> {
                    handleError("Errore di invio automatico", "Impossibile gestire l'invio automatico: " + e.getMessage());
                    showLoading(false);
                });
            }
        });
        
        autoSendThread.setDaemon(true);
        autoSendThread.start();
    }
    
    /**
     * Gestisce l'invio automatico del riepilogo (versione sincrona per compatibilità).
     */
    private void handleSendSummary() {
        try {
            if (currentUser != null) {
                emailSummaryButton.setVisible(true);
                telegramSummaryButton.setVisible(true);
                emailSummaryButton.setDisable(currentUser.getEmail().isEmpty());
                telegramSummaryButton.setDisable(!currentUser.hasTelegramConfigured());
            }
        } catch (Exception e) {
            handleError("Errore di invio", "Impossibile gestire l'invio: " + e.getMessage());
        }
    }

    /**
     * Invia il riepilogo via email.
     */
    @FXML
    private void sendEmailSummary() { sendEmailSummary(true); }
    private void sendEmailSummary(boolean showAlert) {
        try {
            if (currentUser != null && !currentUser.getEmail().isEmpty()) {
                String summary = buildAttemptSummary();
                EmailSender.sendEmail(currentUser.getEmail(), "Riepilogo tentativo - " + exercise.getTitle(), summary);
                if (showAlert) showSuccessAlert("Riepilogo Inviato", "Il riepilogo del tentativo è stato inviato correttamente.");
            }
        } catch (Exception e) {
            handleError("Errore di invio email", "Impossibile inviare il riepilogo via email: " + e.getMessage());
        }
    }

    /**
     * Invia il riepilogo via email (versione background).
     */
    private void sendEmailSummaryInBackground(boolean showAlert) {
        try {
            if (currentUser != null && !currentUser.getEmail().isEmpty()) {
                String summary = buildAttemptSummary();
                EmailSender.sendEmail(currentUser.getEmail(), "Riepilogo tentativo - " + exercise.getTitle(), summary);
                if (showAlert) {
                    Platform.runLater(() -> showSuccessAlert("Riepilogo Inviato", "Il riepilogo del tentativo è stato inviato correttamente."));
                }
            }
        } catch (Exception e) {
            if (showAlert) {
                Platform.runLater(() -> handleError("Errore di invio email", "Impossibile inviare il riepilogo via email: " + e.getMessage()));
            }
        }
    }
    
    /**
     * Invia il riepilogo via Telegram (versione background).
     */
    private void sendTelegramSummaryInBackground(boolean showAlert) {
        try {
            if (currentUser != null && currentUser.hasTelegramConfigured()) {
                String summary = buildAttemptSummary();
                try {
                    sendResultToUser(currentUser, summary);
                    if (showAlert) {
                        Platform.runLater(() -> showSuccessAlert("Riepilogo Inviato", "Il riepilogo del tentativo è stato inviato correttamente."));
                    }
                } catch (RuntimeException e) {
                    if (showAlert) {
                        if (e.getMessage() != null && e.getMessage().contains("chat not found")) {
                            Platform.runLater(() -> handleError("Errore di invio Telegram", "Impossibile inviare il riepilogo: devi prima avviare la chat con il bot su Telegram e premere Start."));
                        } else {
                            Platform.runLater(() -> handleError("Errore di invio Telegram", "Impossibile inviare il riepilogo via Telegram: " + e.getMessage()));
                        }
                    }
                }
            }
        } catch (Exception e) {
            if (showAlert) {
                Platform.runLater(() -> handleError("Errore di invio Telegram", "Impossibile inviare il riepilogo via Telegram: " + e.getMessage()));
            }
        }
    }
    
    /**
     * Invia il riepilogo via Telegram.
     */
    @FXML
    private void sendTelegramSummary() { sendTelegramSummary(true); }
    private void sendTelegramSummary(boolean showAlert) {
        try {
            if (currentUser != null && currentUser.hasTelegramConfigured()) {
                String summary = buildAttemptSummary();
                try {
                    sendResultToUser(currentUser, summary);
                    if (showAlert) showSuccessAlert("Riepilogo Inviato", "Il riepilogo del tentativo è stato inviato correttamente.");
                } catch (RuntimeException e) {
                    if (e.getMessage() != null && e.getMessage().contains("chat not found")) {
                        handleError("Errore di invio Telegram", "Impossibile inviare il riepilogo: devi prima avviare la chat con il bot su Telegram e premere Start.");
                    } else {
                        handleError("Errore di invio Telegram", "Impossibile inviare il riepilogo via Telegram: " + e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            handleError("Errore di invio Telegram", "Impossibile inviare il riepilogo via Telegram: " + e.getMessage());
        }
    }

    /**
     * Mostra un alert di successo.
     */
    private void showSuccessAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Costruisce il riepilogo del tentativo.
     * 
     * @return Il riepilogo formattato
     */
    private String buildAttemptSummary() {
        StringBuilder sb = new StringBuilder();
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm:ss");
        
        sb.append("Riepilogo tentativo\n")
          .append("Titolo: ").append(exercise.getTitle()).append("\n")
          .append("Difficoltà: ").append(difficulty).append("\n")
          .append("Livello: ").append(level).append("\n")
          .append("Data/Ora: ").append(now.format(dtf)).append("\n")
          .append("Tempo impiegato: ").append(secondsElapsed).append(" secondi\n")
          .append("Risposte esatte: ").append(score).append(" su ").append(totalQuestions).append("\n")
          .append("Esito: ").append(attemptOutcome).append("\n");
        
        return sb.toString();
    }

    /**
     * Gestisce il ritorno alla pagina del progresso.
     */
    @FXML
    private void handleBackToProgress() {
        try {
            navigateToProgress();
        } catch (Exception e) {
            handleError("Errore di navigazione", "Impossibile tornare alla pagina del progresso: " + e.getMessage());
        }
    }

    /**
     * Naviga alla pagina del progresso.
     */
    private void navigateToProgress() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/play/view/exercise_progress.fxml"));
        Parent root = loader.load();
        ExerciseProgressController progressController = loader.getController();
        progressController.setExercise(exercise);
        
        Stage stage = (Stage) backToProgressButton.getScene().getWindow();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/com/play/application.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("Progresso - " + exercise.getTitle());
        stage.show();
    }

    /**
     * Gestisce il passaggio al livello successivo.
     */
    @FXML
    private void handleNextLevel() {
        try {
            LevelInfo nextLevel = calculateNextLevel();
            if (nextLevel != null) {
                navigateToNextLevel(nextLevel);
            } else {
                showNoNextLevelAlert();
            }
        } catch (Exception e) {
            handleError("Errore di navigazione", "Impossibile passare al livello successivo: " + e.getMessage());
        }
    }

    /**
     * Calcola le informazioni del prossimo livello.
     * 
     * @return Le informazioni del prossimo livello
     */
    private LevelInfo calculateNextLevel() {
        if (difficulty.equalsIgnoreCase("principiante")) {
            return level == 3 ? 
                new LevelInfo("intermedio", 1) : 
                new LevelInfo("principiante", level + 1);
        } else if (difficulty.equalsIgnoreCase("intermedio")) {
            return level == 3 ? 
                new LevelInfo("esperto", 1) : 
                new LevelInfo("intermedio", level + 1);
        } else if (difficulty.equalsIgnoreCase("esperto")) {
            return level == 3 ? null : new LevelInfo("esperto", level + 1);
        }
        return null;
    }

    /**
     * Naviga al livello successivo.
     */
    private void navigateToNextLevel(LevelInfo nextLevel) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/play/view/exercise_details.fxml"));
        Parent root = loader.load();
        ExerciseDetailsController controller = loader.getController();
        controller.setExerciseDetails(exercise, nextLevel.difficulty, nextLevel.level);
        
        Stage stage = (Stage) nextLevelButton.getScene().getWindow();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/com/play/application.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("Dettaglio Esercizio");
        stage.show();
    }

    /**
     * Mostra l'alert per nessun livello successivo.
     */
    private void showNoNextLevelAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Nessun Livello Successivo");
        alert.setContentText("Hai completato tutti i livelli disponibili per questo esercizio.");
        alert.showAndWait();
    }

    /**
     * Gestisce il riprova del livello corrente.
     */
    @FXML
    private void handleRetryLevel() {
        try {
            if (difficulty != null) {
                navigateToRetry();
            }
        } catch (Exception e) {
            handleError("Errore di navigazione", "Impossibile riprovare il livello: " + e.getMessage());
        }
    }

    /**
     * Naviga alla pagina di riprova.
     */
    private void navigateToRetry() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/play/view/exercise_details.fxml"));
        Parent root = loader.load();
        ExerciseDetailsController controller = loader.getController();
        controller.setExerciseDetails(exercise, difficulty, level);
        
        Stage stage = (Stage) retryButton.getScene().getWindow();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/com/play/application.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("Dettaglio Esercizio");
        stage.show();
    }

    /**
     * Classe interna per gestire le informazioni del livello.
     */
    private static class LevelInfo {
        final String difficulty;
        final int level;

        LevelInfo(String difficulty, int level) {
            this.difficulty = difficulty;
            this.level = level;
        }
    }

    /**
     * Capitalizza la prima lettera di una stringa.
     * 
     * @param s La stringa da capitalizzare
     * @return La stringa capitalizzata
     */
    private String capitalize(String s) {
        if (s == null || s.isEmpty()) {
            return s;
        }
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }

    private void sendResultToUser(User user, String summary) {
        String chatId = user.getTelegramChatId();
        if (TelegramSender.isTelegramConfigured(chatId)) {
            TelegramSender.sendTelegram(chatId, summary);
        } else {
            throw new IllegalArgumentException("L'utente non ha configurato Telegram");
        }
    }

    private void showConfirmationLabel(String message) {
        confirmationLabel.setText(message);
        confirmationLabel.setVisible(true);
        // Nascondi la label dopo 4 secondi
        new Thread(() -> {
            try { Thread.sleep(4000); } catch (InterruptedException ignored) {}
            javafx.application.Platform.runLater(() -> confirmationLabel.setVisible(false));
        }).start();
    }
}
