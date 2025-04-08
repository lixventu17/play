package com.play.controller;

import com.play.model.User;
import com.play.util.FileHandler;
import com.play.util.UserSession;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import java.io.IOException;

public class ProfileController {
    @FXML private ImageView profileImageView;
    @FXML private Button chooseImageButton;
    @FXML private TextField usernameField;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private CheckBox autoSendCheckBox;
    @FXML private Label statusLabel;
    @FXML private Button saveButton;
    
    private User currentUser;
    
    public void initialize() {
        // Carica i dati dell'utente dalla sessione (tramite FileHandler.loadUser)
        currentUser = FileHandler.loadUser(UserSession.getInstance().getUsername());
        if (currentUser != null) {
            usernameField.setText(currentUser.getUsername());
            firstNameField.setText(currentUser.getFirstName());
            lastNameField.setText(currentUser.getLastName());
            emailField.setText(currentUser.getEmail());
            phoneField.setText(currentUser.getPhoneNumber());
            autoSendCheckBox.setSelected(currentUser.isAutoSendEnabled());
            if (currentUser.getProfilePicturePath() != null && !currentUser.getProfilePicturePath().isEmpty()) {
                try {
                    Image img = new Image("file:" + currentUser.getProfilePicturePath());
                    profileImageView.setImage(img);
                } catch (Exception e) {
                    System.err.println("Impossibile caricare l'immagine di profilo.");
                }
            }
        }
    }
    
    @FXML
    private void handleSave() {
        currentUser.setFirstName(firstNameField.getText());
        currentUser.setLastName(lastNameField.getText());
        currentUser.setEmail(emailField.getText());
        currentUser.setPhoneNumber(phoneField.getText());
        currentUser.setAutoSendEnabled(autoSendCheckBox.isSelected());
        try {
            FileHandler.writeUser(currentUser);
            statusLabel.setText("Profilo aggiornato correttamente.");
        } catch (Exception e) {
            statusLabel.setText("Errore durante l'aggiornamento del profilo.");
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleChooseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Scegli immagine di profilo");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        Stage stage = (Stage) chooseImageButton.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            currentUser.setProfilePicturePath(selectedFile.getAbsolutePath());
            try {
                Image img = new Image("file:" + selectedFile.getAbsolutePath());
                profileImageView.setImage(img);
            } catch (Exception e) {
                System.err.println("Errore nel caricare l'immagine selezionata.");
            }
        }
    }

    @FXML
    private void handleBackToHomepage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/play/view/homepage.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) usernameField.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/com/play/application.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Homepage");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
