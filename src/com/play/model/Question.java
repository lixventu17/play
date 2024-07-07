package com.play.model;

import java.util.List;

public class Question {

    private String question;
    private List<String> options;
    private String correctAnswer;

    public Question(String question, List<String> options, String correctAnswer) {
        this.question = question;
        this.options = options;
        this.correctAnswer = correctAnswer;
    }

    public String getQuestion() {
        return question;
    }

    public List<String> getOptions() {
        return options;
    }

    public boolean isCorrect(String answer) {
        return correctAnswer.equals(answer);
    }
}
