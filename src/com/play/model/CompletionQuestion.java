package com.play.model;

public class CompletionQuestion {
    private String questionText;
    private String correctAnswer;

    public CompletionQuestion(String questionText, String correctAnswer) {
        this.questionText = questionText;
        this.correctAnswer = correctAnswer;
    }

    public String getQuestionText() {
        return questionText;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }
}
