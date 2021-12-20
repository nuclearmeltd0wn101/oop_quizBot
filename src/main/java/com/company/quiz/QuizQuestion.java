package com.company.quiz;

import java.util.Locale;

public class QuizQuestion {
    public final String question;
    private final String answer;
    public final int id;

    public QuizQuestion(int id, String question, String answer) {
        if ((question == null) || (answer == null)
                || (question.length() == 0) || (answer.length() == 0))
            throw new IllegalArgumentException();

        this.question = question;
        this.answer = answer;
        this.id = id;
    }

    public String answerHintFirstLetter() {
        return answer.substring(0, 1);
    }

    public int answerHintLength() {
        return answer.length();
    }

    public boolean validateAnswer(String answerCandidate) {
        return answerCandidate.toLowerCase(Locale.ROOT)
                .equals(answer.toLowerCase(Locale.ROOT));
    }
}
