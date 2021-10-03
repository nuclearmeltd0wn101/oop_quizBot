package com.company;

public class Main {
    public static void main(String[] args) {
        var questionBase = new QuestionBase("quiz_questions.txt", "\\*");
        var botLogic = new QuizLogic(questionBase);
        var bot = new ConsoleChatWrapper(botLogic);
        bot.run();
    }
}
