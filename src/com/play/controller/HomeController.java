package com.play.controller;

import java.io.IOException;
import java.util.List;

import com.play.model.Exercise;
import com.play.util.FileHandler;
import com.play.util.UserSession;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

/**
 * Controller per la gestione della homepage dell'applicazione.
 * Gestisce la visualizzazione degli esercizi, il progresso dell'utente e la navigazione tra le varie sezioni.
 */
public class HomeController extends BaseController {
    // Esercizi disponibili
    private Exercise exercise1;
    private Exercise exercise2;

    // Componenti FXML per il form di login
    @FXML private Label greetingLabel;
    @FXML private ImageView exerciseImage1;
    @FXML private ImageView exerciseImage2;
    @FXML private Label exerciseTitle1;
    @FXML private Label exerciseTitle2;
    @FXML private ProgressBar progressBarExercise1;
    @FXML private ProgressBar progressBarExercise2;
    @FXML private ImageView imageMedal1;
    @FXML private ImageView imageMedal2;
    @FXML private Button logoutButton;
    @FXML private Button profileButton;
    @FXML private Button leaderboardButton;

    /**
     * Inizializza la homepage con i dati dell'utente e degli esercizi.
     * Viene chiamato automaticamente dopo il caricamento del FXML.
     */
    public void initialize() {
        try {
            initializeExercises();
            setupUserGreeting();
            setupExerciseDisplay();
            loadAndDisplayProgress();
        } catch (Exception e) {
        	handleError("Errore di inizializzazione", "Impossibile caricare la homepage: " + e.getMessage());
        }
    }

    /**
     * Inizializza gli esercizi disponibili.
     */
    private void initializeExercises() {
        initializeExercise1();
        initializeExercise2();
    }

    /**
     * Configura il messaggio di benvenuto per l'utente.
     */
    private void setupUserGreeting() {
        UserSession session = UserSession.getInstance();
        String firstName = session.getFirstName();
        greetingLabel.setText("Ciao " + firstName + "!");
    }

    /**
     * Configura la visualizzazione degli esercizi.
     */
    private void setupExerciseDisplay() {
        // Configurazione esercizio 1
        StringBuilder title1 = new StringBuilder(exercise1.getTitle());
        title1.setCharAt(20, '\n');
        exerciseTitle1.setText(title1.toString());
        exerciseTitle1.setTextAlignment(TextAlignment.CENTER);
        
        // Carica immagine esercizio 1 con gestione errori
        try {
            java.io.InputStream imageStream1 = getClass().getResourceAsStream(exercise1.getImagePath());
            if (imageStream1 != null) {
                exerciseImage1.setImage(new Image(imageStream1));
            } else {
                System.err.println("Immagine non trovata: " + exercise1.getImagePath());
            }
        } catch (Exception e) {
            System.err.println("Errore caricamento immagine esercizio 1: " + e.getMessage());
        }

        // Configurazione esercizio 2
        StringBuilder title2 = new StringBuilder(exercise2.getTitle());
        title2.setCharAt(22, '\n');
        exerciseTitle2.setText(title2.toString());
        exerciseTitle2.setTextAlignment(TextAlignment.CENTER);
        
        // Carica immagine esercizio 2 con gestione errori
        try {
            java.io.InputStream imageStream2 = getClass().getResourceAsStream(exercise2.getImagePath());
            if (imageStream2 != null) {
                exerciseImage2.setImage(new Image(imageStream2));
            } else {
                System.err.println("Immagine non trovata: " + exercise2.getImagePath());
            }
        } catch (Exception e) {
            System.err.println("Errore caricamento immagine esercizio 2: " + e.getMessage());
        }
    }

    /**
     * Carica e visualizza il progresso dell'utente per entrambi gli esercizi.
     */
    private void loadAndDisplayProgress() {
        String username = UserSession.getInstance().getUsername();
        List<String> progress = FileHandler.loadUserProgress(username);

        int completions1 = calculateCompletions(progress, exercise1.getId());
        int completions2 = calculateCompletions(progress, exercise2.getId());

        updateExerciseProgress(completions1, completions2);
    }

    /**
     * Calcola il numero di completamenti per un esercizio specifico.
     * 
     * @param progress Lista dei record di progresso
     * @param exerciseId ID dell'esercizio
     * @return numero di completamenti
     */
    private int calculateCompletions(List<String> progress, String exerciseId) {
        return (int) progress.stream()
                .filter(rec -> {
                    String[] parts = rec.split(";");
                    return parts.length >= 5 && 
                           parts[0].equalsIgnoreCase(exerciseId) && 
                           parts[3].equals("7");
                })
                .count();
    }

    /**
     * Aggiorna la visualizzazione del progresso per entrambi gli esercizi.
     * 
     * @param completions1 Completamenti dell'esercizio 1
     * @param completions2 Completamenti dell'esercizio 2
     */
    private void updateExerciseProgress(int completions1, int completions2) {
        updateSingleExerciseProgress(completions1, progressBarExercise1, imageMedal1, true);
        updateSingleExerciseProgress(completions2, progressBarExercise2, imageMedal2, false);
    }

    /**
     * Aggiorna la visualizzazione del progresso per un singolo esercizio.
     */
    private void updateSingleExerciseProgress(int completions, ProgressBar progressBar, 
            ImageView medalImage, boolean isFirstExercise) {
        if (completions >= 9) {
            completions = 9;
            progressBar.setProgress(completions / 9.0);
            setProgressBarColor(progressBar, "green");

            String medalPath = isFirstExercise ? 
                "/com/play/images/guarantee1.png" : 
                "/com/play/images/guarantee2.png";
            
            // Carica medaglia con gestione errori
            try {
                java.net.URL medalUrl = getClass().getResource(medalPath);
                if (medalUrl != null) {
                    medalImage.setImage(new Image(medalUrl.toExternalForm()));
                } else {
                    System.err.println("Medaglia non trovata: " + medalPath);
                }
            } catch (Exception e) {
                System.err.println("Errore caricamento medaglia: " + e.getMessage());
            }
        } else {
            progressBar.setProgress(completions / 9.0);
            // Mostra un'immagine trasparente per mantenere l'allineamento
            try {
                java.net.URL transparentUrl = getClass().getResource("/com/play/images/transparent.png");
                if (transparentUrl != null) {
                    medalImage.setImage(new Image(transparentUrl.toExternalForm()));
                } else {
                    System.err.println("Immagine trasparente non trovata");
                }
            } catch (Exception e) {
                System.err.println("Errore caricamento immagine trasparente: " + e.getMessage());
            }
        }
    }

    /**
     * Imposta il colore della progress bar.
     */
    private void setProgressBarColor(ProgressBar progressBar, String color) {
        Platform.runLater(() -> {
            Node track = progressBar.lookup(".track");
            if (track != null) {
                track.setStyle("-fx-background-color: " + color + ";");
            }
        });
    }

    /**
     * Inizializza il primo esercizio.
     */
    private void initializeExercise1() {
        exercise1 = new Exercise(
            "exercise1",
            "Conoscenza e Lettura del codice Java",
            "Questo esercizio testa le tue conoscenze e ti sfida a interpretare il codice scritto, mettendo alla prova la tua capacità di riconoscere la logica e identificare eventuali anomalie. ",
            "Durante l'esercizio, le domande saranno a risposta multipla: scegli l'opzione corretta tra 2 o 4 proposte.",
            "/com/play/images/Exercise1.jpg"
        );
    }

    /**
     * Inizializza il secondo esercizio.
     */
    private void initializeExercise2() {
        exercise2 = new Exercise(
            "exercise2",
            "Conoscenza e Scrittura del codice Java",
            "Questo esercizio testa le tue conoscenze e ti mette alla prova nella scrittura del codice, stimolandoti a produrre soluzioni corrette e funzionali.",
            "Durante l'esercizio, dovrai digitare direttamente la risposta: analizza attentamente la domanda e scrivi la soluzione, facendo attenzione alla sintassi e alla logica.",
            "/com/play/images/Exercise2.jpg"
        );
    }

    /**
     * Gestisce il click sull'esercizio 1.
     */
    @FXML
    private void handleExercise1Click() {
        navigateToExerciseProgress(exercise1, exerciseImage1);
    }

    /**
     * Gestisce il click sull'esercizio 2.
     */
    @FXML
    private void handleExercise2Click() {
        navigateToExerciseProgress(exercise2, exerciseImage2);
    }

    /**
     * Naviga alla pagina del progresso dell'esercizio.
     * 
     * @param exercise L'esercizio da visualizzare
     * @param sourceNode Il nodo che ha triggerato la navigazione
     */
    private void navigateToExerciseProgress(Exercise exercise, Node sourceNode) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/play/view/exercise_progress.fxml"));
            Parent root = loader.load();
            ExerciseProgressController progressController = loader.getController();
            progressController.setExercise(exercise);
            
            Stage stage = (Stage) sourceNode.getScene().getWindow();
            
            Scene scene = new Scene(root);
            
            // Carica il CSS con gestione errori
            try {
                java.net.URL cssUrl = getClass().getResource("/com/play/application.css");
                if (cssUrl != null) {
                    scene.getStylesheets().add(cssUrl.toExternalForm());
                }
            } catch (Exception e) {
                System.err.println("Impossibile caricare il CSS: " + e.getMessage());
            }
            
            stage.setScene(scene);
            stage.setTitle("Progresso - " + exercise.getTitle());
            
            // Forza il fullscreen in modo più robusto
            forceFullscreen(stage);
            
            stage.show();
                } catch (IOException e) {
            handleError("Errore di navigazione", "Impossibile aprire la pagina del progresso: " + e.getMessage());
        }
    }
    
    /**
     * Forza la finestra a occupare tutto lo schermo in modo robusto.
     * 
     * @param stage La finestra da impostare a fullscreen
     */
    private void forceFullscreen(Stage stage) {
        // Usa Platform.runLater per assicurarsi che venga eseguito dopo che la scena è stata impostata
        javafx.application.Platform.runLater(() -> {
            try {
                // Prima prova a massimizzare
                stage.setMaximized(true);
                
                // Poi imposta anche le dimensioni esatte dello schermo
                javafx.stage.Screen screen = javafx.stage.Screen.getPrimary();
                javafx.geometry.Rectangle2D bounds = screen.getVisualBounds();
                
                stage.setX(bounds.getMinX());
                stage.setY(bounds.getMinY());
                stage.setWidth(bounds.getWidth());
                stage.setHeight(bounds.getHeight());
                
                // Assicurati che la finestra sia sempre in primo piano
                stage.setAlwaysOnTop(true);
                stage.setAlwaysOnTop(false);
                
            } catch (Exception e) {
                System.err.println("Errore nell'impostazione del fullscreen: " + e.getMessage());
            }
        });
    }

    /**
     * Gestisce il logout dell'utente.
     */
    @FXML
    private void handleLogout() {
        UserSession.getInstance().clearSession();
        navigateToPage("/com/play/view/login.fxml", logoutButton, "Login");
    }

    /**
     * Gestisce la navigazione al profilo utente.
     */
    @FXML
    private void handleProfile() {
        navigateToPage("/com/play/view/profile.fxml", profileButton, "Profilo");
    }

    /**
     * Gestisce la navigazione alla classifica.
     */
    @FXML
    private void handleLeaderboard() {
        navigateToPage("/com/play/view/leaderboard.fxml", leaderboardButton, "Classifica");
    }
}

