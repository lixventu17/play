package com.play.util;

import com.play.model.User;
import com.play.model.Question;
import com.play.model.CompletionQuestion;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileHandler {
    private static final String USER_DIRECTORY = "resources/com/play/users";
    private static final String PROGRESS_DIRECTORY = "resources/com/play/progress";
    private static boolean completed = false;

    public static List<User> readUsers() throws IOException {
        List<User> users = new ArrayList<>();
        File dir = new File(USER_DIRECTORY);
        File[] files = dir.listFiles((d, name) -> name.endsWith(".dat"));

        if (files != null) {
            for (File file : files) {
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                    users.add((User) ois.readObject());
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        return users;
    }

    public static void writeUser(User user) throws IOException {
        File userFile = new File(USER_DIRECTORY + "/" + user.getUsername() + ".dat");
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(userFile))) {
            oos.writeObject(user);
        } catch (IOException e) {
            System.err.println("Errore durante la scrittura dell'utente " + user.getUsername());
            throw e;
        }
    }
    
    public static User loadUser(String username) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(USER_DIRECTORY + "/" + username + ".dat"))) {
            return (User) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Errore durante il caricamento dell'utente " + username);
            e.printStackTrace();
        }
        return null;
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
    
    public static List<Question> loadQuestions(String exerciseId, String difficulty, int level) {
        List<Question> questions = new ArrayList<>();
        String filePath = "resources/com/play/questions/" + exerciseId + "/" + difficulty + "/" + level + ".txt";
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Legge la prima riga come testo iniziale della domanda
                String ask = line;
                
                // Guarda la riga successiva per verificare se è "-----"
                reader.mark(1000);  // segna la posizione corrente
                String peek = reader.readLine();
                StringBuilder text = new StringBuilder();
                boolean first = true;
                if (peek != null && peek.equals("-----")) {
                    // Domanda complessa: leggi tutte le righe fino al prossimo "-----"
                    while ((line = reader.readLine()) != null && !line.equals("-----")) {
                    	if (!first) {
                            text.append("\n");
                        }
                        text.append(line);
                        first = false;
                    }
                } else {
                    // Domanda semplice: torna indietro se la riga non è "-----"
                    reader.reset();
                }
                
                // Leggi le opzioni e la risposta corretta
                String option1 = reader.readLine();
                String option2 = reader.readLine();
                String option3 = reader.readLine();
                String option4 = reader.readLine();
                String correctAnswer = reader.readLine();
                // Salta la riga vuota di separazione (se presente)
                reader.readLine();
                
                // Se option3 è vuota o nulla, si tratta di una domanda a due opzioni
                if (option3 == null || option3.trim().isEmpty()) {
                    Question question = new Question(ask, text.toString(), option1, option2, correctAnswer);
                    questions.add(question);
                } else {
                    Question question = new Question(ask, text.toString(), option1, option2, option3, option4, correctAnswer);
                    questions.add(question);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return questions;
    }

    public static List<CompletionQuestion> loadCompletionQuestions(String exerciseId, String difficulty, int level) {
        List<CompletionQuestion> questions = new ArrayList<>();
        String filePath = "resources/com/play/questions/" + exerciseId + "/" + difficulty + "/" + level + ".txt";
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Legge la prima parte della domanda (domanda semplice)
                String ask = line;
                
                // La riga successiva DEVE essere "-----"
                line = reader.readLine();
                
                // Legge la parte complessa della domanda fino a trovare il prossimo "-----"
                StringBuilder text = new StringBuilder();
                boolean first = true;
                while ((line = reader.readLine()) != null && !line.equals("-----")) {
                    if (!first) {
                        text.append("\n");
                    }
                    text.append(line);
                    first = false;
                }
                
                // Dopo il secondo "-----", la riga successiva è la risposta corretta
                String correctAnswer = reader.readLine();
                // Salta eventualmente una riga vuota
                reader.readLine();
                
                CompletionQuestion question = new CompletionQuestion(ask, text.toString(), correctAnswer);
                questions.add(question);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return questions;
    }

    public static void saveUserProgress(String username, String exerciseId, String difficulty, int level, int score, int seconds, int attempts, int total) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(PROGRESS_DIRECTORY + "/" + username + ".dat", true))) {
            writer.write(exerciseId + ";" + difficulty + ";" + level + ";" + score + ";" + seconds + ";" + attempts + ";" + total);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
            if (parts.length < 5) continue;
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
                        if (difficulty.equalsIgnoreCase("principiante"))
                            progressMap.put(exerciseId, new ProgressInfo("intermedio", 1));
                        else if (difficulty.equalsIgnoreCase("intermedio"))
                            progressMap.put(exerciseId, new ProgressInfo("esperto", 1));
                        else
                            progressMap.put(exerciseId, new ProgressInfo("esperto", 3)); // massimo per esperto
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
                                if (difficulty.equalsIgnoreCase("principiante"))
                                    progressMap.put(exerciseId, new ProgressInfo("intermedio", 1));
                                else if (difficulty.equalsIgnoreCase("intermedio"))
                                    progressMap.put(exerciseId, new ProgressInfo("esperto", 1));
                                else
                                    progressMap.put(exerciseId, new ProgressInfo("esperto", 3));
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
