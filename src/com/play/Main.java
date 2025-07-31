package com.play;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

  @Override
  public void start(Stage primaryStage) throws Exception {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/play/view/login.fxml"));
    Parent root = loader.load();
    Scene scene = new Scene(root);
    
    // Carica il CSS con gestione errori
    try {
      java.net.URL cssUrl = getClass().getResource("/com/play/application.css");
      if (cssUrl != null) {
        scene.getStylesheets().add(cssUrl.toExternalForm());
      }
    } catch (Exception e) {
      System.err.println("Impossibile caricare il CSS: " + e.getMessage());
    }
    
    // Imposta l'icona dell'applicazione
    try {
      java.net.URL iconUrl = getClass().getResource("/com/play/images/play.png");
      if (iconUrl != null) {
        Image icon = new Image(iconUrl.toExternalForm());
        primaryStage.getIcons().add(icon);
      } else {
        System.err.println("Icona dell'applicazione non trovata: /com/play/images/play.png");
      }
    } catch (Exception e) {
      System.err.println("Impossibile caricare l'icona dell'applicazione: " + e.getMessage());
    }
    
    primaryStage.setTitle("PLAY");
    primaryStage.setScene(scene);
    primaryStage.setMaximized(true);
    primaryStage.show();
  }

  public static void main(String[] args) {
    launch(args);
  }
}
