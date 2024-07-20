package com.play.controller;

import com.play.model.CompletionQuestion;
import com.play.util.FileHandler;
import com.play.util.UserSession;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CompletionExerciseController {
    @FXML
    private Label questionText;
    @FXML
    private TextField answerField;
    @FXML
    private Button finishButton;
    @FXML
    private Button previousButton;
    @FXML
    private Label scoreLabel;
    @FXML
    private Label timerLabel;

    private String exerciseId;
    private String difficulty;
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

    public void loadExercise(String exerciseId, String difficulty) {
        this.exerciseId = exerciseId;
        this.difficulty = difficulty;

        questions = FileHandler.loadCompletionQuestions(exerciseId, difficulty);
        showQuestion();
        startTimer();
    }

    private void showQuestion() {
        if (currentQuestionIndex < questions.size() && currentQuestionIndex >= 0) {
            CompletionQuestion question = questions.get(currentQuestionIndex);
            questionText.setText(question.getQuestionText());
            answerField.setText(userAnswers.size() > currentQuestionIndex ? userAnswers.get(currentQuestionIndex) : "");
        } else if (currentQuestionIndex >= questions.size()) {
            finishQuiz();
        }
    }

    private void startTimer() {
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
    	saveAnswer();
        currentQuestionIndex++;
        showQuestion();
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
            }
            else if (!question.getCorrectAnswer().equalsIgnoreCase(previousAnswer) && question.getCorrectAnswer().equalsIgnoreCase(userAnswer)) {
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
        timeline.stop();
        FileHandler.saveUserProgress(username, exerciseId, difficulty, 0, 0);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/play/view/homepage.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) finishButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Homepage");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void finishQuiz() {
        timeline.stop();
        // Save progress
        FileHandler.saveUserProgress(username, exerciseId, difficulty, score, secondsElapsed);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/play/view/result.fxml"));
            Parent root = loader.load();
            ResultController resultController = loader.getController();
            if (score == 10) {
                resultController.setResult(true, exerciseId, difficulty, score, secondsElapsed);
            }
            else {
                resultController.setResult(false, exerciseId, difficulty, score, secondsElapsed);
            }
            Scene scene = new Scene(root);
            Stage stage = (Stage) finishButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Risultato");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
