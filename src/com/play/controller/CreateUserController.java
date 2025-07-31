package com.play.controller;

import java.io.IOException;
import java.util.List;

import com.play.model.User;
import com.play.util.FileHandler;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * Controller per la pagina di creazione di un nuovo utente.
 * Gestisce la validazione e la creazione di nuovi utenti nel sistema.
 */
public class CreateUserController extends BaseController {
    
    // Campi FXML per il form di registrazione
    @FXML private TextField createUsernameField;
    @FXML private PasswordField createPasswordField;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private TextField telegramChatIdField;

    /**
     * Gestisce la creazione di un nuovo utente.
     * Valida i campi e crea l'utente se tutti i dati sono validi.
     */
    @FXML
    private void handleCreateUserForm() {
        try {
            if (validateCreateUserFields()) {
                createNewUser();
                showInformation("Utente creato", "L'utente è stato creato con successo.");
                navigateToPage("/com/play/view/login.fxml", createUsernameField, "Login");
            }
        } catch (Exception e) {
            handleError("Errore di creazione", "Impossibile creare l'utente: " + e.getMessage());
        }
    }

    /**
     * Gestisce il ritorno alla pagina di login dalla pagina di registrazione.
     */
    @FXML
    private void handleBack() {
        navigateToPage("/com/play/view/login.fxml", createUsernameField, "Login");
    }

    /**
     * Valida i campi del form di creazione utente.
     * 
     * @return true se tutti i campi sono validi, false altrimenti
     */
    private boolean validateCreateUserFields() {
        String username = createUsernameField.getText().trim();
        String password = createPasswordField.getText().trim();
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String email = emailField.getText().trim();
        String telegramChatId = telegramChatIdField.getText().trim();

        if (username.isEmpty() || password.isEmpty() || firstName.isEmpty() || lastName.isEmpty()) {
            handleError("Errore di validazione", "Username, password, nome e cognome sono obbligatori");
            return false;
        }

        // Verifica che l'username non sia già in uso
        try {
            List<User> existingUsers = FileHandler.readUsers();
            boolean usernameExists = existingUsers.stream()
                    .anyMatch(user -> user.getUsername().equals(username));
            if (usernameExists) {
                handleError("Errore di validazione", "Username già in uso");
                return false;
            }
        } catch (IOException e) {
            handleError("Errore di sistema", "Impossibile verificare l'username: " + e.getMessage());
            return false;
        }

        if (!email.isEmpty() && !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            handleError("Errore di validazione", "Formato email non valido");
            return false;
        }

        if (!telegramChatId.isEmpty() && !telegramChatId.matches("^-?\\d+$")) {
            handleError("Errore di validazione", "Chat ID Telegram non valido");
            return false;
        }

        return true;
    }

    /**
     * Crea un nuovo utente con i dati inseriti nel form.
     * 
     * @throws IOException se si verifica un errore durante la scrittura dei dati
     */
    private void createNewUser() throws IOException {
        String username = createUsernameField.getText().trim();
        String password = createPasswordField.getText().trim();
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String email = emailField.getText().trim();
        String telegramChatId = telegramChatIdField.getText().trim();

        User newUser = new User(username, password, firstName, lastName, email.isEmpty() ? null : email);
        if (!telegramChatId.isEmpty()) {
            newUser.setTelegramChatId(telegramChatId);
        }

        FileHandler.writeUser(newUser);
    }
} 