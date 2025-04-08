package com.play.model;

public class Question {
    private String question;
    private String text;
    private String option1;
    private String option2;
    private String option3;
    private String option4;
    private String correctAnswer;
    private int optionCount;

    public Question(String question, String text, String option1, String option2, String correctAnswer) {
        this.question = question;
        this.text = text;
        this.option1 = option1;
        this.option2 = option2;
        this.correctAnswer = correctAnswer;
        this.optionCount = 2;
    }

    public Question(String question, String text, String option1, String option2, String option3, String option4, String correctAnswer) {
        this.question = question;
        this.text = text;
        this.option1 = option1;
        this.option2 = option2;
        this.option3 = option3;
        this.option4 = option4;
        this.correctAnswer = correctAnswer;
        this.optionCount = 4;
    }

    public String getQuestion() {
        return question;
    }

    public String getText() {
        return text;
    }

    public String getOption1() {
        return option1;
    }

    public String getOption2() {
        return option2;
    }

    public String getOption3() {
        return option3;
    }

    public String getOption4() {
        return option4;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public int getOptionCount() {
        return optionCount;
    }
}
