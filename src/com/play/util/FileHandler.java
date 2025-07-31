package com.play.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.play.model.CompletionQuestion;
import com.play.model.Question;
import com.play.model.User;

/**
 * Classe utility per la gestione delle operazioni di I/O su file.
 * Gestisce il salvataggio e il caricamento di utenti, progressi e domande.
 * 
 * @author Play Team
 * @version 1.0
 */
public class FileHandler {
    private static final String USER_DIRECTORY = "resources/com/play/users";
    private static final String PROGRESS_DIRECTORY = "resources/com/play/progress";
    private static final String QUESTIONS_DIRECTORY = "resources/com/play/questions";
    private static boolean completed = false;

    /**
     * Classe interna per rappresentare le informazioni di progresso di un esercizio.
     */
    private static class ProgressInfo {
        private String difficulty;
        private int level;

        public ProgressInfo(String difficulty, int level) {
            this.difficulty = difficulty;
            this.level = level;
        }
    }

    /**
     * Legge tutti gli utenti dal filesystem.
     * 
     * @return Lista di tutti gli utenti
     * @throws IOException se si verifica un errore durante la lettura
     */
    public static List<User> readUsers() throws IOException {
        List<User> users = new ArrayList<>();
        File dir = new File(USER_DIRECTORY);
        if (!dir.exists()) {
            dir.mkdirs();
            return users;
        }

        File[] files = dir.listFiles((d, name) -> name.endsWith(".dat"));
        if (files != null) {
            for (File file : files) {
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                    users.add((User) ois.readObject());
                } catch (ClassNotFoundException e) {
                    System.err.println("Errore durante la deserializzazione dell'utente: " + file.getName());
                    throw new IOException("Formato file utente non valido", e);
                }
            }
        }
        return users;
    }

    /**
     * Salva un utente nel filesystem.
     * 
     * @param user L'utente da salvare
     * @throws IOException se si verifica un errore durante la scrittura
     * @throws IllegalArgumentException se l'utente è null
     */
    public static void writeUser(User user) throws IOException {
        if (user == null) {
            throw new IllegalArgumentException("L'utente non può essere null");
        }

        File dir = new File(USER_DIRECTORY);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File userFile = new File(USER_DIRECTORY + "/" + user.getUsername() + ".dat");
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(userFile))) {
            oos.writeObject(user);
        } catch (IOException e) {
            System.err.println("Errore durante la scrittura dell'utente " + user.getUsername());
            throw e;
        }
    }

    /**
     * Carica un utente dal filesystem.
     * 
     * @param username Il nome utente da caricare
     * @return L'utente caricato, null se non trovato
     * @throws IllegalArgumentException se il nome utente è null o vuoto
     */
    public static User loadUser(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Il nome utente non può essere vuoto");
        }

        File userFile = new File(USER_DIRECTORY + "/" + username + ".dat");
        if (!userFile.exists()) {
            return null;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(userFile))) {
            return (User) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Errore durante il caricamento dell'utente " + username);
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Carica tutte le domande per un esercizio specifico.
     * 
     * @param exerciseId ID dell'esercizio
     * @param difficulty Difficoltà dell'esercizio
     * @param level Livello dell'esercizio
     * @return Lista delle domande
     * @throws IllegalArgumentException se i parametri sono non validi
     */
    public static List<Question> loadQuestions(String exerciseId, String difficulty, int level) {
        validateExerciseParameters(exerciseId, difficulty, level);
        
        List<Question> questions = new ArrayList<>();
        String filePath = QUESTIONS_DIRECTORY + "/" + exerciseId + "/" + difficulty + "/" + level + ".txt";
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue; // Salta righe vuote tra le domande
                Question question = parseQuestion(reader, line);
                if (question != null) {
                    questions.add(question);
                }
            }
        } catch (IOException e) {
            System.err.println("Errore durante il caricamento delle domande: " + e.getMessage());
            e.printStackTrace();
        }
        return questions;
    }

    /**
     * Carica tutte le domande di completamento per un esercizio specifico.
     * 
     * @param exerciseId ID dell'esercizio
     * @param difficulty Difficoltà dell'esercizio
     * @param level Livello dell'esercizio
     * @return Lista delle domande di completamento
     * @throws IllegalArgumentException se i parametri sono non validi
     */
    public static List<CompletionQuestion> loadCompletionQuestions(String exerciseId, String difficulty, int level) {
        validateExerciseParameters(exerciseId, difficulty, level);
        
        List<CompletionQuestion> questions = new ArrayList<>();
        String filePath = QUESTIONS_DIRECTORY + "/" + exerciseId + "/" + difficulty + "/" + level + ".txt";
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                CompletionQuestion question = parseCompletionQuestion(reader, line);
                if (question != null) {
                    questions.add(question);
                }
            }
        } catch (IOException e) {
            System.err.println("Errore durante il caricamento delle domande di completamento: " + e.getMessage());
            e.printStackTrace();
        }
        return questions;
    }

    /**
     * Salva il progresso di un utente.
     * 
     * @param username Nome utente
     * @param exerciseId ID dell'esercizio
     * @param difficulty Difficoltà
     * @param level Livello
     * @param score Punteggio
     * @param seconds Secondi impiegati
     * @param attempts Tentativi
     * @param total Punteggio totale (può essere 0 per tentativi abbandonati)
     * @throws IllegalArgumentException se i parametri sono non validi
     */
    public static void saveUserProgress(String username, String exerciseId, String difficulty, int level, int score, int seconds, int attempts, int total) {
        validateProgressParameters(username, exerciseId, difficulty, level, score, seconds, attempts, total);

        File dir = new File(PROGRESS_DIRECTORY);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(PROGRESS_DIRECTORY + "/" + username + ".dat", true))) {
            writer.write(String.format("%s;%s;%d;%d;%d;%d;%d", exerciseId, difficulty, level, score, seconds, attempts, total));
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Errore durante il salvataggio del progresso: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Metodi di validazione privati

    private static void validateExerciseParameters(String exerciseId, String difficulty, int level) {
        if (exerciseId == null || exerciseId.trim().isEmpty()) {
            throw new IllegalArgumentException("L'ID dell'esercizio non può essere vuoto");
        }
        if (difficulty == null || difficulty.trim().isEmpty()) {
            throw new IllegalArgumentException("La difficoltà non può essere vuota");
        }
        if (level < 1 || level > 4) {
            throw new IllegalArgumentException("Il livello deve essere compreso tra 1 e 4");
        }
    }

    private static void validateProgressParameters(String username, String exerciseId, String difficulty, int level, int score, int seconds, int attempts, int total) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Il nome utente non può essere vuoto");
        }
        validateExerciseParameters(exerciseId, difficulty, level);
        if (score < 0) {
            throw new IllegalArgumentException("Il punteggio non può essere negativo");
        }
        if (seconds < 0) {
            throw new IllegalArgumentException("I secondi non possono essere negativi");
        }
        if (attempts < 0) {
            throw new IllegalArgumentException("I tentativi non possono essere negativi");
        }
        if (total < 0) {
            throw new IllegalArgumentException("Il totale non può essere negativo");
        }
    }

    private static Question parseQuestion(BufferedReader reader, String firstLine) throws IOException {
        if (firstLine == null || firstLine.trim().isEmpty()) {
            System.out.println("ERRORE: questionText nullo o vuoto!");
            return null;
        }

        String questionText = firstLine;
        String text = null; // testo aggiuntivo (es. codice)
        String line = reader.readLine();

        // Controlla se c'è un blocco di codice
        if (line != null && line.trim().equals("-----")) {
            StringBuilder codeBlock = new StringBuilder();
            while ((line = reader.readLine()) != null && !line.trim().equals("-----")) {
                codeBlock.append(line).append("\n");
            }
            text = codeBlock.toString().trim();
            // Dopo il secondo -----, leggi la prossima riga (prima opzione)
            line = reader.readLine();
        }

        // Se non c'è testo aggiuntivo, metti uno spazio (o altro valore di default)
        if (text == null || text.trim().isEmpty()) {
            text = "";
        }

        // Leggi sempre 4 opzioni (anche se vuote)
        String option1 = (line != null) ? line.trim() : "";
        line = reader.readLine();
        String option2 = (line != null) ? line.trim() : "";
        line = reader.readLine();
        String option3 = (line != null) ? line.trim() : "";
        line = reader.readLine();
        String option4 = (line != null) ? line.trim() : "";
        line = reader.readLine();

        // Salta eventuali righe vuote prima della risposta corretta
        while (line != null && line.trim().isEmpty()) {
            line = reader.readLine();
        }
        String correctAnswer = (line != null) ? line.trim() : "";
        if (correctAnswer.startsWith("'") && correctAnswer.endsWith("'") && correctAnswer.length() > 1) {
            correctAnswer = correctAnswer.substring(1, correctAnswer.length() - 1);
        }

        // Costruisci la domanda
        if ((option3 == null || option3.isEmpty()) && (option4 == null || option4.isEmpty())) {
            return new Question(questionText, text, option1, option2, correctAnswer);
        } else {
            return new Question(questionText, text, option1, option2, option3, option4, correctAnswer);
        }
    }

    private static CompletionQuestion parseCompletionQuestion(BufferedReader reader, String ask) throws IOException {
        if (ask == null || ask.trim().isEmpty()) {
            System.out.println("ERRORE: questionText nullo o vuoto!");
            return null;
        }

        String questionText = ask;
        String text = null; // testo aggiuntivo (es. codice)
        String line = reader.readLine();

        // Controlla se c'è un blocco di codice
        if (line != null && line.trim().equals("-----")) {
            StringBuilder codeBlock = new StringBuilder();
            while ((line = reader.readLine()) != null && !line.trim().equals("-----")) {
                codeBlock.append(line).append("\n");
            }
            text = codeBlock.toString().trim();
            // Dopo il secondo -----, leggi la prossima riga (prima opzione)
            line = reader.readLine();
        }

        // Se non c'è testo aggiuntivo, metti uno spazio (o altro valore di default)
        if (text == null || text.trim().isEmpty()) {
            text = "";
        }

        String correctAnswer = (line != null) ? line.trim() : "";
        if (correctAnswer.startsWith("'") && correctAnswer.endsWith("'") && correctAnswer.length() > 1) {
            correctAnswer = correctAnswer.substring(1, correctAnswer.length() - 1);
        }

        // Salta eventuali righe vuote dopo la risposta corretta
        reader.readLine();

        // Costruisci la domanda
        return new CompletionQuestion(questionText, text, correctAnswer);
    }

    public static List<String> getAllProgress() {
        List<String> progress = new ArrayList<>();
        File dir = new File(PROGRESS_DIRECTORY);
        File[] files = dir.listFiles((d, name) -> name.endsWith(".dat"));
        if (files != null) {
            for (File file : files) {
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        progress.add(line);
                    }
                } catch (IOException e) {
                    System.err.println("Errore nella lettura del file: " + file.getName());
                    e.printStackTrace();
                }
            }
        }
        return progress;
    }

    public static List<String> loadUserProgress(String username) {
        List<String> progress = new ArrayList<>();
        File progressFile = new File(PROGRESS_DIRECTORY + "/" + username + ".dat");
        if (!progressFile.exists()) {
            // Se il file non esiste, l'utente non ha ancora progressi, restituisci una lista vuota
            return progress;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(progressFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                progress.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return progress;
    }

    public static List<String> loadDifficult(String username) {
        List<String> results = new ArrayList<>();
        List<String> progressLines = loadUserProgress(username);
        Map<String, ProgressInfo> progressMap = new HashMap<>();

        for (String line : progressLines) {
            String[] parts = line.split(";");
            if (parts.length < 5) {
				continue;
			}
            String exerciseId = parts[0];
            String difficulty = parts[1];
            int level;
            try {
                level = Integer.parseInt(parts[2]);
            } catch(NumberFormatException e) {
                continue;
            }
            int score;
            try {
                score = Integer.parseInt(parts[3]);
            } catch(NumberFormatException e) {
                continue;
            }
            // Se il tentativo è completato (score == 7)
            if (score == 7) {
                ProgressInfo pi = progressMap.get(exerciseId);
                if (pi == null) {
                    // Se si è completato il livello 3, sblocca la difficoltà successiva
                    if (level == 3) {
                        // Passa a difficoltà successiva (da "principiante" a "intermedio", da "intermedio" a "esperto")
                        if (difficulty.equalsIgnoreCase("principiante")) {
							progressMap.put(exerciseId, new ProgressInfo("intermedio", 1));
						} else if (difficulty.equalsIgnoreCase("intermedio")) {
							progressMap.put(exerciseId, new ProgressInfo("esperto", 1));
						} else {
							progressMap.put(exerciseId, new ProgressInfo("esperto", 3)); // massimo per esperto
						}
                    } else {
                        progressMap.put(exerciseId, new ProgressInfo(difficulty, level));
                    }
                } else {
                    // Se già esiste, controlla se questo tentativo sblocca un livello maggiore
                    // (logica: se piú alto livello per la stessa difficoltà oppure se a livello 3 si sblocca la successiva)
                    if (pi.difficulty.equalsIgnoreCase(difficulty)) {
                        if (level > pi.level) {
                            if (level == 3) {
                                // Sblocca difficoltà successiva
                                if (difficulty.equalsIgnoreCase("principiante")) {
									progressMap.put(exerciseId, new ProgressInfo("intermedio", 1));
								} else if (difficulty.equalsIgnoreCase("intermedio")) {
									progressMap.put(exerciseId, new ProgressInfo("esperto", 1));
								} else {
									progressMap.put(exerciseId, new ProgressInfo("esperto", 3));
								}
                            } else {
                                pi.level = level;
                            }
                        }
                    }
                    // Se il record corrente ha difficoltà inferiore, puoi decidere di mantenere quella maggiore
                }
            }
        }
        // Costruisci la lista finale: per ogni exerciseId, il risultato potrebbe essere "exercise1;intermedio;1", ad esempio.
        for (Map.Entry<String, ProgressInfo> entry : progressMap.entrySet()) {
            results.add(entry.getKey() + ";" + entry.getValue().difficulty + ";" + entry.getValue().level);
        }
        return results;
    }


    public static boolean exerciseCompleted() {
        return completed;
    }
}
