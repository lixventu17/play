package com.play.controller;

import com.play.model.Question;
import com.play.util.FileHandler;
import com.play.util.QuizManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;
import java.util.List;

public class QuizController {
    @FXML
    private Label questionText;
    @FXML
    private Label option1;
    @FXML
    private Label option2;
    @FXML
    private Label option3;
    @FXML
    private Label option4;
    @FXML
    private Button finishButton;

    private String exerciseId;
    private String difficulty;

    private List<Question> questions;
    private int currentQuestionIndex = 0;

    public void loadExercise(String exerciseId, String difficulty) {
        this.exerciseId = exerciseId;
        this.difficulty = difficulty;

        questions = FileHandler.loadQuestions(exerciseId, difficulty);
        QuizManager.setQuestions(questions);
        showQuestion();
    }

    private void showQuestion() {
        if (currentQuestionIndex < questions.size()) {
            Question question = questions.get(currentQuestionIndex);
            questionText.setText(question.getQuestionText());
            option1.setText(question.getOption1());
            option2.setText(question.getOption2());
            option3.setText(question.getOption3());
            option4.setText(question.getOption4());
        } else {
            finishQuiz();
        }
    }

    @FXML
    private void handleOption1() {
        checkAnswer(option1.getText());
    }

    @FXML
    private void handleOption2() {
        checkAnswer(option2.getText());
    }

    @FXML
    private void handleOption3() {
        checkAnswer(option3.getText());
    }

    @FXML
    private void handleOption4() {
        checkAnswer(option4.getText());
    }

    private void checkAnswer(String selectedOption) {
        Question question = questions.get(currentQuestionIndex);
        if (question.getCorrectAnswer().equals(selectedOption)) {
            QuizManager.incrementCorrectAnswers();
        }
        currentQuestionIndex++;
        showQuestion();
    }

    @FXML
    private void finishQuiz() {
        boolean isCompleted = QuizManager.isExerciseCompleted100();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/play/view/result.fxml"));
            Parent root = loader.load();
            ResultController controller = loader.getController();
            controller.setResult(isCompleted, exerciseId, difficulty);
            Stage stage = (Stage) finishButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
