package com.play.controller;

import com.play.model.Question;
import com.play.util.QuizManager;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class QuizController {

    @FXML
    private Label questionLabel;
    @FXML
    private RadioButton option1;
    @FXML
    private RadioButton option2;
    @FXML
    private RadioButton option3;
    @FXML
    private RadioButton option4;
    @FXML
    private Button nextButton;
    @FXML
    private Button exitButton;
    @FXML
    private Label scoreLabel;
    
    private ToggleGroup optionsGroup;
    private QuizManager quizManager;
    private String exerciseId;
    private String difficulty;

    public void initialize() {
        optionsGroup = new ToggleGroup();
        option1.setToggleGroup(optionsGroup);
        option2.setToggleGroup(optionsGroup);
        option3.setToggleGroup(optionsGroup);
        option4.setToggleGroup(optionsGroup);
    }

    public void setExerciseDetails(String exerciseId, String difficulty) {
        this.exerciseId = exerciseId;
        this.difficulty = difficulty;

        quizManager = new QuizManager(exerciseId, difficulty);
        loadQuestion(quizManager.getCurrentQuestion());
        updateScore();
    }

    private void loadQuestion(Question question) {
        questionLabel.setText(question.getQuestion());
        List<String> options = question.getOptions();
        if (options.size() >= 4) {
            option1.setText(options.get(0));
            option2.setText(options.get(1));
            option3.setText(options.get(2));
            option4.setText(options.get(3));

            ToggleGroup group = option1.getToggleGroup();
            if (group != null) {
            	group.selectToggle(null);
            }
        } else {
            System.out.println("Error: Each question must have exactly four options.");
        }
    }

    private void updateScore() {
        scoreLabel.setText("Punteggio: " + quizManager.getScore());
    }

    @FXML
    private void handleNextQuestion() {
        RadioButton selectedOption = (RadioButton) optionsGroup.getSelectedToggle();
        if (selectedOption != null) {
            String answer = selectedOption.getText();
            quizManager.submitAnswer(answer);
            if (quizManager.hasNextQuestion()) {
                loadQuestion(quizManager.nextQuestion());
            } else {
                showResult();
            }
            updateScore();
        }
    }

    private void showResult() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/play/view/result.fxml"));
            Parent root = loader.load();

            ResultController controller = loader.getController();
            controller.setResults(quizManager.getScore(), quizManager.getTotalQuestions());

            Stage stage = (Stage) questionLabel.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Result");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleExit() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Exit");
        alert.setHeaderText("You are about to exit the exercise.");
        alert.setContentText("If you exit now, the exercise will be marked as failed. Do you want to proceed?");

        ButtonType yesButton = new ButtonType("Yes");
        ButtonType noButton = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(yesButton, noButton);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == yesButton) {
            // Save progress as failure and exit
            quizManager.saveProgress(false);  // Save as failed
            navigateToHomepage();
        }
    }

	private void navigateToHomepage() {
        quizManager.saveProgress(false);

	    try {
	        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/play/view/homepage.fxml"));
	        Parent root = loader.load();
	        Stage stage = (Stage) questionLabel.getScene().getWindow();
	        Scene scene = new Scene(root);
	        stage.setScene(scene);
	        stage.setTitle("Homepage");
	        stage.show();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
}
