package com.play;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
// import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

  @Override
  public void start(Stage primaryStage) throws Exception {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/play/view/login.fxml"));
    Parent root = loader.load();
    Scene scene = new Scene(root);
    scene.getStylesheets().add(getClass().getResource("/com/play/application.css").toExternalForm());
    primaryStage.setTitle("PLAY");
    primaryStage.setScene(scene);
    // primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/icon.png")));
    primaryStage.show();
  }

  public static void main(String[] args) {
    launch(args);
  }
}
