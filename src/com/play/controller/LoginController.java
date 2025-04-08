package com.play.controller;

import com.play.model.User;
import com.play.util.FileHandler;
import com.play.util.UserSession;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField createUsernameField;
    @FXML private PasswordField createPasswordField;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;

    @FXML
    public void handleLogin(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        try {
            List<User> users = FileHandler.readUsers();
            User loggedInUser = users.stream()
                    .filter(user -> user.getUsername().equals(username) && user.getPassword().equals(password))
                    .findFirst().orElse(null);

            if (loggedInUser != null) {
                UserSession session = UserSession.getInstance();
                session.setUsername(loggedInUser.getUsername());
                session.setFirstName(loggedInUser.getFirstName());
                session.setLastName(loggedInUser.getLastName());
                Stage stage = (Stage) usernameField.getScene().getWindow();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/play/view/homepage.fxml"));
                Parent root = loader.load();
                Scene scene = new Scene(root);
                scene.getStylesheets().add(getClass().getResource("/com/play/application.css").toExternalForm());
                stage.setScene(scene);
                stage.setTitle("Homepage");
                stage.show();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Autenticazione non riuscita");
                alert.setHeaderText(null);
                alert.setContentText("Username o password errati.");
                alert.showAndWait();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleCreateUser(ActionEvent event) {
        try {
            Stage stage = (Stage) usernameField.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/com/play/view/create_user.fxml"));
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/com/play/application.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Registrazione");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleCreateUserForm(ActionEvent event) {
        String username = createUsernameField.getText();
        String password = createPasswordField.getText();
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String email = emailField.getText();
        String phone = phoneField.getText();

        try {
            User newUser = new User(username, password, firstName, lastName, email, phone);
            FileHandler.writeUser(newUser);
            
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Registrazione avvenuta con successo");
            alert.setHeaderText(null);
            alert.setContentText("Utente creato.");
            alert.showAndWait();

            Stage stage = (Stage) createUsernameField.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/com/play/view/login.fxml"));
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/com/play/application.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Login");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Errore di registrazione");
            alert.setContentText("Si è verificato un errore durante la creazione dell'utente.");
            alert.showAndWait();
        }
    }
    
    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/play/view/login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) createUsernameField.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/com/play/application.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Login");
            stage.show();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}
