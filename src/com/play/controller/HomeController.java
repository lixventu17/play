package com.play.controller;

import com.play.model.Exercise;
import com.play.util.FileHandler;
import com.play.util.UserSession;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;

import java.io.IOException;
import java.util.List;

public class HomeController {
    private Exercise exercise1;
    private Exercise exercise2;

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

    public void initialize() {
        initializeExercise1();
        initializeExercise2();
        
        UserSession session = UserSession.getInstance();
        String firstName = session.getFirstName();
        greetingLabel.setText("Ciao " + firstName + "!");

        StringBuilder title1 = new StringBuilder(exercise1.getTitle());
        title1.setCharAt(20, '\n');
        exerciseTitle1.setText(title1.toString());
        exerciseTitle1.setTextAlignment(TextAlignment.CENTER);
        exerciseImage1.setImage(new Image(getClass().getResourceAsStream(exercise1.getImagePath())));
        StringBuilder title2 = new StringBuilder(exercise2.getTitle());
        title2.setCharAt(22, '\n');
        exerciseTitle2.setText(title2.toString());
        exerciseTitle2.setTextAlignment(TextAlignment.CENTER);
        exerciseImage2.setImage(new Image(getClass().getResourceAsStream(exercise2.getImagePath())));
        
        String username = session.getUsername();
        List<String> progress = FileHandler.loadUserProgress(username);
        
        int completamenti1 = 0;
        int completamenti2 = 0;
        for (String rec : progress) {
            String[] parts = rec.split(";");
            if (parts.length >= 5) {
                if (parts[0].equalsIgnoreCase(exercise1.getId()) && parts[3].equals("7")) {
                    completamenti1++;
                } else if (parts[0].equalsIgnoreCase(exercise2.getId()) && parts[3].equals("7")) {
                    completamenti2++;
                }
            }
        }
        if (completamenti1 >= 9) {
        	completamenti1 = 9;
        	progressBarExercise1.setProgress(completamenti1 / 9.0);
        	Platform.runLater(() -> {
        	    Node track = progressBarExercise1.lookup(".track");
        	    if (track != null) {
        	        track.setStyle("-fx-background-color: green;");
        	    }
        	});
        	Image medal1 = new Image(getClass().getResource("/com/play/images/guarantee1.png").toExternalForm());
        	imageMedal1.setImage(medal1);
        	if (completamenti2 < 9) {
	        	Image medal2 = new Image(getClass().getResource("/com/play/images/guarantee2.png").toExternalForm());
	        	imageMedal2.setImage(medal2);
	        	imageMedal2.setVisible(false);
        	}
        }
        else {
        	progressBarExercise1.setProgress(completamenti1 / 9.0);
        }
        if (completamenti2 >= 9) {
        	completamenti2 = 9;
        	progressBarExercise2.setProgress(completamenti2 / 9.0);
        	Platform.runLater(() -> {
        	    Node track = progressBarExercise2.lookup(".track");
        	    if (track != null) {
        	        track.setStyle("-fx-background-color: green;");
        	    }
        	});
        	Image medal2 = new Image(getClass().getResource("/com/play/images/guarantee2.png").toExternalForm());
        	imageMedal2.setImage(medal2);
        	if (completamenti2 < 9) {
            	Image medal1 = new Image(getClass().getResource("/com/play/images/guarantee1.png").toExternalForm());
            	imageMedal1.setImage(medal1);
            	imageMedal1.setVisible(false);
        	}
        }
        else {
        	progressBarExercise2.setProgress(completamenti2 / 9.0);
        }
    }
    
    private void initializeExercise1() {
        exercise1 = new Exercise(
        	"exercise1",
            "Conoscenza e Lettura del codice Java",
            "Questo esercizio testa le tue conoscenze e ti sfida a interpretare il codice scritto, mettendo alla prova la tua capacità di riconoscere la logica e identificare eventuali anomalie. ",
            "Durante l'esercizio, le domande saranno a risposta multipla: scegli l'opzione corretta tra 2 o 4 proposte.",
            "/com/play/images/Exercise1.jpg"
        );
    }
    
    private void initializeExercise2() {
        exercise2 = new Exercise(
        	"exercise2",
            "Conoscenza e Scrittura del codice Java",
            "Questo esercizio testa le tue conoscenze e ti mette alla prova nella scrittura del codice, stimolandoti a produrre soluzioni corrette e funzionali.",
            "Durante l'esercizio, dovrai digitare direttamente la risposta: analizza attentamente la domanda e scrivi la soluzione, facendo attenzione alla sintassi e alla logica.",
            "/com/play/images/Exercise2.jpg"
        );
    }

    @FXML
    private void handleExercise1Click() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/play/view/exercise_progress.fxml"));
            Parent root = loader.load();
            // Passa l'exerciseId "exercise1" al controller di progress
            com.play.controller.ExerciseProgressController progressController = loader.getController();
            progressController.setExercise(exercise1);
            Stage stage = (Stage) exerciseImage1.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/com/play/application.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Progresso - " + exercise1.getTitle());
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleExercise2Click() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/play/view/exercise_progress.fxml"));
            Parent root = loader.load();
            com.play.controller.ExerciseProgressController progressController = loader.getController();
            progressController.setExercise(exercise2);
            Stage stage = (Stage) exerciseImage2.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/com/play/application.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Progresso - " + exercise2.getTitle());
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout() {
        UserSession.getInstance().clearSession();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/play/view/login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/com/play/application.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Login");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleProfile() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/play/view/profile.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) profileButton.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/com/play/application.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Profilo");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLeaderboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/play/view/leaderboard.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) leaderboardButton.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/com/play/application.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Classifica");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
