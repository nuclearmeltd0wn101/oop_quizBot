package com.company;

public class Main {
    public static void main(String[] args) {
        var questions = QuestionsParser.fromTextFile("quiz_questions.txt", "\\*");
        var botLogic = new QuizLogic(questions);
        var bot = new ConsoleChatWrapper(botLogic);
        bot.run();
    }
}
