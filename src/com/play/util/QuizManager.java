package com.play.util;

import java.io.*;
import java.util.*;

import com.play.model.Question;

public class QuizManager {

    private List<Question> questions;
    private int currentQuestionIndex;
    private int score;
    private String exerciseId;
    private String difficulty;

    public QuizManager(String exerciseId, String difficulty) {
        questions = new ArrayList<>();
        loadQuestions(exerciseId, difficulty);
        currentQuestionIndex = 0;
        score = 0;
    }

    private void loadQuestions(String exerciseId, String difficulty) {
        String fileName = "resources/com/play/questions/" + exerciseId + "_" + difficulty + ".txt";
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
            	if (line.startsWith("Q: ")) {
            		String questionText = line.substring(3);
                    List<String> options = new ArrayList<>();
                    String correctOption = "";
                    for (int i = 0; i <= 4; i++) {
                    	if (i == 4) {
                    		correctOption = reader.readLine().substring(3);
                    	}
                    	else {
                            options.add(reader.readLine().substring(3));
                    	}
                    }
                    questions.add(new Question(questionText, options, correctOption));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Question getCurrentQuestion() {
        return questions.get(currentQuestionIndex);
    }

    public boolean hasNextQuestion() {
        return currentQuestionIndex < questions.size() - 1;
    }

    public Question nextQuestion() {
        currentQuestionIndex++;
        return getCurrentQuestion();
    }

    public void submitAnswer(String answer) {
        if (questions.get(currentQuestionIndex).isCorrect(answer)) {
            score++;
        }
    }

    public int getScore() {
        return score;
    }

    public int getTotalQuestions() {
        return questions.size();
    }

    public void saveProgress(boolean completed) {
        Map<String, String> userProgress = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("resources/com/play/user_progress.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("=");
                userProgress.put(parts[0], parts[1]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (completed && difficulty.equals("principiante")) {
            userProgress.put(exerciseId, "intermedio");
        } else if (completed && difficulty.equals("intermedio")) {
            userProgress.put(exerciseId, "esperto");
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("resources/com/play/user_progress.txt"))) {
            for (Map.Entry<String, String> entry : userProgress.entrySet()) {
                writer.write(entry.getKey() + "=" + entry.getValue());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
