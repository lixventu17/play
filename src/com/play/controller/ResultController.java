package com.play.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;

import java.io.IOException;

public class ResultController {

    @FXML
    private Label resultLabel;

    public void setResults(int score, int totalQuestions) {
        resultLabel.setText("Hai risposto correttamente a " + score + " domande su " + totalQuestions);
    }

    @FXML
    private void handleBackToHomepage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/play/view/homepage.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) resultLabel.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Homepage");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
