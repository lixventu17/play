package com.play.controller;

import com.play.model.CompletionQuestion;
import com.play.util.FileHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;
import java.util.List;

public class CompletionExerciseController {
    @FXML
    private Label questionText;
    @FXML
    private TextField answerField;
    @FXML
    private Button finishButton;
    @FXML
    private Label scoreLabel;

    private String exerciseId;
    private String difficulty;

    private List<CompletionQuestion> questions;
    private int currentQuestionIndex = 0;
    private int score = 0;

    public void loadExercise(String exerciseId, String difficulty) {
        this.exerciseId = exerciseId;
        this.difficulty = difficulty;

        questions = FileHandler.loadCompletionQuestions(exerciseId, difficulty);
        showQuestion();
    }

    private void showQuestion() {
        if (currentQuestionIndex < questions.size()) {
            CompletionQuestion question = questions.get(currentQuestionIndex);
            questionText.setText(question.getQuestionText());
            answerField.clear();
        } else {
            finishQuiz();
        }
    }

    @FXML
    private void handleSubmit() {
        checkAnswer();
        currentQuestionIndex++;
        showQuestion();
    }

    private void checkAnswer() {
        String userAnswer = answerField.getText().trim();
        CompletionQuestion question = questions.get(currentQuestionIndex);
        if (question.getCorrectAnswer().equalsIgnoreCase(userAnswer)) {
            score++;
        }
        scoreLabel.setText("Punteggio: " + score);
    }

    @FXML
    private void finishQuiz() {
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

//    @FXML
//    private void finishQuiz() {
//        boolean isCompleted = score == questions.size();
//        try {
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/play/view/result.fxml"));
//            Parent root = loader.load();
//            ResultController controller = loader.getController();
//            controller.setResult(isCompleted, exerciseId, difficulty);
//            Stage stage = (Stage) finishButton.getScene().getWindow();
//            stage.setScene(new Scene(root));
//            stage.show();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
}
