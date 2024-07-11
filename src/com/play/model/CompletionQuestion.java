package com.play.model;

public class CompletionQuestion {
    private String sentence;
    private String correctAnswer;

    public CompletionQuestion(String sentence, String correctAnswer) {
        this.sentence = sentence;
        this.correctAnswer = correctAnswer;
    }

    public String getSentence() {
        return sentence;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }
}
