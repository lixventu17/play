package com.play.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.play.model.Exercise;
import com.play.model.User;
import com.play.util.EmailSender;
import com.play.util.FileHandler;
import com.play.util.TelegramSender;
import com.play.util.UserSession;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javax.mail.MessagingException;

/**
 * Controller per la gestione del progresso degli esercizi.
 * Permette la visualizzazione e il monitoraggio del progresso dell'utente attraverso i vari livelli di difficoltà.
 * Estende BaseController per ereditare funzionalità comuni di gestione degli errori e navigazione.
 */
public class ExerciseProgressController extends BaseController {
    @FXML private Label titleLabel;
    @FXML private ImageView imageViewBeginner;
    @FXML private ImageView imageViewIntermediate;
    @FXML private ImageView imageViewAdvanced;
    @FXML private Label progressLabelBeginner;
    @FXML private Label progressLabelIntermediate;
    @FXML private Label progressLabelAdvanced;
    @FXML private ProgressBar progressBarBeginner;
    @FXML private ProgressBar progressBarIntermediate;
    @FXML private ProgressBar progressBarAdvanced;
    @FXML private Label prerequisiteLabel;
    @FXML private ImageView imageMedal;
    @FXML private ImageView imageGmail;
    @FXML private ImageView imageTelegram;
    @FXML private Button btnEmailRecap;
    @FXML private Button btnTelegramRecap;

    // Ora l'esercizio viene passato come oggetto dal model
    private Exercise exercise;

    /**
     * Imposta l'esercizio corrente e inizializza l'interfaccia.
     * 
     * @param exercise L'esercizio da visualizzare
     */
    public void setExercise(Exercise exercise) {
        try {
            this.exercise = exercise;
            titleLabel.setText("Progresso - " + exercise.getTitle());
            loadImages();
            updateProgress();
        } catch (Exception e) {
            handleError("Errore di inizializzazione", "Impossibile caricare i dati dell'esercizio: " + e.getMessage());
        }
    }

    /**
     * Carica le immagini necessarie per l'interfaccia.
     * 
     * @throws IOException se si verifica un errore durante il caricamento delle immagini
     */
    private void loadImages() throws IOException {
        try {
            // Carica le immagini delle difficoltà
            String beginnerPath = "/com/play/images/1.png";
            String intermediatePath = "/com/play/images/2.png";
            String advancedPath = "/com/play/images/3.png";
            String gmailPath = "/com/play/images/gmail.png";
            String telegramPath = "/com/play/images/telegram.png";

            Image imgBeginner = new Image(getClass().getResourceAsStream(beginnerPath));
            Image imgIntermediate = new Image(getClass().getResourceAsStream(intermediatePath));
            Image imgAdvanced = new Image(getClass().getResourceAsStream(advancedPath));
            Image gmail = new Image(getClass().getResourceAsStream(gmailPath));
            Image telegram = new Image(getClass().getResourceAsStream(telegramPath));
            
            // Imposta le immagini nelle ImageView
            imageViewBeginner.setImage(imgBeginner);
            imageViewIntermediate.setImage(imgIntermediate);
            imageViewAdvanced.setImage(imgAdvanced);
            imageGmail.setImage(gmail);
            imageTelegram.setImage(telegram);

            // Configura le proprietà delle ImageView per le difficoltà
            configureDifficultyImageView(imageViewBeginner);
            configureDifficultyImageView(imageViewIntermediate);
            configureDifficultyImageView(imageViewAdvanced);

            // Configura le proprietà delle ImageView per i pulsanti
            configureButtonImageView(imageGmail);
            configureButtonImageView(imageTelegram);

        } catch (Exception e) {
            throw new IOException("Errore nel caricamento delle immagini: " + e.getMessage());
        }
    }

    /**
     * Configura le proprietà di una ImageView per le difficoltà.
     * 
     * @param imageView L'ImageView da configurare
     */
    private void configureDifficultyImageView(ImageView imageView) {
        imageView.setFitHeight(200);
        imageView.setFitWidth(200);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
    }

    /**
     * Configura le proprietà di una ImageView per i pulsanti.
     * 
     * @param imageView L'ImageView da configurare
     */
    private void configureButtonImageView(ImageView imageView) {
        imageView.setFitHeight(20);
        imageView.setFitWidth(20);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
    }

    /**
     * Aggiorna la visualizzazione del progresso per tutti i livelli di difficoltà.
     */
    private void updateProgress() {
        try {
            String username = UserSession.getInstance().getUsername();
            List<String> progressList = FileHandler.loadUserProgress(username);

            Map<String, Integer> completedLevels = calculateCompletedLevels(progressList);
            updateProgressBars(completedLevels);
            updateCursors(completedLevels);
            updateRecapButtons();
        } catch (Exception e) {
            handleError("Errore di aggiornamento", "Impossibile aggiornare il progresso: " + e.getMessage());
        }
    }

    /**
     * Calcola il numero di livelli completati per ogni difficoltà.
     * 
     * @param progressList Lista dei record di progresso
     * @return Mappa con il numero di livelli completati per difficoltà
     */
    private Map<String, Integer> calculateCompletedLevels(List<String> progressList) {
        Map<String, Integer> completedLevels = new HashMap<>();
        completedLevels.put("principiante", 0);
        completedLevels.put("intermedio", 0);
        completedLevels.put("esperto", 0);

        for (String rec : progressList) {
            String[] parts = rec.split(";");
            if (parts.length >= 5 && parts[0].equalsIgnoreCase(exercise.getId())) {
                try {
                    String difficulty = parts[1].toLowerCase();
                    int score = Integer.parseInt(parts[3]);
                    if (score == 7) {
                        completedLevels.put(difficulty, completedLevels.get(difficulty) + 1);
                    }
                } catch (NumberFormatException e) {
                    // Ignora record non validi
                }
            }
        }
        return completedLevels;
    }

    /**
     * Aggiorna le barre di progresso e le etichette per ogni difficoltà.
     * 
     * @param completedLevels Mappa con il numero di livelli completati per difficoltà
     */
    private void updateProgressBars(Map<String, Integer> completedLevels) {
        updateSingleProgressBar(completedLevels.get("principiante"), progressBarBeginner, progressLabelBeginner);
        updateSingleProgressBar(completedLevels.get("intermedio"), progressBarIntermediate, progressLabelIntermediate);
        updateSingleProgressBar(completedLevels.get("esperto"), progressBarAdvanced, progressLabelAdvanced);

        if (completedLevels.get("esperto") >= 3) {
            updateMedalImage();
        }
    }

    /**
     * Aggiorna una singola barra di progresso e la sua etichetta.
     * 
     * @param completed Numero di livelli completati
     * @param progressBar Barra di progresso da aggiornare
     * @param label Etichetta da aggiornare
     */
    private void updateSingleProgressBar(int completed, ProgressBar progressBar, Label label) {
        if (completed >= 3) {
            Platform.runLater(() -> {
                Node track = progressBar.lookup(".track");
                if (track != null) {
                    track.setStyle("-fx-background-color: green;");
                }
            });
            label.setText("3 / 3");
        } else {
            progressBar.setProgress(completed / 3.0);
            label.setText(completed + " / 3");
        }
    }

    /**
     * Aggiorna l'immagine della medaglia in base all'esercizio.
     */
    private void updateMedalImage() {
        try {
            String medalPath = exercise.getId().equalsIgnoreCase("exercise1") ? 
                "/com/play/images/guarantee1.png" : 
                "/com/play/images/guarantee2.png";
            Image medal = new Image(getClass().getResourceAsStream(medalPath));
            imageMedal.setImage(medal);
            imageMedal.setFitHeight(100);
            imageMedal.setFitWidth(100);
            imageMedal.setPreserveRatio(true);
            imageMedal.setSmooth(true);
        } catch (Exception e) {
            handleError("Errore di visualizzazione", "Impossibile caricare l'immagine della medaglia: " + e.getMessage());
        }
    }

    /**
     * Aggiorna i cursori delle immagini in base al progresso.
     * 
     * @param completedLevels Mappa con il numero di livelli completati per difficoltà
     */
    private void updateCursors(Map<String, Integer> completedLevels) {
        imageViewBeginner.setCursor(Cursor.HAND);
        
        if (completedLevels.get("principiante") >= 3) {
            imageViewIntermediate.setCursor(Cursor.HAND);
        } else {
            imageViewIntermediate.setCursor(Cursor.DEFAULT);
        }
        
        if (completedLevels.get("intermedio") >= 3) {
            imageViewAdvanced.setCursor(Cursor.HAND);
        } else {
            imageViewAdvanced.setCursor(Cursor.DEFAULT);
        }
    }

    /**
     * Ottiene il livello corrente per una determinata difficoltà.
     * 
     * @param difficulty La difficoltà dell'esercizio
     * @return Il livello corrente (1-4) o 0 se non ci sono livelli completati
     */
    private int getCurrentLevel(String difficulty) {
        try {
            String username = UserSession.getInstance().getUsername();
            List<String> progressList = FileHandler.loadUserProgress(username);
            int currentLevel = 0;

            for (String rec : progressList) {
                String[] parts = rec.split(";");
                if (parts.length >= 5 && 
                    parts[0].equalsIgnoreCase(exercise.getId()) && 
                    parts[1].equalsIgnoreCase(difficulty)) {
                    try {
                        int level = Integer.parseInt(parts[2]);
                        int score = Integer.parseInt(parts[3]);
                        if (score == 7 && level > currentLevel) {
                            currentLevel = level;
                        }
                    } catch (NumberFormatException e) {
                        // Ignora record non validi
                    }
                }
            }
            return currentLevel + 1; // Il prossimo livello da completare
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Aggiorna lo stato dei pulsanti di recap in base ai dati dell'utente.
     */
    private void updateRecapButtons() {
        try {
            User currentUser = FileHandler.loadUser(UserSession.getInstance().getUsername());
            if (currentUser != null) {
                btnEmailRecap.setDisable(currentUser.getEmail() == null || currentUser.getEmail().isEmpty());
                btnTelegramRecap.setDisable(!currentUser.hasTelegramConfigured());
            }
        } catch (Exception e) {
            btnEmailRecap.setDisable(true);
            btnTelegramRecap.setDisable(true);
        }
    }

    /**
     * Gestisce il click sul livello principiante.
     */
    @FXML
    private void handleBeginnerClick() {
        int currentLevel = getCurrentLevel("principiante");
        if (currentLevel >= 1 && currentLevel <= 4) {
            startExercise("principiante", currentLevel);
        }
    }

    /**
     * Gestisce il click sul livello intermedio.
     */
    @FXML
    private void handleIntermediateClick() {
        if (getCurrentLevel("principiante") == 4) {
            int currentLevel = getCurrentLevel("intermedio");
            if (currentLevel >= 1 && currentLevel <= 4) {
                startExercise("intermedio", currentLevel);
            }
        } else {
            prerequisiteLabel.setText("Completa tutti i livelli della difficoltà Principiante per sbloccare la difficoltà Intermedio.");
        }
    }

    /**
     * Gestisce il click sul livello esperto.
     */
    @FXML
    private void handleAdvancedClick() {
        if (getCurrentLevel("intermedio") == 4) {
            int currentLevel = getCurrentLevel("esperto");
            if (currentLevel >= 1 && currentLevel <= 4) {
                startExercise("esperto", currentLevel);
            }
        } else {
            prerequisiteLabel.setText("Completa tutti i livelli della difficoltà Intermedio per sbloccare la difficoltà Esperto.");
        }
    }

    /**
     * Avvia l'esercizio con la difficoltà e il livello specificati.
     * 
     * @param difficulty La difficoltà dell'esercizio
     * @param level Il livello dell'esercizio
     */
    private void startExercise(String difficulty, int level) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/play/view/exercise_details.fxml"));
            Parent root = loader.load();
            ExerciseDetailsController controller = loader.getController();
            controller.setExerciseDetails(exercise, difficulty, level);
            
            Stage stage = (Stage) imageViewBeginner.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/com/play/application.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Descrizione e Istruzioni - " + exercise.getTitle());
            stage.show();
        } catch (IOException e) {
        	handleError("Errore di navigazione", "Impossibile avviare l'esercizio: " + e.getMessage());
        }
    }

    /**
     * Gestisce il ritorno alla homepage.
     */
    @FXML
    private void handleBackToHomepage() {
        navigateToPage("/com/play/view/homepage.fxml", imageViewBeginner, "Homepage");
    }

    /**
     * Invia il recap via email.
     * 
     * @param user L'utente a cui inviare il recap
     * @throws MessagingException se si verifica un errore durante l'invio dell'email
     */
    private void sendEmailRecap(User user) throws MessagingException {
        String recap = buildRecap();
        if (user.getEmail() != null && !user.getEmail().isEmpty()) {
            try {
                EmailSender.sendEmail(user.getEmail(), "Recap - " + exercise.getTitle(), recap);
            } catch (MessagingException e) {
                throw new MessagingException("Errore durante l'invio dell'email: " + e.getMessage(), e);
            }
        }
    }

    /**
     * Gestisce l'invio del recap via email.
     */
    @FXML
    private void handleEmailRecap() {
        try {
            User currentUser = FileHandler.loadUser(UserSession.getInstance().getUsername());
            if (currentUser != null && currentUser.getEmail() != null && !currentUser.getEmail().isEmpty()) {
                sendEmailRecap(currentUser);
                showInformation("EMAIL INVIATA!", "Il recap dell'esercizio è stato inviato via EMAIL.");
            } else {
                handleError("Errore di invio", "Email non configurata per l'utente");
            }
        } catch (MessagingException e) {
            handleError("Errore di invio", "Impossibile inviare l'email: " + e.getMessage());
        } catch (Exception e) {
            handleError("Errore di invio", "Errore imprevisto durante l'invio dell'email: " + e.getMessage());
        }
    }

    /**
     * Gestisce l'invio del recap via Telegram.
     */
    @FXML
    private void handleTelegramRecap() {
        try {
            User currentUser = FileHandler.loadUser(UserSession.getInstance().getUsername());
            if (currentUser != null && currentUser.hasTelegramConfigured()) {
                try {
                    sendTelegramRecap(currentUser);
                    showInformation("MESSAGGIO TELEGRAM INVIATO!", "Il recap dell'esercizio è stato inviato via TELEGRAM.");
                } catch (RuntimeException e) {
                    if (e.getMessage() != null && e.getMessage().contains("chat not found")) {
                        handleError("Errore di invio Telegram", "Impossibile inviare il recap: devi prima avviare la chat con il bot @RiepilogoPlayBot su Telegram e premere Start.");
                    } else {
                        handleError("Errore di invio Telegram", "Impossibile inviare il recap via Telegram: " + e.getMessage());
                    }
                }
            } else {
                handleError("Errore di invio", "Chat ID Telegram non configurato per l'utente");
            }
        } catch (Exception e) {
            handleError("Errore di invio", "Impossibile inviare il messaggio Telegram: " + e.getMessage());
        }
    }

    /**
     * Invia il recap via Telegram.
     * 
     * @param user L'utente a cui inviare il recap
     */
    private void sendTelegramRecap(User user) {
        String recap = buildRecap();
        if (user.hasTelegramConfigured()) {
            sendRecapToUser(user, recap);
        } else {
            handleError("Errore di invio", "Chat ID Telegram non configurato per l'utente");
        }
    }

    /**
     * Invia il recap all'utente.
     * 
     * @param user L'utente a cui inviare il recap
     * @param recap Il recap da inviare
     */
    private void sendRecapToUser(User user, String recap) {
        String chatId = user.getTelegramChatId();
        if (TelegramSender.isTelegramConfigured(chatId)) {
            TelegramSender.sendTelegram(chatId, recap);
        } else {
            throw new IllegalArgumentException("L'utente non ha configurato Telegram");
        }
    }

    /**
     * Costruisce il recap dettagliato per l'esercizio.
     * 
     * @return Il recap formattato
     */
    private String buildRecap() {
        StringBuilder sb = new StringBuilder();
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm:ss");
        String formattedDate = now.format(dtf);
        sb.append("Recap ").append(exercise.getTitle()).append("\n");
        sb.append("Data/Ora: ").append(formattedDate).append("\n\n");

        String[] difficulties = {"principiante", "intermedio", "esperto"};
        boolean overallCompleted = true;

        for (String diff : difficulties) {
            Map<Integer, RecapInfo> levelRecap = buildLevelRecap(diff);
            boolean diffCompleted = updateRecapForDifficulty(sb, diff, levelRecap);
            if (!diffCompleted) {
                overallCompleted = false;
            }
        }
        
        sb.append("\nSTATO COMPLESSIVO: ").append(overallCompleted ? "COMPLETATO" : "NON COMPLETATO");
        return sb.toString();
    }

    /**
     * Costruisce il recap per un livello specifico.
     * 
     * @param difficulty La difficoltà da analizzare
     * @return Mappa con le informazioni di recap per ogni livello
     */
    private Map<Integer, RecapInfo> buildLevelRecap(String difficulty) {
        Map<Integer, RecapInfo> levelRecap = new HashMap<>();
        for (int lvl = 1; lvl <= 3; lvl++) {
            levelRecap.put(lvl, new RecapInfo());
        }
        
        try {
            List<String> progressList = FileHandler.loadUserProgress(UserSession.getInstance().getUsername());
            for (String rec : progressList) {
                String[] parts = rec.split(";");
                if (parts.length >= 5 && parts[0].equalsIgnoreCase(exercise.getId()) && parts[1].equalsIgnoreCase(difficulty)) {
                    try {
                        int lvl = Integer.parseInt(parts[2]);
                        int scr = Integer.parseInt(parts[3]);
                        RecapInfo info = levelRecap.get(lvl);
                        if (info != null) {
                            updateRecapInfo(info, scr);
                        }
                    } catch (NumberFormatException e) {
                        // Ignora record non validi
                    }
                }
            }
        } catch (Exception e) {
            handleError("Errore di lettura", "Impossibile leggere il progresso dell'utente: " + e.getMessage());
        }
        
        return levelRecap;
    }

    /**
     * Aggiorna le informazioni di recap per un tentativo.
     * 
     * @param info Le informazioni di recap da aggiornare
     * @param score Il punteggio del tentativo
     * @throws IllegalArgumentException se il punteggio è negativo
     */
    private void updateRecapInfo(RecapInfo info, int score) {
        if (score < 0) {
            throw new IllegalArgumentException("Il punteggio non può essere negativo");
        }
        
        if (score == 7) {
            info.incrementCompleted();
        } else if (score > 0) {
            info.incrementNonCompleted();
        } else {
            info.incrementAbandoned();
        }
    }

    /**
     * Aggiorna il recap per una difficoltà specifica.
     * 
     * @param sb StringBuilder per costruire il recap
     * @param diff La difficoltà da analizzare
     * @param levelRecap Mappa con le informazioni di recap per ogni livello
     * @return true se la difficoltà è stata completata, false altrimenti
     * @throws IllegalArgumentException se la difficoltà è null o vuota
     */
    private boolean updateRecapForDifficulty(StringBuilder sb, String diff, Map<Integer, RecapInfo> levelRecap) {
        if (diff == null || diff.trim().isEmpty()) {
            throw new IllegalArgumentException("La difficoltà non può essere null o vuota");
        }

        sb.append(capitalize(diff)).append(":\n");
        boolean diffCompleted = true;
        
        for (int lvl = 1; lvl <= 3; lvl++) {
            RecapInfo info = levelRecap.get(lvl);
            if (info == null) {
                throw new IllegalStateException("Informazioni mancanti per il livello " + lvl);
            }
            
            String levelStatus = info.isCompleted() ? "completato" : "non completato";
            if (!info.isCompleted()) {
                diffCompleted = false;
            }
            sb.append("  Livello ").append(lvl).append(" - Stato: ").append(levelStatus).append("\n");
            sb.append("    Tentativi completati: ").append(info.completedCount).append("\n");
            sb.append("    Tentativi non completati: ").append(info.nonCompletedCount).append("\n");
            sb.append("    Tentativi abbandonati: ").append(info.abandonedCount).append("\n");
        }
        
        sb.append("Stato ").append(": ").append(diffCompleted ? "COMPLETATO" : "NON COMPLETATO").append("\n");
        sb.append("----------------------------------\n");
        return diffCompleted;
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
     * Classe interna per memorizzare le informazioni di recap di un livello.
     * Implementa il pattern Value Object per rappresentare i dati di progresso.
     */
    private static final class RecapInfo {
        private int completedCount;
        private int nonCompletedCount;
        private int abandonedCount;

        /**
         * Costruttore di default che inizializza i contatori a zero.
         */
        public RecapInfo() {
            this.completedCount = 0;
            this.nonCompletedCount = 0;
            this.abandonedCount = 0;
        }

        /**
         * Incrementa il contatore dei tentativi completati.
         */
        public void incrementCompleted() {
            this.completedCount++;
        }

        /**
         * Incrementa il contatore dei tentativi non completati.
         */
        public void incrementNonCompleted() {
            this.nonCompletedCount++;
        }

        /**
         * Incrementa il contatore dei tentativi abbandonati.
         */
        public void incrementAbandoned() {
            this.abandonedCount++;
        }

        /**
         * Verifica se il livello è stato completato.
         * @return true se il livello è stato completato almeno una volta
         */
        public boolean isCompleted() {
            return completedCount > 0;
        }
    }
}
