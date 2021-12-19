package com.company.quiz;

public enum UserCommands {
    Repeat("повтор"),
    Help("/help"),
    Score("/score"),
    Start("/start"),
    Question("вопрос"),
    ThrowUp("сдаюсь");
    public final String text;

    UserCommands(String text) {

        this.text = text;
    }
}
