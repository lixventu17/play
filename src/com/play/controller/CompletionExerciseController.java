package com.play.controller;

import com.play.model.CompletionQuestion;
import com.play.model.Exercise;
import com.play.util.FileHandler;
import com.play.util.UserSession;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CompletionExerciseController {
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

    public void initialize() {
        UserSession session = UserSession.getInstance();
        username = session.getUsername();
        userAnswers = new ArrayList<>();
    }

    public void loadExercise(Exercise exercise, String difficulty, int level) {
        this.exercise = exercise;
        this.difficulty = difficulty;
        this.level = level;

        questions = FileHandler.loadCompletionQuestions(exercise.getId(), difficulty, level);
        if (questions == null || questions.isEmpty()) {
            question.setText("Nessuna domanda disponibile.");
            return;
        }
        showQuestion();
        startTimer();
    }

    private void showQuestion() {
        if (currentQuestionIndex < questions.size() && currentQuestionIndex >= 0) {
            previousButton.setDisable(currentQuestionIndex == 0);
            if (questions != null && !questions.isEmpty()) {
                nextButton.setText((currentQuestionIndex == questions.size() - 1) ? "Termina" : "Seguente");
            }

            CompletionQuestion question = questions.get(currentQuestionIndex);
            this.question.setText((currentQuestionIndex + 1) + ") " + question.getQuestion());
            if (!question.getText().trim().isEmpty()) {
            	text.setText(question.getText());
            }
            else {
            	text.setVisible(false);
            }
            answerField.setText(userAnswers.size() > currentQuestionIndex ? userAnswers.get(currentQuestionIndex) : "");
        } else if (currentQuestionIndex >= questions.size()) {
            finishQuiz();
        }
    }

    private void startTimer() {
    	DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm:ss");
    	startDateLabel.setText("Inizio: " + LocalDateTime.now().format(dtf));

        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            secondsElapsed++;
            updateTimerLabel();
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    private void updateTimerLabel() {
        int minutes = secondsElapsed / 60;
        int seconds = secondsElapsed % 60;
        timerLabel.setText(String.format("Tempo: %02d:%02d", minutes, seconds));
    }

    @FXML
    private void handleSubmit() {
    	// Se siamo all'ultima domanda, chiedi conferma per terminare il tentativo
        if (currentQuestionIndex == questions.size() - 1) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Conferma fine tentativo");
            alert.setHeaderText(null);
            alert.setContentText("Sei sicuro di voler terminare il tentativo?");
            alert.showAndWait().ifPresent(response -> {
                if (response == javafx.scene.control.ButtonType.OK) {
                	saveAnswer();
                    currentQuestionIndex++;
                    showQuestion();
                }
            });
        } else {
            // Altrimenti, prosegui normalmente
            saveAnswer();
            currentQuestionIndex++;
            showQuestion();
        }
    }

    @FXML
    private void handlePrevious() {
        if (currentQuestionIndex > 0) {
            saveAnswer();
            currentQuestionIndex--;
            showQuestion();
        }
    }

    private void saveAnswer() {
        String userAnswer = answerField.getText().trim();
        if (currentQuestionIndex < userAnswers.size()) {
            String previousAnswer = userAnswers.get(currentQuestionIndex);
            CompletionQuestion question = questions.get(currentQuestionIndex);
            if (question.getCorrectAnswer().equalsIgnoreCase(previousAnswer) && !question.getCorrectAnswer().equalsIgnoreCase(userAnswer)) {
                score--;
            } else if (!question.getCorrectAnswer().equalsIgnoreCase(previousAnswer) && question.getCorrectAnswer().equalsIgnoreCase(userAnswer)) {
                score++;
            }
            userAnswers.set(currentQuestionIndex, userAnswer);
        } else {
            userAnswers.add(userAnswer);
            CompletionQuestion question = questions.get(currentQuestionIndex);
            if (question.getCorrectAnswer().equalsIgnoreCase(userAnswer)) {
                score++;
            }
        }
        scoreLabel.setText("Punteggio: " + score);
    }

    @FXML
    private void handleBackToHomepage() {
    	Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Conferma abbandono tentativo");
        alert.setHeaderText(null);
        alert.setContentText("Sei sicuro di voler abbandonare il tentativo?");
        alert.showAndWait().ifPresent(response -> {
            if (response == javafx.scene.control.ButtonType.OK) {
		        timeline.stop();
		        int attempts = fetchAttempts();
		        FileHandler.saveUserProgress(username, exercise.getId(), difficulty, level, 0, 0, attempts, 0);
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
		            e.printStackTrace();
		        }
            }
        });
    }

    @FXML
    private void finishQuiz() {
        timeline.stop();
        int attempts = fetchAttempts();
        int total = calculateTotal(attempts);
        FileHandler.saveUserProgress(username, exercise.getId(), difficulty, level, score, secondsElapsed, attempts, total);

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/play/view/result.fxml"));
            Parent root = loader.load();
            ResultController resultController = loader.getController();
            if (score == 7) {
                resultController.setResult(true, exercise, difficulty, level, score, secondsElapsed);
            } else {
                resultController.setResult(false, exercise, difficulty, level, score, secondsElapsed);
            }
            Stage stage = (Stage) finishButton.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/com/play/application.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Risultato - " + exercise.getTitle() + " (" + (Character.toUpperCase(difficulty.charAt(0)) + difficulty.substring(1)) + " - Livello " + level + ")");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int fetchAttempts() {
    	List<String> userProgress = FileHandler.loadUserProgress(username);
        Collections.reverse(userProgress);
        int attempts = 0;
        for (String line : userProgress) {
        	String[] parts = line.split(";");
        	if (parts[0].equalsIgnoreCase(exercise.getId())) {
        		if (parts[1].equalsIgnoreCase(difficulty)) {
        			if (Integer.parseInt(parts[2]) == level) {
        				attempts = Integer.parseInt(parts[5]) + 1;
        				break;
        			}
        		}
        	}
        }
        return attempts;
    }

    private int calculateTotal(int attempts) {
    	int total = 0;
        if (secondsElapsed <= 1800) {
    		total += (1800 - secondsElapsed);
    	}
    	else {
    		total = 0;
    	}
    	if (score == 7) {
    		total += 200;
    	}
    	else if (score == 6) {
    		if (total >= 200) {
    			total -= 200;
    		}
        	else {
        		total = 0;
        	}
    	}
    	else if (score == 5) {
    		if (total >= 400) {
    			total -= 400;
    		}
        	else {
        		total = 0;
        	}
    	}
    	else if (score == 4) {
    		if (total >= 600) {
    			total -= 600;
    		}
        	else {
        		total = 0;
        	}
    	}
    	else if (score == 3) {
    		if (total >= 800) {
    			total -= 800;
    		}
        	else {
        		total = 0;
        	}
    	}
    	else if (score == 2) {
    		if (total >= 1000) {
    			total -= 1000;
    		}
        	else {
        		total = 0;
        	}
    	}
    	else if (score == 1) {
    		if (total >= 1200) {
    			total -= 1200;
    		}
        	else {
        		total = 0;
        	}
    	}
    	else if (score == 0) {
    		if (total >= 1400) {
    			total -= 1400;
    		}
        	else {
        		total = 0;
        	}
    	}

        if (attempts != 0) {
        	int penaltyAttempts = 15 * attempts;
        	if (total >= penaltyAttempts) {
        		total -= penaltyAttempts;
        	}
        	else {
        		total = 0;
        	}
        }
        return total;
    }
}
