package com.play.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Classe che rappresenta un utente dell'applicazione.
 * Implementa Serializable per permettere la persistenza dei dati.
 * 
 * @author Play Team
 * @version 1.0
 */
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    // Campi dell'utente
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private String profilePicturePath;
    private boolean autoSendEnabled;
    private String telegramChatId; // ID della chat Telegram dell'utente

    // Mappa per tenere traccia del progresso negli esercizi
    private Map<String, Integer> exerciseProgress;

    /**
     * Costruttore per creare un nuovo utente.
     * 
     * @param username Nome utente
     * @param password Password
     * @param firstName Nome
     * @param lastName Cognome
     * @param email Email (opzionale)
     * @throws IllegalArgumentException se uno dei parametri obbligatori è null o vuoto
     */
    public User(String username, String password, String firstName, String lastName, String email) {
        validateConstructorParameters(username, password, firstName, lastName);
        
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.profilePicturePath = null;
        this.autoSendEnabled = false;
        this.telegramChatId = null; // Inizialmente null, verrà impostato quando l'utente si connette con Telegram
        this.exerciseProgress = new HashMap<>();
    }

    /**
     * Valida i parametri obbligatori del costruttore.
     * 
     * @param username Nome utente
     * @param password Password
     * @param firstName Nome
     * @param lastName Cognome
     * @throws IllegalArgumentException se uno dei parametri è null o vuoto
     */
    private void validateConstructorParameters(String username, String password, String firstName, String lastName) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username non può essere null o vuoto");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password non può essere null o vuota");
        }
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome non può essere null o vuoto");
        }
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("Cognome non può essere null o vuoto");
        }
    }

    // Getters e Setters con validazione

    public String getUsername() { return username; }
    
    /**
     * Imposta il nome utente.
     * 
     * @param username Il nuovo nome utente
     * @throws IllegalArgumentException se il nome utente è null o vuoto
     */
    public void setUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username non può essere null o vuoto");
        }
        this.username = username;
    }

    public String getPassword() { return password; }
    
    /**
     * Imposta la password.
     * 
     * @param password La nuova password
     * @throws IllegalArgumentException se la password è null o vuota
     */
    public void setPassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password non può essere null o vuota");
        }
        this.password = password;
    }

    public String getFirstName() { return firstName; }
    
    /**
     * Imposta il nome.
     * 
     * @param firstName Il nuovo nome
     * @throws IllegalArgumentException se il nome è null o vuoto
     */
    public void setFirstName(String firstName) {
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome non può essere null o vuoto");
        }
        this.firstName = firstName;
    }

    public String getLastName() { return lastName; }
    
    /**
     * Imposta il cognome.
     * 
     * @param lastName Il nuovo cognome
     * @throws IllegalArgumentException se il cognome è null o vuoto
     */
    public void setLastName(String lastName) {
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("Cognome non può essere null o vuoto");
        }
        this.lastName = lastName;
    }

    /**
     * Ottiene il progresso per un esercizio specifico.
     * 
     * @param exerciseId ID dell'esercizio
     * @return Il progresso dell'esercizio, 0 se non presente
     */
    public int getExerciseProgress(String exerciseId) {
        if (exerciseId == null || exerciseId.trim().isEmpty()) {
            throw new IllegalArgumentException("ID esercizio non può essere null o vuoto");
        }
        return exerciseProgress.getOrDefault(exerciseId, 0);
    }

    /**
     * Imposta il progresso per un esercizio specifico.
     * 
     * @param exerciseId ID dell'esercizio
     * @param progress Il nuovo progresso
     * @throws IllegalArgumentException se l'ID esercizio è null o vuoto, o se il progresso è negativo
     */
    public void setExerciseProgress(String exerciseId, int progress) {
        if (exerciseId == null || exerciseId.trim().isEmpty()) {
            throw new IllegalArgumentException("ID esercizio non può essere null o vuoto");
        }
        if (progress < 0) {
            throw new IllegalArgumentException("Progresso non può essere negativo");
        }
        exerciseProgress.put(exerciseId, progress);
    }

    public String getEmail() { return email; }
    
    /**
     * Imposta l'email.
     * 
     * @param email La nuova email
     * @throws IllegalArgumentException se l'email non è null e non è valida
     */
    public void setEmail(String email) {
        if (email != null && !email.isEmpty() && !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Formato email non valido");
        }
        this.email = email;
    }

    public String getProfilePicturePath() { return profilePicturePath; }
    
    /**
     * Imposta il percorso dell'immagine del profilo.
     * 
     * @param profilePicturePath Il nuovo percorso dell'immagine
     */
    public void setProfilePicturePath(String profilePicturePath) {
        this.profilePicturePath = profilePicturePath;
    }

    public boolean isAutoSendEnabled() { return autoSendEnabled; }
    
    /**
     * Imposta l'abilitazione dell'invio automatico.
     * 
     * @param autoSendEnabled Nuovo stato dell'invio automatico
     */
    public void setAutoSendEnabled(boolean autoSendEnabled) {
        this.autoSendEnabled = autoSendEnabled;
    }

    /**
     * Ottiene l'ID della chat Telegram dell'utente.
     * 
     * @return L'ID della chat Telegram, null se non configurato
     */
    public String getTelegramChatId() {
        return telegramChatId;
    }

    /**
     * Imposta l'ID della chat Telegram dell'utente.
     * 
     * @param telegramChatId Il nuovo ID della chat Telegram
     * @throws IllegalArgumentException se l'ID della chat non è valido
     */
    public void setTelegramChatId(String telegramChatId) {
        if (telegramChatId != null && !telegramChatId.matches("^-?\\d+$")) {
            throw new IllegalArgumentException("ID chat Telegram non valido");
        }
        this.telegramChatId = telegramChatId;
    }

    /**
     * Verifica se l'utente ha configurato Telegram.
     * 
     * @return true se l'utente ha un ID chat Telegram configurato, false altrimenti
     */
    public boolean hasTelegramConfigured() {
        return telegramChatId != null && !telegramChatId.isEmpty();
    }

    @Override
    public String toString() {
        return username + ":" + password + ":" + firstName + ":" + lastName + ":" + email + (telegramChatId != null ? ":" + telegramChatId : "");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }
}
