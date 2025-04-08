package com.play.util;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLEncoder;

public class TelegramSender {
    public static void sendTelegram(String phoneNumber, String message) {
        // Per il test, usiamo valori fissi. In un sistema reale, mappa il numero di telefono al chat id.
        String botToken = "7095458307:AAGJ_teoNOWZcdgsS5eOkScQMg7yjmhlvUc";
        String chatId = "696126521";

        try {
            String urlString = "https://api.telegram.org/bot" + botToken + "/sendMessage?chat_id=" 
                    + chatId + "&text=" + URLEncoder.encode(message, "UTF-8");
            URI uri = new URI(urlString);
            HttpURLConnection conn = (HttpURLConnection) uri.toURL().openConnection();
            conn.setRequestMethod("GET");
            int responseCode = conn.getResponseCode();
            System.out.println("Telegram API response code: " + responseCode);
            if (responseCode != 200) {
                System.err.println("Errore durante l'invio del messaggio Telegram.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Errore durante l'invio del messaggio Telegram a " + phoneNumber);
        }
    }
}
