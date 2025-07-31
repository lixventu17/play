package com.play.util;

/**
 * Classe che implementa il pattern Singleton per gestire la sessione dell'utente.
 * Mantiene le informazioni dell'utente attualmente loggato.
 * 
 * @author Play Team
 * @version 1.0
 */
public class UserSession {
    private static UserSession instance;
    private static final Object LOCK = new Object();

    private String username;
    private String firstName;
    private String lastName;

    /**
     * Costruttore privato per implementare il pattern Singleton.
     */
    private UserSession() {
    }

    /**
     * Ottiene l'istanza singleton della sessione utente.
     * Implementa il pattern Double-Checked Locking per thread-safety.
     * 
     * @return L'istanza singleton di UserSession
     */
    public static UserSession getInstance() {
        if (instance == null) {
            synchronized (LOCK) {
                if (instance == null) {
                    instance = new UserSession();
                }
            }
        }
        return instance;
    }

    /**
     * Ottiene il nome utente della sessione corrente.
     * 
     * @return Il nome utente, null se non è impostato
     */
    public String getUsername() {
        return username;
    }

    /**
     * Imposta il nome utente della sessione.
     * 
     * @param username Il nuovo nome utente
     * @throws IllegalArgumentException se il nome utente è null o vuoto
     */
    public void setUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Il nome utente non può essere vuoto");
        }
        this.username = username;
    }

    /**
     * Ottiene il nome dell'utente della sessione corrente.
     * 
     * @return Il nome dell'utente, null se non è impostato
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Imposta il nome dell'utente della sessione.
     * 
     * @param firstName Il nuovo nome dell'utente
     * @throws IllegalArgumentException se il nome è null o vuoto
     */
    public void setFirstName(String firstName) {
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("Il nome non può essere vuoto");
        }
        this.firstName = firstName;
    }

    /**
     * Ottiene il cognome dell'utente della sessione corrente.
     * 
     * @return Il cognome dell'utente, null se non è impostato
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Imposta il cognome dell'utente della sessione.
     * 
     * @param lastName Il nuovo cognome dell'utente
     * @throws IllegalArgumentException se il cognome è null o vuoto
     */
    public void setLastName(String lastName) {
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("Il cognome non può essere vuoto");
        }
        this.lastName = lastName;
    }

    /**
     * Verifica se esiste una sessione utente attiva.
     * 
     * @return true se esiste una sessione attiva, false altrimenti
     */
    public boolean isSessionActive() {
        return username != null && firstName != null && lastName != null;
    }

    /**
     * Pulisce i dati della sessione corrente.
     * Imposta tutti i campi a null.
     */
    public void clearSession() {
        username = null;
        firstName = null;
        lastName = null;
    }

    /**
     * Ottiene il nome completo dell'utente.
     * 
     * @return Il nome completo (nome + cognome), null se la sessione non è attiva
     */
    public String getFullName() {
        if (!isSessionActive()) {
            return null;
        }
        return firstName + " " + lastName;
    }
}
