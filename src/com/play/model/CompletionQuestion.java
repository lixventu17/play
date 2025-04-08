package com.play.model;

public class CompletionQuestion {
    private String question;
    private String text;
    private String correctAnswer;

    public CompletionQuestion(String question, String text, String correctAnswer) {
        this.question = question;
        this.text = text;
        this.correctAnswer = correctAnswer;
    }

    public String getQuestion() {
        return question;
    }

    public String getText() {
        return text;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }
}
