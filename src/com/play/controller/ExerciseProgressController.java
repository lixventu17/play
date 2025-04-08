package com.play.controller;

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
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ExerciseProgressController {
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

    public void setExercise(Exercise exercise) {
        this.exercise = exercise;
        titleLabel.setText("Progresso - " + exercise.getTitle());
        
        // Carica le immagini per i cubotti e per i pulsanti recap
        try {
            Image imgBeginner = new Image(getClass().getResource("/com/play/images/1.png").toExternalForm());
            Image imgIntermediate = new Image(getClass().getResource("/com/play/images/2.png").toExternalForm());
            Image imgAdvanced = new Image(getClass().getResource("/com/play/images/3.png").toExternalForm());
            Image gmail = new Image(getClass().getResource("/com/play/images/gmail.png").toExternalForm());
            Image telegram = new Image(getClass().getResource("/com/play/images/telegram.png").toExternalForm());
            imageViewBeginner.setImage(imgBeginner);
            imageViewIntermediate.setImage(imgIntermediate);
            imageViewAdvanced.setImage(imgAdvanced);
            imageGmail.setImage(gmail);
            imageTelegram.setImage(telegram);
        } catch (Exception e) {
            System.err.println("Errore nel caricamento delle immagini: " + e.getMessage());
        }
        updateProgress();
    }
    
    private void updateProgress() {
        String username = UserSession.getInstance().getUsername();
        List<String> progressList = FileHandler.loadUserProgress(username);
        
        int beginnerCompleted = 0;
        int intermediateCompleted = 0;
        int advancedCompleted = 0;
        
        // Formato: exerciseId;difficoltà;livello;score;seconds
        for (String rec : progressList) {
            String[] parts = rec.split(";");
            if (parts.length >= 5 && parts[0].equalsIgnoreCase(exercise.getId())) {
                String recDifficulty = parts[1];
                try {
                    int score = Integer.parseInt(parts[3]);
                    if (score == 7) {
                        if (recDifficulty.equalsIgnoreCase("principiante"))
                            beginnerCompleted++;
                        else if (recDifficulty.equalsIgnoreCase("intermedio"))
                            intermediateCompleted++;
                        else if (recDifficulty.equalsIgnoreCase("esperto"))
                            advancedCompleted++;
                    }
                } catch (NumberFormatException e) {
                    // ignora record non validi
                }
            }
        }

        if (beginnerCompleted >= 3) {
        	Platform.runLater(() -> {
        	    Node track = progressBarBeginner.lookup(".track");
        	    if (track != null) {
        	        track.setStyle("-fx-background-color: green;");
        	    }
        	});
            progressLabelBeginner.setText("3 / 3");
        }
        else {
            progressBarBeginner.setProgress(beginnerCompleted / 3.0);
            progressLabelBeginner.setText(beginnerCompleted + " / 3");
        }
        if (intermediateCompleted >= 3) {
        	Platform.runLater(() -> {
        	    Node track = progressBarIntermediate.lookup(".track");
        	    if (track != null) {
        	        track.setStyle("-fx-background-color: green;");
        	    }
        	});
            progressLabelIntermediate.setText("3 / 3");
        }
        else {
            progressBarIntermediate.setProgress(intermediateCompleted / 3.0);
            progressLabelIntermediate.setText(intermediateCompleted + " / 3");
        }
        if (advancedCompleted >= 3) {
        	Platform.runLater(() -> {
        	    Node track = progressBarAdvanced.lookup(".track");
        	    if (track != null) {
        	        track.setStyle("-fx-background-color: green;");
        	    }
        	});
            progressLabelAdvanced.setText("3 / 3");
            if (exercise.getId().equalsIgnoreCase("exercise1")) {
            	Image medal = new Image(getClass().getResource("/com/play/images/guarantee1.png").toExternalForm());
            	imageMedal.setImage(medal);
            }
            else {
            	Image medal = new Image(getClass().getResource("/com/play/images/guarantee2.png").toExternalForm());
            	imageMedal.setImage(medal);
            }
        }
        else {
            progressBarAdvanced.setProgress(advancedCompleted / 3.0);
            progressLabelAdvanced.setText(advancedCompleted + " / 3");
        }
        
        // Imposta il cursore: Principiante è sempre sbloccato
        imageViewBeginner.setCursor(Cursor.HAND);
        // Per intermedio, è sbloccato solo se il livello corrente di principiante è 4
        if (getCurrentLevel("principiante") == 4) {
            imageViewIntermediate.setCursor(Cursor.HAND);
        } else {
            imageViewIntermediate.setCursor(Cursor.DEFAULT);
        }
        // Per esperto, è sbloccato solo se il livello corrente di intermedio è 4
        if (getCurrentLevel("intermedio") == 4) {
            imageViewAdvanced.setCursor(Cursor.HAND);
        } else {
            imageViewAdvanced.setCursor(Cursor.DEFAULT);
        }

        User currentUser = FileHandler.loadUser(UserSession.getInstance().getUsername());
        if (currentUser != null) {
        	if (currentUser.getEmail().isEmpty()) {
        		btnEmailRecap.setDisable(true);
        	}
        	if (currentUser.getPhoneNumber().isEmpty()) {
        		btnTelegramRecap.setDisable(true);
        	}
        }
    }
    
    // Restituisce il livello corrente per una data difficoltà.
    // Se non ci sono record, restituisce 1; se 3 tentativi completati, restituisce 4.
    private int getCurrentLevel(String diff) {
        int completedCount = 0;
        String username = UserSession.getInstance().getUsername();
        List<String> progressList = FileHandler.loadUserProgress(username);
        for (String rec : progressList) {
            String[] parts = rec.split(";");
            if (parts.length >= 5 && parts[0].equalsIgnoreCase(exercise.getId()) && parts[1].equalsIgnoreCase(diff)) {
                try {
                    int score = Integer.parseInt(parts[3]);
                    if (score == 7) {
                        completedCount++;
                    }
                } catch (NumberFormatException e) {
                    // ignora
                }
            }
        }
        if (completedCount >= 3) {
            return 4;
        }
        return completedCount + 1;
    }
    
    @FXML
    private void handleBeginnerClick() {
        int currentLevel = getCurrentLevel("principiante");
        if (currentLevel >= 1 && currentLevel <= 4) {
            startExercise("principiante", currentLevel);
        }
    }
    
    @FXML
    private void handleIntermediateClick() {
        if (getCurrentLevel("principiante") == 4) {
            int currentLevel = getCurrentLevel("intermedio");
            if (currentLevel >= 1 && currentLevel <= 4) {
                startExercise("intermedio", currentLevel);
            }
        }
        else {
        	String msg = "Completa tutti i livelli della difficoltà Principiante per sbloccare la difficoltà Intermedio.";
            prerequisiteLabel.setText(msg);
        }
    }
    
    @FXML
    private void handleAdvancedClick() {
        if (getCurrentLevel("intermedio") == 4) {
            int currentLevel = getCurrentLevel("esperto");
            if (currentLevel >= 1 && currentLevel <= 4) {
                startExercise("esperto", currentLevel);
            }
        }
        else {
        	String msg = "Completa tutti i livelli della difficoltà Intermedio per sbloccare la difficoltà Esperto.";
            prerequisiteLabel.setText(msg);
        }
    }
    
    private void startExercise(String diff, int level) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/play/view/exercise_details.fxml"));
            Parent root = loader.load();
            ExerciseDetailsController controller = loader.getController();
            controller.setExerciseDetails(exercise, diff, level);
            Stage stage = (Stage) imageViewBeginner.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/com/play/application.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Descrizione e Istruzioni - " + exercise.getTitle());
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleBackToHomepage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/play/view/homepage.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) imageViewBeginner.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/com/play/application.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Homepage");
            stage.show();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleEmailRecap() {
        User currentUser = FileHandler.loadUser(UserSession.getInstance().getUsername());
        if (currentUser != null) {
            sendEmailRecap(currentUser);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("EMAIL INVIATA!");
            alert.setContentText("Il recap dell'esercizio è stato inviato via EMAIL.");
            alert.showAndWait();
        }
    }
    
    @FXML
    private void handleTelegramRecap() {
        User currentUser = FileHandler.loadUser(UserSession.getInstance().getUsername());
        if (currentUser != null) {
            sendTelegramRecap(currentUser);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("MESSAGGIO TELEGRAM INVIATO!");
            alert.setContentText("Il recap dell'esercizio è stato inviato via TELEGRAM.");
            alert.showAndWait();
        }
    }
    
    private void sendEmailRecap(User user) {
        String recap = buildRecap();
        if (user.getEmail() != null && !user.getEmail().isEmpty()) {
            EmailSender.sendEmail(user.getEmail(), "Recap - " + exercise.getTitle(), recap);
        }
    }
    
    private void sendTelegramRecap(User user) {
        String recap = buildRecap();
        if (user.getPhoneNumber() != null && !user.getPhoneNumber().isEmpty()) {
            TelegramSender.sendTelegram(user.getPhoneNumber(), recap);
        }
    }
    
    /**
     * Costruisce il recap dettagliato per l'esercizio.
     * Per ogni difficoltà (principiante, intermedio, esperto) e per ciascun livello (1,2,3):
     * - Stato del livello: "completato" se almeno un tentativo con score==7, altrimenti "non completato".
     * - Conteggio di tentativi completati, non completati (score>0 e !=7) e abbandonati (score==0).
     * Dopo l'elenco delle difficoltà, viene aggiunta una riga vuota e poi lo stato complessivo dell'esercizio.
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
            Map<Integer, RecapInfo> levelRecap = new HashMap<>();
            for (int lvl = 1; lvl <= 3; lvl++) {
                levelRecap.put(lvl, new RecapInfo());
            }
            List<String> progressList = FileHandler.loadUserProgress(UserSession.getInstance().getUsername());
            for (String rec : progressList) {
                String[] parts = rec.split(";");
                if (parts.length >= 5 && parts[0].equalsIgnoreCase(exercise.getId()) && parts[1].equalsIgnoreCase(diff)) {
                    int lvl;
                    int scr;
                    try {
                        lvl = Integer.parseInt(parts[2]);
                        scr = Integer.parseInt(parts[3]);
                    } catch (NumberFormatException e) {
                        continue;
                    }
                    RecapInfo info = levelRecap.get(lvl);
                    if (info != null) {
                        if (scr == 7) {
                            info.completedCount++;
                        } else if (scr > 0) {
                            info.nonCompletedCount++;
                        } else if (scr == 0) {
                            info.abandonedCount++;
                        }
                    }
                }
            }
            sb.append(capitalize(diff)).append(":\n");
            boolean diffCompleted = true;
            for (int lvl = 1; lvl <= 3; lvl++) {
                RecapInfo info = levelRecap.get(lvl);
                String levelStatus = (info.completedCount > 0) ? "completato" : "non completato";
                if (info.completedCount == 0) {
                    diffCompleted = false;
                }
                sb.append("  Livello ").append(lvl).append(" - Stato: ").append(levelStatus).append("\n");
                sb.append("    Tentativi completati: ").append(info.completedCount).append("\n");
                sb.append("    Tentativi non completati: ").append(info.nonCompletedCount).append("\n");
                sb.append("    Tentativi abbandonati: ").append(info.abandonedCount).append("\n");
            }
            sb.append("Stato ").append(": ").append(diffCompleted ? "COMPLETATO" : "NON COMPLETATO").append("\n");
            sb.append("----------------------------------\n");
            if (!diffCompleted) {
                overallCompleted = false;
            }
        }
        sb.append("\nSTATO COMPLESSIVO: ").append(overallCompleted ? "COMPLETATO" : "NON COMPLETATO");
        return sb.toString();
    }
    
    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }
    
    private static class RecapInfo {
        int completedCount = 0;
        int nonCompletedCount = 0;
        int abandonedCount = 0;
    }
}
