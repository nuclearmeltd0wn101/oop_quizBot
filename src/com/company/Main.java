package com.company;

public class Main {
    public static void main(String[] args) {
        var env = System.getenv();
        var token = env.getOrDefault("tgBotToken", "");
        var dbPath = env.getOrDefault("dbPath", "QuizBot.sqlite");
        if ("".equals(token)) {
            System.err.println("Telegram Bot Token not submitted");
            return;
        }

        var questions = QuestionsParser.fromTextFile("quiz_questions.txt", "\\*");
        var db = new QuizDBSQLite(dbPath);

        var botLogic = new QuizLogic(questions, db);
        var bot = new TelegramBotWrapper(botLogic, token);
        bot.run();
    }
}
