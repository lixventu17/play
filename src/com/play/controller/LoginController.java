package com.play.controller;

import java.io.IOException;
import java.util.List;

import com.play.model.User;
import com.play.util.FileHandler;
import com.play.util.UserSession;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * Controller per la gestione del login e della registrazione degli utenti.
 * Gestisce le operazioni di autenticazione e creazione di nuovi utenti.
 */
public class LoginController extends BaseController {
    // Campi FXML per il form di login
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    /**
     * Inizializza il controller del login. Permette di premere INVIO nei campi per eseguire il login.
     */
    @FXML
    public void initialize() {
        // Quando si preme INVIO su usernameField o passwordField, esegui il login
        usernameField.setOnAction(event -> handleLogin(event));
        passwordField.setOnAction(event -> handleLogin(event));
    }

    /**
     * Gestisce il tentativo di login dell'utente.
     * Verifica le credenziali e, in caso di successo, reindirizza alla homepage.
     * 
     * @param event L'evento che ha triggerato l'azione
     */
    @FXML
    public void handleLogin(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            handleError("Errore di input", "Inserire username e password");
            return;
        }

        try {
            List<User> users = FileHandler.readUsers();
            User loggedInUser = users.stream()
                    .filter(user -> user.getUsername().equals(username) && user.getPassword().equals(password))
                    .findFirst().orElse(null);

            if (loggedInUser != null) {
                initializeUserSession(loggedInUser);
                navigateToHomepage();
            } else {
                handleError("Autenticazione non riuscita", "Username o password errati.");
            }
        } catch (IOException e) {
            handleError("Errore di sistema", "Impossibile accedere al database utenti: " + e.getMessage());
        }
    }

    /**
     * Gestisce la navigazione alla pagina di registrazione.
     * 
     * @param event L'evento che ha triggerato l'azione
     */
    @FXML
    public void handleCreateUser(ActionEvent event) {
        try {
            navigateToCreateUser();
        } catch (IOException e) {
            handleError("Errore di navigazione", "Impossibile aprire la pagina di registrazione: " + e.getMessage());
        }
    }



    // Metodi di supporto privati

    /**
     * Inizializza la sessione utente con i dati dell'utente loggato.
     * 
     * @param user L'utente che ha effettuato il login
     */
    private void initializeUserSession(User user) {
        UserSession session = UserSession.getInstance();
        session.setUsername(user.getUsername());
        session.setFirstName(user.getFirstName());
        session.setLastName(user.getLastName());
    }

    /**
     * Naviga alla homepage dell'applicazione.
     * 
     * @throws IOException se il caricamento della pagina fallisce
     */
    private void navigateToHomepage() throws IOException {
        navigateToPage("/com/play/view/homepage.fxml", usernameField, "Homepage");
    }

    /**
     * Naviga alla pagina di registrazione.
     * 
     * @throws IOException se il caricamento della pagina fallisce
     */
    private void navigateToCreateUser() throws IOException {
        navigateToPage("/com/play/view/create_user.fxml", usernameField, "Registrazione");
    }

}
