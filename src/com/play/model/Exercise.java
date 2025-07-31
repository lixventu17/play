package com.play.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Classe che rappresenta un esercizio del quiz.
 * Un esercizio contiene informazioni sulla difficoltà, i livelli e le istruzioni.
 * 
 * @author Play Team
 * @version 1.0
 */
public class Exercise {
    private final String id;
    private final String title;
    private final String baseDescription;
    private final String instructions;
    private final String imagePath;
    private final Map<String, String> difficultyDescriptions;
    private final Map<Integer, String> levelDescriptions;

    /**
     * Costruttore per creare un nuovo esercizio.
     * 
     * @param id Identificativo univoco dell'esercizio
     * @param title Titolo dell'esercizio
     * @param baseDescription Descrizione base dell'esercizio
     * @param instructions Istruzioni specifiche dell'esercizio
     * @param imagePath Percorso dell'immagine associata all'esercizio
     * @throws IllegalArgumentException se uno dei parametri è null o vuoto
     */
    public Exercise(String id, String title, String baseDescription, String instructions, String imagePath) {
        validateParameters(id, title, baseDescription, instructions, imagePath);
        
        this.id = id;
        this.title = title;
        this.baseDescription = baseDescription;
        this.instructions = instructions;
        this.imagePath = imagePath;

        this.difficultyDescriptions = initializeDifficultyDescriptions();
        this.levelDescriptions = initializeLevelDescriptions();
    }

    /**
     * Valida i parametri del costruttore.
     * 
     * @param id Identificativo dell'esercizio
     * @param title Titolo dell'esercizio
     * @param baseDescription Descrizione base
     * @param instructions Istruzioni
     * @param imagePath Percorso immagine
     * @throws IllegalArgumentException se uno dei parametri è null o vuoto
     */
    private void validateParameters(String id, String title, String baseDescription, String instructions, String imagePath) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("L'ID dell'esercizio non può essere vuoto");
        }
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Il titolo dell'esercizio non può essere vuoto");
        }
        if (baseDescription == null || baseDescription.trim().isEmpty()) {
            throw new IllegalArgumentException("La descrizione base non può essere vuota");
        }
        if (instructions == null || instructions.trim().isEmpty()) {
            throw new IllegalArgumentException("Le istruzioni non possono essere vuote");
        }
        if (imagePath == null || imagePath.trim().isEmpty()) {
            throw new IllegalArgumentException("Il percorso dell'immagine non può essere vuoto");
        }
    }

    /**
     * Inizializza le descrizioni delle difficoltà.
     * 
     * @return Mappa delle descrizioni delle difficoltà
     */
    private Map<String, String> initializeDifficultyDescriptions() {
        Map<String, String> descriptions = new HashMap<>();
        descriptions.put("principiante", "In modalità Principiante, le domande sono studiate per verificare le conoscenze di base senza complicazioni.");
        descriptions.put("intermedio", "In modalità Intermedio, la difficoltà aumenta: le domande sono formulate per mettere alla prova la tua capacità di analisi e comprensione, senza però risultare eccessivamente complesse.");
        descriptions.put("esperto", "In modalità Esperto, le domande sono estremamente stimolanti e richiedono un'analisi approfondita dei concetti di programmazione.");
        return descriptions;
    }

    /**
     * Inizializza le descrizioni dei livelli.
     * 
     * @return Mappa delle descrizioni dei livelli
     */
    private Map<Integer, String> initializeLevelDescriptions() {
        Map<Integer, String> descriptions = new HashMap<>();
        descriptions.put(1, "Livello 1.");
        descriptions.put(2, "Livello 2.");
        descriptions.put(3, "Livello 3.");
        return descriptions;
    }

    /**
     * Ottiene l'identificativo dell'esercizio.
     * 
     * @return L'ID dell'esercizio
     */
    public String getId() {
        return id;
    }

    /**
     * Ottiene il titolo dell'esercizio.
     * 
     * @return Il titolo dell'esercizio
     */
    public String getTitle() {
        return title;
    }

    /**
     * Ottiene il percorso dell'immagine dell'esercizio.
     * 
     * @return Il percorso dell'immagine
     */
    public String getImagePath() {
        return imagePath;
    }

    /**
     * Ottiene la descrizione completa dell'esercizio per una data difficoltà e livello.
     * 
     * @param difficulty La difficoltà dell'esercizio
     * @param level Il livello dell'esercizio
     * @return La descrizione completa
     * @throws IllegalArgumentException se la difficoltà è null o vuota, o se il livello non è valido
     */
    public String getDescription(String difficulty, int level) {
        if (difficulty == null || difficulty.trim().isEmpty()) {
            throw new IllegalArgumentException("La difficoltà non può essere vuota");
        }
        if (level < 1 || level > 4) {
            throw new IllegalArgumentException("Il livello deve essere compreso tra 1 e 4");
        }

        String diffPart = difficultyDescriptions.getOrDefault(difficulty.toLowerCase(), "");
        String levelPart = "Ogni difficoltà è composta da tre livelli, con l'obiettivo di aumentare progressivamente la complessità delle domande." + "\n\n" + 
                          "Adesso stai per affrontare il " + levelDescriptions.getOrDefault(level, "");
        return baseDescription + "\n\n" + diffPart + "\n\n" + levelPart;
    }

    /**
     * Ottiene le istruzioni complete dell'esercizio.
     * 
     * @return Le istruzioni complete
     */
    public String getInstructions() {
        String commonPart = "Ogni esercizio è composto da 7 domande. Per completare l'esercizio dovrai rispondere correttamente a tutte le 7 domande." + "\n\n" +
                "Durante l'esercizio, utilizzerai i pulsanti 'Precedente' e 'Seguente' per navigare tra le domande. Potrai abbandonare il tentativo con il pulsante 'Abbandona'." + "\n\n" +
                "Un timer terrà traccia del tempo, che inciderà anche sul tuo punteggio nella classifica." + "\n\n" +
                "Al termine, la pagina dei risultati riepiloga il tuo punteggio e il tempo impiegato, con la possibilità di tornare alla pagina di progresso e di ricevere un resoconto via email e via Telegram del tentativo appena effettuato." + "\n\n" +
                "Per quanto riguarda l'avanzamento, puoi svolgere nuovamente l'esercizio o, se hai risposto correttamente a tutte le domande, passare al livello successivo. Se hai completato tutti i livelli puoi passare alla difficoltà successiva. Se hai completato tutti i livelli di tutte le difficoltà ricevi il badge.";
        return instructions + "\n\n" + commonPart;
    }
}