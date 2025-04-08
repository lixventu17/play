package com.play.model;

import java.util.HashMap;
import java.util.Map;

public class Exercise {
    private String id;
    private String title;
    private String baseDescription;
    private String instructions;
    private String imagePath;
    private Map<String, String> difficultyDescriptions;
    private Map<Integer, String> levelDescriptions;

    public Exercise(String id, String title, String baseDescription, String instructions, String imagePath) {
        this.id = id;
        this.title = title;
        this.baseDescription = baseDescription;
        this.instructions = instructions;
        this.imagePath = imagePath;

        difficultyDescriptions = new HashMap<>();
        difficultyDescriptions.put("principiante", "In modalità Principiante, le domande sono studiate per verificare le conoscenze di base senza complicazioni.");
        difficultyDescriptions.put("intermedio", "In modalità Intermedio, la difficoltà aumenta: le domande sono formulate per mettere alla prova la tua capacità di analisi e comprensione, senza però risultare eccessivamente complesse.");
        difficultyDescriptions.put("esperto", "In modalità Esperto, le domande sono estremamente stimolanti e richiedono un'analisi approfondita dei concetti di programmazione.");

        levelDescriptions = new HashMap<>();
        levelDescriptions.put(1, "Livello 1.");
        levelDescriptions.put(2, "Livello 2.");
        levelDescriptions.put(3, "Livello 3.");
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getDescription(String difficulty, int level) {
        String diffPart = difficultyDescriptions.getOrDefault(difficulty.toLowerCase(), "");
        String levelPart = "Ogni difficoltà è composta da tre livelli, con l'obiettivo di aumentare progressivamente la complessità delle domande." + "\n\n" + "Adesso stai per affrontare il " + levelDescriptions.getOrDefault(level, "");
        return baseDescription + "\n\n" + diffPart + "\n\n" + levelPart;
    }

    public String getInstructions() {
        String commonPart = "Ogni esercizio è composto da 7 domande. Per completare l'esercizio dovrai rispondere correttamente a tutte le 7 domande." + "\n\n" +
                "Durante l'esercizio, utilizzerai i pulsanti 'Precedente' e 'Seguente' per navigare tra le domande. Potrai abbandonare il tentativo con il pulsante 'Abbandona'." + "\n\n" +
        		"Un timer terrà traccia del tempo, che inciderà anche sul tuo punteggio nella classifica." + "\n\n" +
                "Al termine, la pagina dei risultati riepiloga il tuo punteggio e il tempo impiegato, con la possibilità di tornare alla pagina di progresso e di ricevere un resoconto via email e via Telegram del tentativo appena effettuato." + "\n\n" +
                "Per quanto riguarda l'avanzamento, puoi svolgere nuovamente l'esercizio o, se hai risposto correttamente a tutte le domande, passare al livello successivo. Se hai completato tutti i livelli puoi passare alla difficoltà successiva. Se hai completato tutti i livelli di tutte le difficoltà ricevi il badge.";
        return instructions + "\n\n" + commonPart;
    }
}