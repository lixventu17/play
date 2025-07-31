package com.play.model;

/**
 * Classe base che rappresenta una domanda di completamento.
 * Contiene i campi base comuni a tutti i tipi di domande.
 * 
 * @author Play Team
 * @version 1.0
 */
public class CompletionQuestion {
    protected final String question;
    protected final String text;
    protected final String correctAnswer;

    /**
     * Costruttore per creare una nuova domanda di completamento.
     * 
     * @param question Il testo della domanda
     * @param text Il testo da completare
     * @param correctAnswer La risposta corretta
     * @throws IllegalArgumentException se uno dei parametri è null o vuoto
     */
    public CompletionQuestion(String question, String text, String correctAnswer) {
        validateParameters(question, text, correctAnswer);
        
        this.question = question;
        this.text = text;
        this.correctAnswer = correctAnswer;
    }

    /**
     * Valida i parametri del costruttore.
     * 
     * @param question Il testo della domanda
     * @param text Il testo da completare
     * @param correctAnswer La risposta corretta
     * @throws IllegalArgumentException se uno dei parametri è null o vuoto
     */
    protected void validateParameters(String question, String text, String correctAnswer) {
        if (question == null || question.trim().isEmpty()) {
            throw new IllegalArgumentException("Il testo della domanda non può essere vuoto");
        }
        if (text == null) {
            throw new IllegalArgumentException("Il testo da completare non può essere null");
        }
        if (correctAnswer == null || correctAnswer.trim().isEmpty()) {
            throw new IllegalArgumentException("La risposta corretta non può essere vuota");
        }
    }

    /**
     * Ottiene il testo della domanda.
     * 
     * @return Il testo della domanda
     */
    public String getQuestion() {
        return question;
    }

    /**
     * Ottiene il testo da completare.
     * 
     * @return Il testo da completare
     */
    public String getText() {
        return text;
    }

    /**
     * Ottiene la risposta corretta.
     * 
     * @return La risposta corretta
     */
    public String getCorrectAnswer() {
        return correctAnswer;
    }

    /**
     * Verifica se la risposta fornita è corretta.
     * 
     * @param answer La risposta da verificare
     * @return true se la risposta è corretta, false altrimenti
     * @throws IllegalArgumentException se la risposta è null o vuota
     */
    public boolean isCorrectAnswer(String answer) {
        if (answer == null || answer.trim().isEmpty()) {
            throw new IllegalArgumentException("La risposta non può essere vuota");
        }
        return answer.trim().equalsIgnoreCase(correctAnswer.trim());
    }
}
