package com.play.controller;

import com.play.model.Question;
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
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class QuizController {
    @FXML
    private Label questionText;
    @FXML
    private RadioButton option1;
    @FXML
    private RadioButton option2;
    @FXML
    private RadioButton option3;
    @FXML
    private RadioButton option4;
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

    private List<Question> questions;
    private int currentQuestionIndex = 0;
    private int score = 0;

    private ToggleGroup toggleGroup;
    private List<String> userAnswers;
    private Timeline timeline;
    private int secondsElapsed = 0;

    @FXML
    public void initialize() {
    	UserSession session = UserSession.getInstance();
        username = session.getUsername();

        toggleGroup = new ToggleGroup();
        option1.setToggleGroup(toggleGroup);
        option2.setToggleGroup(toggleGroup);
        option3.setToggleGroup(toggleGroup);
        option4.setToggleGroup(toggleGroup);

        userAnswers = new ArrayList<>();
    }

    public void loadExercise(String exerciseId, String difficulty) {
        this.exerciseId = exerciseId;
        this.difficulty = difficulty;

        questions = FileHandler.loadQuestions(exerciseId, difficulty);
        showQuestion();
        startTimer();
    }

    private void showQuestion() {
        if (currentQuestionIndex < questions.size() && currentQuestionIndex >= 0) {
            Question question = questions.get(currentQuestionIndex);
            questionText.setText(question.getQuestionText());
            option1.setText(question.getOption1());
            option2.setText(question.getOption2());
            option3.setText(question.getOption3());
            option4.setText(question.getOption4());
            String previousAnswer = userAnswers.size() > currentQuestionIndex ? userAnswers.get(currentQuestionIndex) : null;
            if (previousAnswer != null) {
                if (previousAnswer.equals(option1.getText()))
                	toggleGroup.selectToggle(option1);
                else if (previousAnswer.equals(option2.getText()))
                	toggleGroup.selectToggle(option2);
                else if (previousAnswer.equals(option3.getText()))
                	toggleGroup.selectToggle(option3);
                else if (previousAnswer.equals(option4.getText()))
                	toggleGroup.selectToggle(option4);
            } else {
                toggleGroup.selectToggle(null);
            }
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
        RadioButton selectedRadioButton = (RadioButton) toggleGroup.getSelectedToggle();
        if (selectedRadioButton != null) {
            String selectedOption = selectedRadioButton.getText();
            if (currentQuestionIndex < userAnswers.size()) {
                String previousAnswer = userAnswers.get(currentQuestionIndex);
                Question question = questions.get(currentQuestionIndex);
                if (question.getCorrectAnswer().equals(previousAnswer) && !question.getCorrectAnswer().equals(selectedOption)) {
                    score--;
                }
                else if (!question.getCorrectAnswer().equals(previousAnswer) && question.getCorrectAnswer().equals(selectedOption)) {
                    score++;
                }
                userAnswers.set(currentQuestionIndex, selectedOption);
            } else {
                userAnswers.add(selectedOption);
                Question question = questions.get(currentQuestionIndex);
                if (question.getCorrectAnswer().equals(selectedOption)) {
                    score++;
                }
            }
            scoreLabel.setText("Punteggio: " + score);
        }
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
