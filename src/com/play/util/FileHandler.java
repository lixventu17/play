package com.play.util;

import com.play.model.User;
import com.play.model.Question;
import com.play.model.CompletionQuestion;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

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
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(USER_DIRECTORY + "/" + user.getUsername() + ".dat"))) {
            oos.writeObject(user);
        }
    }

    public static User loadUser(String username) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(USER_DIRECTORY + "/" + username + ".dat"))) {
            return (User) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Question> loadQuestions(String exerciseId, String difficulty) {
        List<Question> questions = new ArrayList<>();
        String filePath = "resources/com/play/questions/" + exerciseId + "_" + difficulty + ".txt";
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String questionText = line;
                String option1 = reader.readLine();
                String option2 = reader.readLine();
                String option3 = reader.readLine();
                String option4 = reader.readLine();
                String correctAnswer = reader.readLine();
                reader.readLine(); // per saltare la riga vuota tra le domande
                
                Question question = new Question(questionText, option1, option2, option3, option4, correctAnswer);
                questions.add(question);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return questions;
    }

    public static List<CompletionQuestion> loadCompletionQuestions(String exerciseId, String difficulty) {
        List<CompletionQuestion> questions = new ArrayList<>();
        String fileName = "resources/com/play/questions/" + exerciseId + "_" + difficulty + ".txt";
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    questions.add(new CompletionQuestion(parts[0], parts[1]));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return questions;
    }

    public static void saveUserProgress(String username, String exerciseId, String difficulty, int score, int seconds) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(PROGRESS_DIRECTORY + "/" + username + ".dat", true))) {
            writer.write(exerciseId + ";" + difficulty + ";" + score + ";" + seconds);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<String> loadUserProgress(String username) {
        List<String> progress = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(PROGRESS_DIRECTORY + "/" + username + ".dat"))) {
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
        List<String> progress = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(PROGRESS_DIRECTORY + "/" + username + ".dat"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts[2].equals("10")) {
                	if (parts[1].equals("principiante")) {
                        progress.add(parts[0] + ";intermedio;");
                	}
                	else if (parts[1].equals("intermedio")) {
                        progress.add(parts[0] + ";esperto");
                	}
                	else {
                		progress.add(parts[0] + ";esperto");
                		completed = true;
                	}
                }
                else {
                	progress.add(parts[0] + ";" + parts[1]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return progress;
    }

    public static boolean exerciseCompleted() {
    	return completed;
    }
}