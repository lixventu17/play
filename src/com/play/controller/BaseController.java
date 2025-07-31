package com.play.controller;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.stage.Screen;

/**
 * Classe base per tutti i controller dell'applicazione.
 * Fornisce funzionalità comuni come la navigazione tra pagine e la gestione degli errori.
 */
public abstract class BaseController {
    
    /**
     * Naviga a una nuova pagina.
     * 
     * @param fxmlPath Il percorso del file FXML della pagina di destinazione
     * @param sourceNode Il nodo che ha triggerato la navigazione
     * @param title Il titolo della nuova finestra
     */
    protected void navigateToPage(String fxmlPath, Node sourceNode, String title) {
        try {
            // Prova diversi approcci per caricare il file FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            
            // Se il primo tentativo fallisce, prova con il percorso assoluto
            if (loader.getLocation() == null) {
                loader = new FXMLLoader(BaseController.class.getResource(fxmlPath));
            }
            
            if (loader.getLocation() == null) {
                throw new IOException("Impossibile trovare il file FXML: " + fxmlPath);
            }
            
            Parent root = loader.load();
            Stage stage = (Stage) sourceNode.getScene().getWindow();
            
            Scene scene = new Scene(root);
            
            // Carica il CSS se disponibile
            try {
                java.net.URL cssUrl = getClass().getResource("/com/play/application.css");
                if (cssUrl != null) {
                    scene.getStylesheets().add(cssUrl.toExternalForm());
                } else {
                    // Prova con un percorso alternativo
                    cssUrl = BaseController.class.getResource("/com/play/application.css");
                    if (cssUrl != null) {
                        scene.getStylesheets().add(cssUrl.toExternalForm());
                    }
                }
            } catch (Exception e) {
                // Se il CSS non può essere caricato, continua senza
                System.err.println("Impossibile caricare il CSS: " + e.getMessage());
            }
            
            stage.setScene(scene);
            stage.setTitle(title);
            
            // Forza il fullscreen in modo più robusto
            forceFullscreen(stage);
            
            stage.show();
        } catch (IOException e) {
            handleError("Errore di navigazione", "Impossibile aprire la pagina: " + fxmlPath + "\nErrore: " + e.getMessage());
        }
    }
    
    /**
     * Forza la finestra a occupare tutto lo schermo in modo robusto.
     * 
     * @param stage La finestra da impostare a fullscreen
     */
    private void forceFullscreen(Stage stage) {
        // Usa Platform.runLater per assicurarsi che venga eseguito dopo che la scena è stata impostata
        javafx.application.Platform.runLater(() -> {
            try {
                // Prima prova a massimizzare
                stage.setMaximized(true);
                
                // Poi imposta anche le dimensioni esatte dello schermo
                Screen screen = Screen.getPrimary();
                javafx.geometry.Rectangle2D bounds = screen.getVisualBounds();
                
                stage.setX(bounds.getMinX());
                stage.setY(bounds.getMinY());
                stage.setWidth(bounds.getWidth());
                stage.setHeight(bounds.getHeight());
                
                // Assicurati che la finestra sia sempre in primo piano
                stage.setAlwaysOnTop(true);
                stage.setAlwaysOnTop(false);
                
            } catch (Exception e) {
                System.err.println("Errore nell'impostazione del fullscreen: " + e.getMessage());
            }
        });
    }

    /**
     * Gestisce gli errori mostrando un messaggio all'utente.
     * 
     * @param title Il titolo del messaggio di errore
     * @param message Il messaggio di errore dettagliato
     */
    protected void handleError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Mostra un messaggio informativo all'utente.
     * 
     * @param title Il titolo del messaggio
     * @param message Il contenuto del messaggio
     */
    protected void showInformation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Mostra un messaggio di conferma all'utente.
     * 
     * @param title Il titolo del messaggio
     * @param message Il contenuto del messaggio
     * @return true se l'utente ha confermato, false altrimenti
     */
    protected boolean showConfirmation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert.showAndWait().get() == javafx.scene.control.ButtonType.OK;
    }
} 