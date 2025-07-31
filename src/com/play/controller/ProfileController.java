package com.play.controller;

import java.io.File;
import java.io.IOException;

import com.play.model.User;
import com.play.util.FileHandler;
import com.play.util.UserSession;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.event.ActionEvent;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/**
 * Controller per la gestione del profilo utente.
 * Permette la visualizzazione e modifica dei dati personali e dell'immagine del profilo.
 */
public class ProfileController extends BaseController {
    @FXML private ImageView profileImageView;
    @FXML private Button chooseImageButton;
    @FXML private TextField usernameField;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private TextField telegramChatIdField;
    @FXML private CheckBox autoSendCheckBox;
    @FXML private Label statusLabel;
    @FXML private Button saveButton;
    @FXML private ImageView infoIcon;

    private User currentUser;

    /**
     * Inizializza il controller caricando i dati dell'utente corrente.
     * Viene chiamato automaticamente dopo il caricamento del FXML.
     */
    @FXML
    public void initialize() {
        try {
            if (infoIcon != null) {
                infoIcon.setImage(new Image(getClass().getResourceAsStream("/com/play/images/info.png")));
            }
            loadUserProfile();
        } catch (Exception e) {
            handleError("Errore di inizializzazione", "Impossibile caricare il profilo: " + e.getMessage());
        }
    }

    /**
     * Carica i dati dell'utente dalla sessione e li visualizza nell'interfaccia.
     * 
     * @throws IOException se si verifica un errore durante la lettura dei dati
     */
    private void loadUserProfile() throws IOException {
        currentUser = FileHandler.loadUser(UserSession.getInstance().getUsername());
        if (currentUser != null) {
            populateUserFields();
            loadProfileImage();
        } else {
            throw new IOException("Utente non trovato");
        }
    }

    /**
     * Popola i campi dell'interfaccia con i dati dell'utente.
     */
    private void populateUserFields() {
        usernameField.setText(currentUser.getUsername());
        firstNameField.setText(currentUser.getFirstName());
        lastNameField.setText(currentUser.getLastName());
        emailField.setText(currentUser.getEmail());
        telegramChatIdField.setText(currentUser.getTelegramChatId());
        autoSendCheckBox.setSelected(currentUser.isAutoSendEnabled());
    }

    /**
     * Carica l'immagine del profilo se presente.
     */
    private void loadProfileImage() {
        if (currentUser.getProfilePicturePath() != null && !currentUser.getProfilePicturePath().isEmpty()) {
            try {
                File imageFile = new File("resources/com/play/images/profiles/" + currentUser.getProfilePicturePath());
                if (imageFile.exists()) {
                    Image img = new Image("file:" + imageFile.getAbsolutePath());
                    profileImageView.setImage(img);
                }
            } catch (Exception e) {
                handleError("Errore di caricamento", "Impossibile caricare l'immagine di profilo: " + e.getMessage());
            }
        }
    }

    /**
     * Gestisce il salvataggio delle modifiche al profilo.
     * Aggiorna i dati dell'utente e li salva nel database.
     */
    @FXML
    private void handleSaveProfile() {
        try {
            if (validateFields()) {
                updateUserProfile(currentUser);
                FileHandler.writeUser(currentUser);
                showInformation("Profilo aggiornato", "Le modifiche sono state salvate con successo.");
            }
        } catch (Exception e) {
            handleError("Errore di salvataggio", "Impossibile salvare il profilo: " + e.getMessage());
        }
    }

    private boolean validateFields() {
        String firstName = firstNameField.getText() != null ? firstNameField.getText().trim() : "";
        String lastName = lastNameField.getText() != null ? lastNameField.getText().trim() : "";
        String email = emailField.getText() != null ? emailField.getText().trim() : "";
        String telegramChatId = telegramChatIdField.getText() != null ? telegramChatIdField.getText().trim() : "";

        if (firstName.isEmpty() || lastName.isEmpty()) {
            handleError("Errore di validazione", "Nome e cognome sono obbligatori");
            return false;
        }

        if (!email.isEmpty() && !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            handleError("Errore di validazione", "Formato email non valido");
            return false;
        }

        if (autoSendCheckBox.isSelected() && (telegramChatId == null || telegramChatId.isEmpty())) {
            handleError("Errore di validazione", "Per abilitare l'invio automatico è necessario inserire il Chat ID Telegram");
            return false;
        }

        if (telegramChatId != null && !telegramChatId.isEmpty() && !telegramChatId.matches("^-?\\d+$")) {
            handleError("Errore di validazione", "Chat ID Telegram non valido");
            return false;
        }

        return true;
    }

    private void updateUserProfile(User user) {
        user.setFirstName(firstNameField.getText() != null ? firstNameField.getText().trim() : "");
        user.setLastName(lastNameField.getText() != null ? lastNameField.getText().trim() : "");
        user.setEmail(emailField.getText() != null ? emailField.getText().trim() : "");
        user.setTelegramChatId(telegramChatIdField.getText() != null ? telegramChatIdField.getText().trim() : "");
        user.setAutoSendEnabled(autoSendCheckBox.isSelected());
    }

    /**
     * Gestisce la selezione di una nuova immagine di profilo.
     * Permette all'utente di scegliere un'immagine e la salva nella cartella appropriata.
     */
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
            try {
                saveProfileImage(selectedFile);
            } catch (Exception e) {
            	handleError("Errore di salvataggio", "Impossibile salvare l'immagine di profilo: " + e.getMessage());
                statusLabel.setText("Errore nel salvare l'immagine di profilo.");
            }
        }
    }

    /**
     * Salva l'immagine di profilo selezionata nella cartella appropriata.
     * 
     * @param selectedFile Il file immagine selezionato
     * @throws IOException se si verifica un errore durante il salvataggio
     */
    private void saveProfileImage(File selectedFile) throws IOException {
        File profilesDir = new File("resources/com/play/images/profiles");
        if (!profilesDir.exists() && !profilesDir.mkdirs()) {
            throw new IOException("Impossibile creare la directory per le immagini di profilo");
        }

        String extension = selectedFile.getName().substring(selectedFile.getName().lastIndexOf("."));
        File targetFile = new File(profilesDir, currentUser.getUsername() + extension);
        
        java.nio.file.Files.copy(
            selectedFile.toPath(), 
            targetFile.toPath(), 
            java.nio.file.StandardCopyOption.REPLACE_EXISTING
        );

        currentUser.setProfilePicturePath(currentUser.getUsername() + extension);
        updateProfileImageView(targetFile);
    }

    /**
     * Aggiorna l'immagine del profilo nell'interfaccia.
     * 
     * @param imageFile Il file immagine da visualizzare
     * @throws IOException se il file non esiste o non può essere letto
     */
    private void updateProfileImageView(File imageFile) throws IOException {
        if (!imageFile.exists()) {
            throw new IOException("Il file immagine non è stato creato correttamente");
        }
        Image img = new Image(imageFile.toURI().toString());
        profileImageView.setImage(img);
    }

    /**
     * Gestisce il ritorno alla homepage.
     */
    @FXML
    private void handleBackToHomepage() {
        navigateToPage("/com/play/view/homepage.fxml", usernameField, "Homepage");
    }

    /**
     * Mostra una finestra di dialogo con le istruzioni per trovare il Chat ID Telegram (Metodo 1).
     */
    @FXML
    private void handleInfoChatId(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Come trovare il Chat ID Telegram");
        alert.setHeaderText("Guida rapida");

        Text t1 = new Text("Per trovare il tuo Chat ID Telegram e ricevere notifiche:\n\n1. Apri Telegram e cerca ");
        Text t2 = new Text("@userinfobot");
        t2.setStyle("-fx-font-weight: bold");
        Text t3 = new Text("\n2. Avvia la chat con @userinfobot e premi su Start\n3. Il bot ti risponderà con una serie di informazioni fra cui il tuo chat ID (un numero)\n4. Copia il chat ID e incollalo qui\n5. Cerca su Telegram il bot chiamato ");
        Text t4 = new Text("@RiepilogoPlayBot");
        t4.setStyle("-fx-font-weight: bold");
        Text t5 = new Text(", avvia la chat e premi su Start\n\n");
        Text t6 = new Text("⚠️ IMPORTANTE: se non esegui tutti i passaggi sopra, non sarà possibile ricevere messaggi dal bot.");
        t6.setStyle("-fx-fill: #d32f2f; -fx-font-weight: bold");
        TextFlow flow = new TextFlow(t1, t2, t3, t4, t5, t6);
        flow.setPrefWidth(350);
        alert.getDialogPane().setContent(flow);
        alert.showAndWait();
    }
}

