package com.play.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.play.model.CompletionQuestion;
import com.play.model.Exercise;
import com.play.util.FileHandler;
import com.play.util.UserSession;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Controller per la gestione degli esercizi di completamento.
 * Gestisce la visualizzazione delle domande, il timer, il punteggio e la navigazione tra le domande.
 * Estende la funzionalità base di un controller FXML.
 */
public class CompletionExerciseController extends BaseController {
	@FXML private Label startDateLabel;
    @FXML private Label timerLabel;
    @FXML private Label question;
    @FXML private Label text;
    @FXML private TextField answerField;
    @FXML private Button previousButton;
    @FXML private Button nextButton;
    @FXML private Button finishButton;
    @FXML private Label scoreLabel;

    private Exercise exercise;
    private String difficulty;
    private int level;
    private String username;

    private List<CompletionQuestion> questions;
    private int currentQuestionIndex = 0;
    private List<String> userAnswers;
    private int score = 0;
    private Timeline timeline;
    private int secondsElapsed = 0;

    /**
     * Inizializza il controller.
     * Configura la sessione utente e inizializza la lista delle risposte.
     */
    public void initialize() {
        try {
            UserSession session = UserSession.getInstance();
            username = session.getUsername();
            if (username == null || username.trim().isEmpty()) {
                throw new IllegalStateException("Username non valido");
            }
            userAnswers = new ArrayList<>();
        } catch (Exception e) {
            handleError("Errore di inizializzazione", "Impossibile inizializzare l'esercizio: " + e.getMessage());
        }
    }

    /**
     * Carica l'esercizio con le relative domande.
     * 
     * @param exercise L'esercizio da caricare
     * @param difficulty La difficoltà dell'esercizio
     * @param level Il livello dell'esercizio
     * @throws IllegalArgumentException se i parametri non sono validi
     */
    public void loadExercise(Exercise exercise, String difficulty, int level) {
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

        questions = FileHandler.loadCompletionQuestions(exercise.getId(), difficulty, level);
        if (questions == null || questions.isEmpty()) {
            handleError("Errore di caricamento", "Nessuna domanda disponibile per questo esercizio");
            return;
        }
        showQuestion();
        startTimer();
    }

    /**
     * Mostra la domanda corrente e gestisce la navigazione.
     */
    private void showQuestion() {
        try {
            // Se siamo alla fine dell'esercizio, termina
            if (currentQuestionIndex >= questions.size()) {
                finishQuiz();
                return;
            }

            // Se l'indice è negativo, mostra la prima domanda
            if (currentQuestionIndex < 0) {
                currentQuestionIndex = 0;
            }

            previousButton.setDisable(currentQuestionIndex == 0);
            nextButton.setText((currentQuestionIndex == questions.size() - 1) ? "Termina" : "Seguente");

            CompletionQuestion currentQuestion = questions.get(currentQuestionIndex);
            question.setText((currentQuestionIndex + 1) + ") " + currentQuestion.getQuestion());
            
            if (!currentQuestion.getText().trim().isEmpty()) {
                text.setText(currentQuestion.getText());
                text.setVisible(true);
            } else {
                text.setVisible(false);
            }
            
            answerField.setText(userAnswers.size() > currentQuestionIndex ? userAnswers.get(currentQuestionIndex) : "");
        } catch (Exception e) {
            handleError("Errore di visualizzazione", "Impossibile mostrare la domanda: " + e.getMessage());
        }
    }

    /**
     * Avvia il timer per l'esercizio.
     */
    private void startTimer() {
        try {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm:ss");
            startDateLabel.setText("Inizio: " + LocalDateTime.now().format(dtf));

            timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
                secondsElapsed++;
                updateTimerLabel();
            }));
            timeline.setCycleCount(Animation.INDEFINITE);
            timeline.play();
        } catch (Exception e) {
            handleError("Errore del timer", "Impossibile avviare il timer: " + e.getMessage());
        }
    }

    /**
     * Aggiorna l'etichetta del timer.
     */
    private void updateTimerLabel() {
        int minutes = secondsElapsed / 60;
        int seconds = secondsElapsed % 60;
        timerLabel.setText(String.format("Tempo: %02d:%02d", minutes, seconds));
    }

    /**
     * Gestisce l'invio della risposta e la navigazione.
     */
    @FXML
    private void handleSubmit() {
        try {
            if (currentQuestionIndex == questions.size() - 1) {
                showConfirmationDialog();
            } else {
                saveAnswer();
                currentQuestionIndex++;
                showQuestion();
            }
        } catch (Exception e) {
            handleError("Errore di navigazione", "Impossibile procedere alla domanda successiva: " + e.getMessage());
        }
    }

    /**
     * Mostra la finestra di dialogo di conferma per terminare l'esercizio.
     */
    private void showConfirmationDialog() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Conferma fine tentativo");
        alert.setHeaderText(null);
        alert.setContentText("Sei sicuro di voler terminare il tentativo?");
        alert.showAndWait().ifPresent(response -> {
            if (response == javafx.scene.control.ButtonType.OK) {
                try {
                    saveAnswer();
                    finishQuiz();
                } catch (Exception e) {
                    handleError("Errore di completamento", "Impossibile completare l'esercizio: " + e.getMessage());
                }
            }
        });
    }

    /**
     * Gestisce la navigazione alla domanda precedente.
     */
    @FXML
    private void handlePrevious() {
        if (currentQuestionIndex > 0) {
            saveAnswer();
            currentQuestionIndex--;
            showQuestion();
        }
    }

    /**
     * Salva la risposta dell'utente e aggiorna il punteggio.
     */
    private void saveAnswer() {
        try {
            String userAnswer = answerField.getText().trim();
            updateScore(userAnswer);
            updateUserAnswers(userAnswer);
            scoreLabel.setText("Punteggio: " + score);
        } catch (Exception e) {
            handleError("Errore di salvataggio", "Impossibile salvare la risposta: " + e.getMessage());
        }
    }

    /**
     * Aggiorna il punteggio in base alla risposta.
     * 
     * @param userAnswer La risposta dell'utente
     */
    private void updateScore(String userAnswer) {
        if (currentQuestionIndex < userAnswers.size()) {
            String previousAnswer = userAnswers.get(currentQuestionIndex);
            CompletionQuestion question = questions.get(currentQuestionIndex);
            if (question.getCorrectAnswer().equalsIgnoreCase(previousAnswer) && 
                !question.getCorrectAnswer().equalsIgnoreCase(userAnswer)) {
                score--;
            } else if (!question.getCorrectAnswer().equalsIgnoreCase(previousAnswer) && 
                      question.getCorrectAnswer().equalsIgnoreCase(userAnswer)) {
                score++;
            }
        } else {
            CompletionQuestion question = questions.get(currentQuestionIndex);
            if (question.getCorrectAnswer().equalsIgnoreCase(userAnswer)) {
                score++;
            }
        }
    }

    /**
     * Aggiorna la lista delle risposte dell'utente.
     * 
     * @param userAnswer La risposta dell'utente
     */
    private void updateUserAnswers(String userAnswer) {
        if (currentQuestionIndex < userAnswers.size()) {
            userAnswers.set(currentQuestionIndex, userAnswer);
        } else {
            userAnswers.add(userAnswer);
        }
    }

    /**
     * Gestisce il ritorno alla homepage.
     */
    @FXML
    private void handleBackToHomepage() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Conferma abbandono tentativo");
        alert.setHeaderText(null);
        alert.setContentText("Sei sicuro di voler abbandonare il tentativo?");
        alert.showAndWait().ifPresent(response -> {
            if (response == javafx.scene.control.ButtonType.OK) {
                stopTimer();
                saveAbandonedAttempt();
                navigateToHomepage();
            }
        });
    }

    /**
     * Ferma il timer.
     */
    private void stopTimer() {
        if (timeline != null) {
            timeline.stop();
        }
    }

    /**
     * Salva il tentativo abbandonato.
     */
    private void saveAbandonedAttempt() {
        try {
            int attempts = fetchAttempts();
            FileHandler.saveUserProgress(username, exercise.getId(), difficulty, level, 0, 0, attempts, 0);
        } catch (Exception e) {
            handleError("Errore di salvataggio", "Impossibile salvare il tentativo abbandonato: " + e.getMessage());
        }
    }

    /**
     * Naviga alla homepage.
     */
    private void navigateToHomepage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/play/view/homepage.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) finishButton.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/com/play/application.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Homepage");
            stage.show();
        } catch (IOException e) {
            handleError("Errore di navigazione", "Impossibile tornare alla homepage: " + e.getMessage());
        }
    }

    /**
     * Termina l'esercizio e mostra i risultati.
     */
    @FXML
    private void finishQuiz() {
        try {
            stopTimer();
            int attempts = fetchAttempts();
            int total = calculateTotal(attempts);
            FileHandler.saveUserProgress(username, exercise.getId(), difficulty, level, score, secondsElapsed, attempts, total);
            showResults();
        } catch (Exception e) {
            handleError("Errore di completamento", "Impossibile completare l'esercizio: " + e.getMessage());
        }
    }

    /**
     * Mostra la pagina dei risultati.
     */
    private void showResults() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/play/view/result.fxml"));
            Parent root = loader.load();
            ResultController resultController = loader.getController();
            resultController.setResult(score == 7, exercise, difficulty, level, score, secondsElapsed);
            
            Stage stage = (Stage) finishButton.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/com/play/application.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Risultato - " + exercise.getTitle() + " (" + capitalize(difficulty) + " - Livello " + level + ")");
            stage.show();
        } catch (IOException e) {
            handleError("Errore di visualizzazione", "Impossibile mostrare i risultati: " + e.getMessage());
        }
    }

    /**
     * Recupera il numero di tentativi precedenti.
     * 
     * @return Il numero di tentativi
     */
    private int fetchAttempts() {
        try {
            List<String> userProgress = FileHandler.loadUserProgress(username);
            Collections.reverse(userProgress);
            for (String line : userProgress) {
                String[] parts = line.split(";");
                if (parts[0].equalsIgnoreCase(exercise.getId()) && 
                    parts[1].equalsIgnoreCase(difficulty) && 
                    Integer.parseInt(parts[2]) == level) {
                    return Integer.parseInt(parts[5]) + 1;
                }
            }
            return 0;
        } catch (Exception e) {
            handleError("Errore di recupero", "Impossibile recuperare i tentativi: " + e.getMessage());
            return 0;
        }
    }

    /**
     * Calcola il punteggio totale in base al tempo e al punteggio.
     * 
     * @param attempts Il numero di tentativi
     * @return Il punteggio totale
     */
    private int calculateTotal(int attempts) {
        int total = calculateTimeScore();
        total = applyScorePenalty(total);
        total = applyAttemptsPenalty(total, attempts);
        return total;
    }

    /**
     * Calcola il punteggio basato sul tempo.
     * 
     * @return Il punteggio per il tempo
     */
    private int calculateTimeScore() {
        return (secondsElapsed <= 1800) ? (1800 - secondsElapsed) : 0;
    }

    /**
     * Applica la penalità basata sul punteggio.
     * 
     * @param total Il punteggio totale corrente
     * @return Il punteggio dopo l'applicazione della penalità
     */
    private int applyScorePenalty(int total) {
        int[] penalties = {1400, 1200, 1000, 800, 600, 400, 200, 0};
        int penalty = penalties[Math.min(score, 7)];
        return (total >= penalty) ? (total - penalty) : 0;
    }

    /**
     * Applica la penalità basata sul numero di tentativi.
     * 
     * @param total Il punteggio totale corrente
     * @param attempts Il numero di tentativi
     * @return Il punteggio dopo l'applicazione della penalità
     */
    private int applyAttemptsPenalty(int total, int attempts) {
        if (attempts == 0) return total;
        int penaltyAttempts = 15 * attempts;
        return (total >= penaltyAttempts) ? (total - penaltyAttempts) : 0;
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
}
