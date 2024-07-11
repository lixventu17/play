package com.play.controller;

import com.play.model.CompletionQuestion;
import com.play.util.FileHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;

import java.util.List;

public class CompletionExerciseController {

    @FXML
    private Label sentenceLabel;
    
    @FXML
    private TextField answerField;
    
    @FXML
    private Label feedbackLabel;
    
    @FXML
    private Button nextButton;

    private List<CompletionQuestion> questions;
    private int currentQuestionIndex = 0;

    public void loadExercise(String exerciseId, String difficulty) {
        String filePath = "resources/com/play/questions/" + exerciseId + "_" + difficulty + ".txt";
        questions = FileHandler.loadCompletionQuestions(filePath);
        if (questions != null && !questions.isEmpty()) {
            displayQuestion(questions.get(currentQuestionIndex));
        } else {
            sentenceLabel.setText("Nessuna domanda trovata per questo esercizio.");
        }
    }

    private void displayQuestion(CompletionQuestion question) {
        sentenceLabel.setText(question.getSentence());
        answerField.setText("");
        feedbackLabel.setText("");
    }

    @FXML
    private void handleCheckAnswer() {
        String userAnswer = answerField.getText().trim();
        CompletionQuestion currentQuestion = questions.get(currentQuestionIndex);
        if (userAnswer.equalsIgnoreCase(currentQuestion.getCorrectAnswer())) {
            feedbackLabel.setText("Corretto!");
        } else {
            feedbackLabel.setText("Risposta sbagliata. La risposta corretta era: " + currentQuestion.getCorrectAnswer());
        }
    }

    @FXML
    private void handleNextQuestion() {
        if (currentQuestionIndex < questions.size() - 1) {
            currentQuestionIndex++;
            displayQuestion(questions.get(currentQuestionIndex));
        } else {
            sentenceLabel.setText("Hai completato l'esercizio!");
            nextButton.setDisable(true);
        }
    }
}
