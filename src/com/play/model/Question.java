package com.play.model;

/**
 * Classe che rappresenta una domanda a scelta multipla.
 * Estende CompletionQuestion per ereditare i campi base comuni.
 * Aggiunge il supporto per 2 o 4 opzioni di risposta.
 * 
 * @author Play Team
 * @version 1.0
 */
public class Question extends CompletionQuestion {
    private final String option1;
    private final String option2;
    private final String option3;
    private final String option4;
    private final int optionCount;

    /**
     * Costruttore per una domanda con 2 opzioni di risposta.
     * 
     * @param question Il testo della domanda
     * @param text Il testo aggiuntivo della domanda
     * @param option1 La prima opzione di risposta
     * @param option2 La seconda opzione di risposta
     * @param correctAnswer La risposta corretta
     * @throws IllegalArgumentException se uno dei parametri è null o vuoto
     */
    public Question(String question, String text, String option1, String option2, String correctAnswer) {
        super(question, text, correctAnswer);
        validateTwoOptions(option1, option2);
        validateCorrectAnswer(option1, option2, correctAnswer);
        
        this.option1 = option1;
        this.option2 = option2;
        this.option3 = null;
        this.option4 = null;
        this.optionCount = 2;
    }

    /**
     * Costruttore per una domanda con 4 opzioni di risposta.
     * 
     * @param question Il testo della domanda
     * @param text Il testo aggiuntivo della domanda
     * @param option1 La prima opzione di risposta
     * @param option2 La seconda opzione di risposta
     * @param option3 La terza opzione di risposta
     * @param option4 La quarta opzione di risposta
     * @param correctAnswer La risposta corretta
     * @throws IllegalArgumentException se uno dei parametri è null o vuoto
     */
    public Question(String question, String text, String option1, String option2, String option3, String option4, String correctAnswer) {
        super(question, text, correctAnswer);
        validateFourOptions(option1, option2, option3, option4, correctAnswer);
        
        this.option1 = option1;
        this.option2 = option2;
        this.option3 = option3;
        this.option4 = option4;
        this.optionCount = 4;
    }

    /**
     * Valida i parametri per una domanda con 2 opzioni.
     */
    private void validateTwoOptions(String option1, String option2) {
        if (option1 == null || option1.trim().isEmpty()) {
            throw new IllegalArgumentException("La prima opzione non può essere vuota");
        }
        if (option2 == null || option2.trim().isEmpty()) {
            throw new IllegalArgumentException("La seconda opzione non può essere vuota");
        }
    }

    /**
     * Valida i parametri per una domanda con 4 opzioni.
     */
    private void validateFourOptions(String option1, String option2, String option3, String option4, String correctAnswer) {
        validateTwoOptions(option1, option2);
        
        if (option3 == null || option3.trim().isEmpty()) {
            throw new IllegalArgumentException("La terza opzione non può essere vuota");
        }
        if (option4 == null || option4.trim().isEmpty()) {
            throw new IllegalArgumentException("La quarta opzione non può essere vuota");
        }
        if (!correctAnswer.equals(option1) && !correctAnswer.equals(option2) && 
            !correctAnswer.equals(option3) && !correctAnswer.equals(option4)) {
            throw new IllegalArgumentException("La risposta corretta deve corrispondere a una delle opzioni");
        }
    }

    /**
     * Valida la risposta corretta per una domanda con 2 opzioni.
     */
    private void validateCorrectAnswer(String option1, String option2, String correctAnswer) {
        if (!correctAnswer.equals(option1) && !correctAnswer.equals(option2)) {
            throw new IllegalArgumentException("La risposta corretta deve corrispondere a una delle opzioni");
        }
    }

    /**
     * Ottiene la prima opzione di risposta.
     */
    public String getOption1() {
        return option1;
    }

    /**
     * Ottiene la seconda opzione di risposta.
     */
    public String getOption2() {
        return option2;
    }

    /**
     * Ottiene la terza opzione di risposta.
     */
    public String getOption3() {
        return option3;
    }

    /**
     * Ottiene la quarta opzione di risposta.
     */
    public String getOption4() {
        return option4;
    }

    /**
     * Ottiene il numero di opzioni della domanda.
     */
    public int getOptionCount() {
        return optionCount;
    }

    /**
     * Verifica se la risposta fornita è corretta.
     * Per le domande a scelta multipla, il confronto è case-sensitive.
     * 
     * @param answer La risposta da verificare
     * @return true se la risposta è corretta, false altrimenti
     * @throws IllegalArgumentException se la risposta è null o vuota
     */
    @Override
    public boolean isCorrectAnswer(String answer) {
        if (answer == null || answer.trim().isEmpty()) {
            throw new IllegalArgumentException("La risposta non può essere vuota");
        }
        return answer.equals(correctAnswer);
    }
}
