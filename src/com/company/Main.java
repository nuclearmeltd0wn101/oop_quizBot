package com.company;

public class Main {
    public static void main(String[] args) {
        var env = System.getenv();
        var token = env.getOrDefault("tgBotToken", "");
        if ("".equals(token)) {
            System.err.println("Telegram Bot Token not submitted");
            return;
        }

        var questions = QuestionsParser.fromTextFile("quiz_questions.txt", "\\*");
        var db = new QuizDBRAM();
        db.setUserName(382139175,"aaa");
        db.setUserName(683545096,"bbbaaaaaaa");
        for (var i=0;i<10;i++)
        {
            db.scoreIncrement(-528640224,382139175);
            db.scoreIncrement(-528640224,683545096);
        }
        var botLogic = new QuizLogic(questions, db);
        var bot = new TelegramBotWrapper(botLogic, token);
        bot.run();
    }
}
