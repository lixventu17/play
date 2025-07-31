package com.play.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLEncoder;

/**
 * Classe utility per l'invio di messaggi tramite Telegram.
 * Utilizza l'API di Telegram per inviare messaggi agli utenti.
 */
public class TelegramSender {
    private static final String BOT_TOKEN = "7095458307:AAGJ_teoNOWZcdgsS5eOkScQMg7yjmhlvUc";

    /**
     * Invia un messaggio Telegram all'utente specificato.
     * 
     * @param chatId L'ID della chat Telegram dell'utente
     * @param message Il messaggio da inviare
     * @throws IllegalArgumentException se il chatId è null o vuoto
     * @throws RuntimeException se si verifica un errore durante l'invio
     */
    public static void sendTelegram(String chatId, String message) {
        if (chatId == null || chatId.trim().isEmpty()) {
            throw new IllegalArgumentException("Chat ID non può essere null o vuoto");
        }
        if (message == null || message.trim().isEmpty()) {
            throw new IllegalArgumentException("Il messaggio non può essere null o vuoto");
        }

        try {
            String urlString = "https://api.telegram.org/bot" + BOT_TOKEN + "/sendMessage?chat_id=" + chatId + "&text=" + URLEncoder.encode(message, "UTF-8");
            
            URI uri = new URI(urlString);
            HttpURLConnection conn = (HttpURLConnection) uri.toURL().openConnection();
            conn.setRequestMethod("GET");
            
            int responseCode = conn.getResponseCode();
            
            // Leggi la risposta per avere più dettagli in caso di errore
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                responseCode >= 400 ? conn.getErrorStream() : conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            
            if (responseCode != 200) {
                throw new RuntimeException("Errore durante l'invio del messaggio Telegram. Codice risposta: " + responseCode + 
                    "\nRisposta: " + response.toString());
            }
        } catch (Exception e) {
            throw new RuntimeException("Errore durante l'invio del messaggio Telegram: " + e.getMessage(), e);
        }
    }

    /**
     * Verifica se un utente ha configurato Telegram.
     * 
     * @param chatId L'ID della chat Telegram dell'utente
     * @return true se l'utente ha configurato Telegram, false altrimenti
     */
    public static boolean isTelegramConfigured(String chatId) {
        return chatId != null && !chatId.trim().isEmpty();
    }
}
